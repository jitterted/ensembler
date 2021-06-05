package com.jitterted.mobreg.adapter.out.mobtimer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jitterted.mobreg.domain.Huddle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
public class MobTimerMessageSender {

    private final WebSocketSession webSocketSession;
    private final ObjectMapper objectMapper;

    @Autowired
    public MobTimerMessageSender(WebSocketSession webSocketSession, ObjectMapper objectMapper) {
        this.webSocketSession = webSocketSession;
        this.objectMapper = objectMapper;
    }

    public void updateParticipantsTo(Huddle huddle) {
        MobParticipantsDto mobParticipantsDto = MobParticipantsDto.from(huddle);

        try {
            String contents = objectMapper.writeValueAsString(mobParticipantsDto);
            send(contents);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    public void sendStandardTimerSettings() {
        String contents = """
                          {
                              "type": "settings:update",
                              "settings": {
                                "mobOrder": "Typist,Navigator",
                                "duration": 300000
                              }
                          }
                          """;
        send(contents);
    }

    private void send(String messageContent) {
        TextMessage mobTimerMessage = new TextMessage(messageContent);
        try {
            webSocketSession.sendMessage(mobTimerMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
