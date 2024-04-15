package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.TestEnsembleServiceBuilder;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.ZonedDateTimeFactory;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class InProgressEnsembleViewTest {

    // appears as In-Progress only if NOW is after start time, NOW is before end time, only if member (POV) is REGISTERED

    @Test
    void hasAllInformationFiledIn() throws Exception {
        Ensemble ensemble = new Ensemble("in-progress",
                                         new URI("https://zoom.us/inprogress"),
                                         ZonedDateTimeFactory.zoneDateTimeUtc(2024, 2, 1, 11));
        ensemble.setId(EnsembleId.of(378));
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(ensemble)
                .withThreeParticipants("Participant 1", "Participant 2", "Participant 3")
                .saveMemberAsSpectator("Spectator 1");

        EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(
                builder.ensembleRepository(),
                builder.memberRepository());

        InProgressEnsembleView inProgressEnsembleView =
                InProgressEnsembleView.from(ensemble,
                                            builder.memberService(),
                                            ensembleTimerHolder);

        assertThat(inProgressEnsembleView)
                .isEqualTo(new InProgressEnsembleView(
                        "in-progress",
                        "https://zoom.us/inprogress",
                        "11:00 AM", // hour = 11
                        "/member/ensemble-timer/378",
                        false,
                        List.of("Participant 1", "Participant 2", "Participant 3"),
                        List.of("Spectator 1")));
    }

}