package com.jitterted.mobreg;

import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.time.ZonedDateTime;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MobRegApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobRegApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty("mobreg.repository.inmemory")
    public EnsembleRepository ensembleRepository(MemberRepository memberRepository) {
        InMemoryEnsembleRepository inMemoryEnsembleRepository = new InMemoryEnsembleRepository();
        inMemoryEnsembleRepository.save(new Ensemble("Created by App Startup: Future Event", URI.create("https://zoom.us"), ZonedDateTime.now().plusHours(1)));
        Ensemble pendingComplete = new Ensemble("Created by App Startup: Pending Complete", URI.create("https://zoom.us"), ZonedDateTime.now().minusDays(1));
        Member member = memberRepository.save(new Member("Ted", "tedyoung", "ROLE_USER", "ROLE_MEMBER", "ROLE_ADMIN"));
        pendingComplete.acceptedBy(member.getId());
        inMemoryEnsembleRepository.save(pendingComplete);
        return inMemoryEnsembleRepository;
    }

    @Bean
    @ConditionalOnProperty("mobreg.repository.inmemory")
    public MemberRepository memberRepository() {
        return new InMemoryMemberRepository();
    }


}
