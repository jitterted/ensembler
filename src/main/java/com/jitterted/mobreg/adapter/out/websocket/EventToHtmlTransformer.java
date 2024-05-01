package com.jitterted.mobreg.adapter.out.websocket;

import com.jitterted.mobreg.domain.EnsembleTimer;

public class EventToHtmlTransformer {
    public static String htmlFor(EnsembleTimer.TimerEvent timerEvent) {
        String html = """
                      <audio id="audio-container"
                             hx-swap-oob="innerHTML"
                             src="/horn.wav"
                             autoplay>
                      </audio>
                      """;
        return html;
    }
}
