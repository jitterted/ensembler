package com.jitterted.mobreg.adapter.out.websocket;

import com.jitterted.mobreg.domain.EnsembleTimer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EventToHtmlTransformerTest {

    @Test
    void htmlForFinishedStateIsHornAudioElement() {
        String html = EventToHtmlTransformer.htmlFor(EnsembleTimer.TimerEvent.FINISHED);

        assertThat(html)
                .isEqualTo("""
                           <audio id="audio-container"
                                  hx-swap-oob="innerHTML"
                                  src="/horn.wav"
                                  autoplay>
                           </audio>
                           """);
    }

    @Test
    void htmlForPausedEventIsPausedAudioElement() {
        String html = EventToHtmlTransformer.htmlFor(EnsembleTimer.TimerEvent.PAUSED);

        assertThat(html)
                .isEqualTo("""
                           <audio id="audio-container"
                                  hx-swap-oob="innerHTML"
                                  src="/arp_down.wav"
                                  autoplay>
                           </audio>
                           """);
    }
    @Test
    void htmlForResumeEventIsResumedAudioElement() {
        String html = EventToHtmlTransformer.htmlFor(EnsembleTimer.TimerEvent.RESUMED);

        assertThat(html)
                .isEqualTo("""
                           <audio id="audio-container"
                                  hx-swap-oob="innerHTML"
                                  src="/arp_up.wav"
                                  autoplay>
                           </audio>
                           """);
    }
}