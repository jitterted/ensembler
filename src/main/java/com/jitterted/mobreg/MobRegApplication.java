package com.jitterted.mobreg;

import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import com.jitterted.mobreg.domain.Member;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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

    @Bean
    public MemberService memberService(MemberRepository memberRepository) {
        return new MemberService(memberRepository);
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

    // disabled until can figure out MobTi.me new API
//    @Bean
    public WebSocketSession createMobTimerWebSocketSession() throws ExecutionException, InterruptedException {
        try {
            StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
            ListenableFuture<WebSocketSession> webSocketSessionListenableFuture =
                    standardWebSocketClient.doHandshake(new TextWebSocketHandler(),
                                                        "wss://mobti.me/jitterted00");
            return webSocketSessionListenableFuture.completable().get();
        } catch (InterruptedException | ExecutionException e) {
            return new WebSocketSession() {
                @Override
                public String getId() {
                    return null;
                }

                @Override
                public URI getUri() {
                    return null;
                }

                @Override
                public HttpHeaders getHandshakeHeaders() {
                    return null;
                }

                @Override
                public Map<String, Object> getAttributes() {
                    return null;
                }

                @Override
                public Principal getPrincipal() {
                    return null;
                }

                @Override
                public InetSocketAddress getLocalAddress() {
                    return null;
                }

                @Override
                public InetSocketAddress getRemoteAddress() {
                    return null;
                }

                @Override
                public String getAcceptedProtocol() {
                    return null;
                }

                @Override
                public void setTextMessageSizeLimit(int messageSizeLimit) {

                }

                @Override
                public int getTextMessageSizeLimit() {
                    return 0;
                }

                @Override
                public void setBinaryMessageSizeLimit(int messageSizeLimit) {

                }

                @Override
                public int getBinaryMessageSizeLimit() {
                    return 0;
                }

                @Override
                public List<WebSocketExtension> getExtensions() {
                    return null;
                }

                @Override
                public void sendMessage(WebSocketMessage<?> message) throws IOException {

                }

                @Override
                public boolean isOpen() {
                    return false;
                }

                @Override
                public void close() throws IOException {

                }

                @Override
                public void close(CloseStatus status) throws IOException {

                }
            };
        }
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
