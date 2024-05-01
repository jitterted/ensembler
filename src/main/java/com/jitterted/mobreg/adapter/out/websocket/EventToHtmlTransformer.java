package com.jitterted.mobreg.adapter.out.websocket;

import com.jitterted.mobreg.domain.EnsembleTimer;

import java.util.Map;

public class EventToHtmlTransformer {
    private static Map<EnsembleTimer.TimerEvent, String> soundMap =
            Map.of(EnsembleTimer.TimerEvent.FINISHED, "horn.wav");

    public static String htmlFor(EnsembleTimer.TimerEvent timerEvent) {
        String html = """
                      <audio id="audio-container"
                             hx-swap-oob="innerHTML"
                             src="/%s"
                             autoplay>
                      </audio>
                      """.formatted(soundMap.get(timerEvent));
        return html;
    }
}
