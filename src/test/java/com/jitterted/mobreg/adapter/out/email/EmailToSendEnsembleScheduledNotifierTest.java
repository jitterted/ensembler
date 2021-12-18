package com.jitterted.mobreg.adapter.out.email;

import com.jitterted.mobreg.application.MemberBuilder;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.domain.Ensemble;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

public class EmailToSendEnsembleScheduledNotifierTest {

    @Test
    public void memberWithEmailWhenEnsembleScheduledThenCustomEmailSentToMember() throws Exception {
        MemberBuilder memberBuilder = new MemberBuilder();
        memberBuilder.withEmail("name@example.com")
                     .withTimezone("America/Los_Angeles")
                     .build();
        Ensemble ensemble = new Ensemble("Ensemble #314", URI.create("https://zoom.us"), ZonedDateTime.of(2021, 12, 3, 17, 0, 0, 0, ZoneOffset.UTC));
        SpyEmailer spyEmailer = new SpyEmailer();
        Notifier notifier = new EmailNotifier(memberBuilder.memberService(), spyEmailer);

        notifier.ensembleScheduled(ensemble, URI.create("https://mobreg.herokuapp.com/"));

        assertThat(spyEmailer.sentEmails())
                .containsExactly(new EmailToSend(
                        "Ensembler: New Ensemble Scheduled for 2021-12-03 at 9:00AM (PST)",
                        """
                           New Ensemble 'Ensemble #314' has been scheduled.
                           <br/>
                           Visit <a href="https://mobreg.herokuapp.com/">MobReg</a> to register.
                           """,
                        "name@example.com"));
    }

    @Test
    public void membersInDifferentTimeZonesGetCustomizedEnsembleScheduledEmail() throws Exception {
        MemberBuilder memberBuilder = new MemberBuilder();
        memberBuilder.withEmail("pst@example.com").withTimezone("America/Los_Angeles").build();
        memberBuilder.withEmail("est@example.com").withTimezone("America/New_York").build();
        Ensemble ensemble = new Ensemble("Ensemble #1217", URI.create("https://zoom.us"), ZonedDateTime.of(2021, 12, 17, 21, 0, 0, 0, ZoneOffset.UTC));
        SpyEmailer spyEmailer = new SpyEmailer();
        Notifier notifier = new EmailNotifier(memberBuilder.memberService(), spyEmailer);

        notifier.ensembleScheduled(ensemble, URI.create("https://mobreg.herokuapp.com/"));

        assertThat(spyEmailer.sentEmails())
                .map(EmailToSend::recipient)
                .containsExactly("pst@example.com", "est@example.com");
        assertThat(spyEmailer.sentEmails())
                .map(EmailToSend::subject)
                .containsExactly("Ensembler: New Ensemble Scheduled for 2021-12-17 at 1:00PM (PST)",
                                 "Ensembler: New Ensemble Scheduled for 2021-12-17 at 4:00PM (EST)");
        assertThat(spyEmailer.sentEmails())
                .map(EmailToSend::body)
                .containsOnly("""
                                      New Ensemble 'Ensemble #1217' has been scheduled.
                                      <br/>
                                      Visit <a href="https://mobreg.herokuapp.com/">MobReg</a> to register.
                                      """);
    }

}