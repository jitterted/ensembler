package com.jitterted.mobreg.adapter.out.email;

import com.jitterted.mobreg.application.TestMemberBuilder;
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
        TestMemberBuilder memberBuilder = new TestMemberBuilder();
        memberBuilder.withEmail("name@example.com")
                     .withTimezone("America/Los_Angeles")
                     .buildAndSave();
        Ensemble ensemble = new Ensemble("Ensemble #314", URI.create("https://zoom.us"), ZonedDateTime.of(2021, 12, 3, 17, 0, 0, 0, ZoneOffset.UTC));
        SpyEmailer spyEmailer = new SpyEmailer();
        Notifier notifier = new EmailNotifier(memberBuilder.memberService(), spyEmailer);

        notifier.ensembleScheduled(ensemble, URI.create("https://ensembler.dev/"));

        assertThat(spyEmailer.sentEmails())
                .containsExactly(new EmailToSend(
                        "Ensembler: New Ensemble Scheduled for 2021-12-03 at 9:00AM (PST)",
                        """
                           New Ensemble 'Ensemble #314' has been scheduled for December 3, 2021 at 9:00AM (PST).
                           <br/>
                           Visit <a href="https://ensembler.dev/">MobReg</a> to register.
                           """,
                        "name@example.com"));
    }

    @Test
    public void membersInDifferentTimeZonesGetCustomizedEnsembleScheduledEmail() throws Exception {
        TestMemberBuilder memberBuilder = new TestMemberBuilder();
        memberBuilder.withEmail("pst@example.com").withTimezone("America/Los_Angeles").buildAndSave();
        memberBuilder.withEmail("est@example.com").withTimezone("America/New_York").buildAndSave();
        Ensemble ensemble = new Ensemble("Ensemble #1217", URI.create("https://zoom.us"), ZonedDateTime.of(2021, 12, 17, 21, 0, 0, 0, ZoneOffset.UTC));
        SpyEmailer spyEmailer = new SpyEmailer();
        Notifier notifier = new EmailNotifier(memberBuilder.memberService(), spyEmailer);

        notifier.ensembleScheduled(ensemble, URI.create("https://ensembler.dev/"));

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
                                      New Ensemble 'Ensemble #1217' has been scheduled for December 17, 2021 at 1:00PM (PST).
                                      <br/>
                                      Visit <a href="https://ensembler.dev/">MobReg</a> to register.
                                      ""","""
                                      New Ensemble 'Ensemble #1217' has been scheduled for December 17, 2021 at 4:00PM (EST).
                                      <br/>
                                      Visit <a href="https://ensembler.dev/">MobReg</a> to register.
                                      """
                              );
    }

}