package com.jitterted.mobreg;

import com.jitterted.mobreg.adapter.out.CompositeBroadcaster;
import com.jitterted.mobreg.adapter.out.clock.ScheduledExecutorSecondsTicker;
import com.jitterted.mobreg.adapter.out.jdbc.EnsembleJdbcRepository;
import com.jitterted.mobreg.application.DefaultMemberService;
import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.Broadcaster;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Configuration
public class EnsemblerConfiguration {

    @Bean
    public MemberService memberService(MemberRepository memberRepository) {
        return new DefaultMemberService(memberRepository);
    }

    @Bean
    public EnsembleService createEnsembleService(EnsembleRepository ensembleRepository,
                                                 MemberRepository memberRepository,
                                                 Notifier notifier,
                                                 VideoConferenceScheduler videoConferenceScheduler) {
        return new EnsembleService(ensembleRepository,
                                   memberRepository,
                                   notifier,
                                   videoConferenceScheduler);
    }

    @Bean
    public Broadcaster compositeBroadcaster(List<Broadcaster> broadcasters) {
        return new CompositeBroadcaster(broadcasters);
    }

    @Bean
    public EnsembleTimerHolder ensembleTimerHolder(EnsembleRepository ensembleRepository,
                                                   MemberRepository memberRepository,
                                                   @Qualifier("compositeBroadcaster") Broadcaster broadcaster) {
        return new EnsembleTimerHolder(ensembleRepository,
                                       memberRepository,
                                       broadcaster,
                                       new ScheduledExecutorSecondsTicker());
    }

    // TODO: remove this once member registration works
    @Bean
    public CommandLineRunner commandLineRunner(MemberService memberService, MemberRepository memberRepository) {
        return _ -> {
            if (memberRepository.findByGithubUsername("tedyoung")
                                .isEmpty()) {
                memberService.save(new Member("Ted", "tedyoung", "ROLE_USER", "ROLE_MEMBER", "ROLE_ADMIN"));
            }
        };
    }

    @Bean
    @Profile("local")
    public CommandLineRunner createInProgressEnsemble(MemberService memberService,
                                                      MemberRepository memberRepository,
                                                      EnsembleService ensembleService,
                                                      EnsembleJdbcRepository jdbcRepository) {
        return _ -> {
            if (jdbcRepository.countAllByNameContainingIgnoreCase("Ensemble Timer Demo") > 2) {
                System.err.println("Too many timer demo ensembles, deleting them.");
                jdbcRepository.deleteWhereNameEquals("Ensemble Timer Demo");
            }

            List<Member> allMembers = memberRepository.findAll();
            if (allMembers.size() < 5) {
                int numberOfMembersToCreate = 5 - allMembers.size();
                for (int i = 0; i < numberOfMembersToCreate; i++) {
                    Member member = new Member("Member " + i, "member" + i + "@" + LocalTime.now(), "ROLE_USER", "ROLE_MEMBER");
                    memberService.save(member);
                }
            }
            Ensemble ensemble = ensembleService.scheduleEnsemble("Ensemble Timer Demo",
                                                                 URI.create("https://zoom.us"),
                                                                 ZonedDateTime.now()
                                                                              .withZoneSameInstant(ZoneOffset.UTC)
                                                                              .minusMinutes(5));
            allMembers = memberRepository.findAll(); // get the 5 (or more) members from the repo and use the first 5
            allMembers.stream()
                      .limit(5)
                      .forEach(member -> ensembleService.joinAsParticipant(ensemble.getId(), member.getId()));
        };
    }
}