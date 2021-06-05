package com.jitterted.mobreg;

import com.jitterted.mobreg.adapter.out.file.FileHuddlePuddle;
import com.jitterted.mobreg.domain.HuddleRepository;
import com.jitterted.mobreg.domain.HuddleService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class MobRegApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobRegApplication.class, args);
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
