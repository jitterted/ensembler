package com.jitterted.mobreg.adapter.out.websocket;

import com.jitterted.mobreg.domain.EnsembleTimer;

import java.util.Map;

public class EventToHtmlTransformer {
    private static final Map<EnsembleTimer.TimerEvent, String> soundMap =
            Map.of(
                    EnsembleTimer.TimerEvent.FINISHED, "horn.wav",
                    EnsembleTimer.TimerEvent.PAUSED, "arp_down.wav",
                    EnsembleTimer.TimerEvent.RESUMED, "arp_up.wav"
            );

    public static String htmlFor(EnsembleTimer.TimerEvent timerEvent) {
        return """
               <audio id="audio-container"
                      hx-swap-oob="innerHTML"
                      src="/%s"
                      autoplay>
               </audio>
               """.formatted(soundMap.get(timerEvent));
    }
}
