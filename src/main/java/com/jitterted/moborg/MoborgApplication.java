package com.jitterted.moborg;

import com.jitterted.moborg.adapter.out.file.FileHuddlePuddle;
import com.jitterted.moborg.domain.HuddleRepository;
import com.jitterted.moborg.domain.HuddleService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class MoborgApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoborgApplication.class, args);
    }

    @Bean
    public HuddleService createHuddleService() {
        return new HuddleService(createHuddleRepository());
    }

//  @Bean
//  public HuddleRepository createHuddleRepository() {
//    return new InMemoryHuddleRepository();
//  }

    @Bean
    public HuddleRepository createHuddleRepository() {
        return new FileHuddlePuddle();
    }

    @Bean
    public WebSocketSession createMobTimerWebSocketSession() throws ExecutionException, InterruptedException {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        ListenableFuture<WebSocketSession> webSocketSessionListenableFuture =
                standardWebSocketClient.doHandshake(new TextWebSocketHandler(),
                                                    "wss://mobti.me/jitterted");
        return webSocketSessionListenableFuture.completable().get();
    }

}
