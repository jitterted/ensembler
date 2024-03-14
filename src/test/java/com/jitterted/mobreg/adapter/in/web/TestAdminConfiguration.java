package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.application.DefaultMemberService;
import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.DummyNotifier;
import com.jitterted.mobreg.application.port.DummyVideoConferenceScheduler;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import com.jitterted.mobreg.domain.Member;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestAdminConfiguration {

    @Bean
    public Notifier testNotifier() {
        return new DummyNotifier();
    }

    @Bean
    public VideoConferenceScheduler testVideoConferenceScheduler() {
        return new DummyVideoConferenceScheduler();
    }

    @Bean
    public EnsembleRepository ensembleRepository() {
        return new InMemoryEnsembleRepository();
    }

    @Bean
    public MemberRepository memberRepository() {
        return new InMemoryMemberRepository();
    }

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
    public EnsembleTimerHolder createEnsembleTimerHolder(EnsembleRepository ensembleRepository) {
        // TODO: enable real broadcaster when feature is ready
        return EnsembleTimerHolder.createNull(ensembleRepository);
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
}