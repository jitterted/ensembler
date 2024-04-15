package com.jitterted.mobreg;

import com.jitterted.mobreg.adapter.out.clock.ScheduledExecutorSecondsTicker;
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
import com.jitterted.mobreg.domain.MemberId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

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
    public EnsembleTimerHolder ensembleTimerHolder(EnsembleRepository ensembleRepository, MemberRepository memberRepository, Broadcaster broadcaster) {
        return new EnsembleTimerHolder(ensembleRepository,
                                       memberRepository,
                                       broadcaster,
                                       new ScheduledExecutorSecondsTicker());
    }

    // TODO: remove this once member registration works
    @Bean
    public CommandLineRunner commandLineRunner(MemberService memberService, MemberRepository memberRepository) {
        return args -> {
            if (memberRepository.findByGithubUsername("tedyoung").isEmpty()) {
                memberService.save(new Member("Ted", "tedyoung", "ROLE_USER", "ROLE_MEMBER", "ROLE_ADMIN"));
            }
        };
    }

    @Bean
    @Profile("local")
    public CommandLineRunner createInProgressEnsemble(EnsembleService ensembleService) {
        return args -> {
            Ensemble ensemble = ensembleService.scheduleEnsemble("In Progress Ensemble",
                                                                 URI.create("https://zoom.us"),
                                                                 ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).minusMinutes(5));
            ensembleService.joinAsParticipant(ensemble.getId(), MemberId.of(1));
            ensembleService.joinAsParticipant(ensemble.getId(), MemberId.of(2));
            ensembleService.joinAsParticipant(ensemble.getId(), MemberId.of(3));
        };
    }
}