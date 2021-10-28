package com.jitterted.mobreg;

import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.port.HuddleRepository;
import com.jitterted.mobreg.domain.port.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.port.MemberRepository;
import com.jitterted.mobreg.domain.port.Notifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MobRegApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobRegApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty("mobreg.repository.inmemory")
    public HuddleRepository huddleRepository() {
        return new InMemoryHuddleRepository();
    }

    @Bean
    @ConditionalOnProperty("mobreg.repository.inmemory")
    public MemberRepository memberRepository() {
        return new InMemoryMemberRepository();
    }

    @Bean
    public MemberService memberService(MemberRepository memberRepository) {
        return new MemberService(memberRepository);
    }


    @Bean
    public HuddleService createHuddleService(HuddleRepository huddleRepository, MemberRepository memberRepository, Notifier notifier) {
        return new HuddleService(huddleRepository, memberRepository, notifier);
    }

    @Bean
    public WebSocketSession createMobTimerWebSocketSession() throws ExecutionException, InterruptedException {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        ListenableFuture<WebSocketSession> webSocketSessionListenableFuture =
                standardWebSocketClient.doHandshake(new TextWebSocketHandler(),
                                                    "wss://mobti.me/jitterted00");
        return webSocketSessionListenableFuture.completable().get();
    }

    // TODO: remove this once member registration works
    @Bean
    public CommandLineRunner commandLineRunner(MemberService memberService, MemberRepository memberRepository) {
        return args -> {
            if (memberRepository.findByGithubUsername("tedyoung").isEmpty()) {
                memberService.save(new Member("Ted", "tedyoung", "ROLE_USER", "ROLE_MEMBER", "ROLE_ADMIN"));
                memberService.save(new Member("Ted", "tedyoung", "ROLE_USER", "ROLE_MEMBER", "ROLE_ADMIN"));
                memberService.save(new Member("Ted", "tedyoung", "ROLE_USER", "ROLE_MEMBER", "ROLE_ADMIN"));
                memberService.save(new Member("Ted", "tedyoung", "ROLE_USER", "ROLE_MEMBER", "ROLE_ADMIN"));
                memberService.save(new Member("Ted", "tedyoung", "ROLE_USER", "ROLE_MEMBER", "ROLE_ADMIN"));
            }
        };
    }

}
