package com.jitterted.mobreg.adapter.out.websocket;

import com.jitterted.mobreg.application.port.Broadcaster;
import com.jitterted.mobreg.domain.EnsembleTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketBroadcaster extends TextWebSocketHandler implements Broadcaster {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketBroadcaster.class);

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private Optional<TextMessage> currentTextMessage = Optional.empty();

    @Override
    public void sendCurrentTimer(EnsembleTimer ensembleTimer) {
        String html = TimerToHtmlTransformer.htmlFor(ensembleTimer);
        TextMessage textMessage = new TextMessage(html);
        currentTextMessage = Optional.of(textMessage);
        sessionMap.values().forEach(session -> sendMessageViaWebSocket(session, textMessage));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOGGER.info("Websocket connection established, session ID: {}, session remote address: {}", session.getId(), session.getRemoteAddress());
        sessionMap.put(session.getId(), session);
        currentTextMessage.ifPresent(message -> sendMessageViaWebSocket(session, message));
    }

    private void sendMessageViaWebSocket(WebSocketSession session, TextMessage message) {
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        LOGGER.info("Websocket Text Message received from session: {}, with message: {}", session.toString(), message.toString());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessionMap.remove(session.getId());
    }

}
