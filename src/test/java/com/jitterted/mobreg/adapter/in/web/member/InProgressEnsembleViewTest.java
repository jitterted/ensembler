package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.TestMemberBuilder;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
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
        TestMemberBuilder memberBuilder = new TestMemberBuilder();
        Member participant1 = memberBuilder.withFirstName("Participant 1").buildAndSave();
        Member participant2 = memberBuilder.withFirstName("Participant 2").buildAndSave();
        Member spectator1 = memberBuilder.withFirstName("Spectator 1").buildAndSave();
        MemberService memberService = memberBuilder.memberService();
        ensemble.joinAsParticipant(participant1.getId());
        ensemble.joinAsParticipant(participant2.getId());
        ensemble.joinAsSpectator(spectator1.getId());

        InProgressEnsembleView inProgressEnsembleView = InProgressEnsembleView.from(ensemble, memberService);

        assertThat(inProgressEnsembleView)
                .isEqualTo(new InProgressEnsembleView("in-progress",
                                                      "https://zoom.us/inprogress",
                                                      "11:00 AM", // hour = 11
                                                      List.of("Participant 1", "Participant 2"),
                                                      List.of("Spectator 1")));
    }


    // this test makes no sense, since the presentation doesn't decide whether or not an individual member sees a specific ensemble
    // that's decided by the EnsembleService (Application Layer)
//        @Test
//        void startTimeInThePastEndTimeInTheFutureMemberIsNotRegisteredThenIsNotInProgress() {
//        }

    // Member won't even see an Ensemble from the past unless they had registered anyway
//        @Test
//        void endTimeIsInThePastMemberRegisteredIsNotInProgress() {
//
//        }


}