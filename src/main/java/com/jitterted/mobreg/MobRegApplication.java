package com.jitterted.mobreg;

import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MobRegApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobRegApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty("mobreg.repository.inmemory")
    public EnsembleRepository ensembleRepository() {
        return new InMemoryEnsembleRepository();
    }

    @Bean
    @ConditionalOnProperty("mobreg.repository.inmemory")
    public MemberRepository memberRepository() {
        return new InMemoryMemberRepository();
    }


}
