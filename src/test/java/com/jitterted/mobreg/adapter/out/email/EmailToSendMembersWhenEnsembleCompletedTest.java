package com.jitterted.mobreg.adapter.out.email;

import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.TestEnsembleBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmailToSendMembersWhenEnsembleCompletedTest {

    // emails are only sent to Ensemble.acceptedMembers()
    @Test
    public void emailsOnlySentToAcceptedMembers() throws Exception {
        TestEnsembleBuilder ensembleBuilder = new TestEnsembleBuilder();
        Ensemble ensemble = ensembleBuilder.acceptedMemberWithEmailOf("accepted@example.com")
                                           .acceptedMemberWithEmailOf("accepted2@example.com")
                                           .declinedMemberWithEmailOf("declined@example.com")
                                           .build();
        SpyEmailer spyEmailer = new SpyEmailer();
        Notifier notifier = new EmailNotifier(ensembleBuilder.memberService(), spyEmailer);

        notifier.ensembleCompleted(ensemble);

        assertThat(spyEmailer.sentEmails())
                .extracting(EmailToSend::recipient)
                .containsOnly("accepted@example.com", "accepted2@example.com");
    }

//    @Test
//    public void emailContentIsFormattedCorrectly() throws Exception {
//
//    }

}