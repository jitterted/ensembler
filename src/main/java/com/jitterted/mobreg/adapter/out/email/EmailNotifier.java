package com.jitterted.mobreg.adapter.out.email;

import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Primary
@Component
public class EmailNotifier implements Notifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotifier.class);
    private static final DateTimeFormatter LONG_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("LLLL d, uuuu 'at' h:mma (zzz)");
    private static final DateTimeFormatter SHORT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' h:mma (zzz)");

    private final MemberService memberService;
    private final Emailer emailer;
    private final GoogleCalendarLinkCreator googleCalendarLinkCreator = new GoogleCalendarLinkCreator();

    @Autowired
    public EmailNotifier(MemberService memberService, Emailer emailer) {
        this.memberService = memberService;
        this.emailer = emailer;
    }

    @Override
    public int ensembleScheduled(Ensemble ensemble, URI registrationLink) {
        memberService.findAll().stream()
                     .map(member -> createEmailToSend(ensemble, registrationLink, member))
                     .forEach(emailer::send);
        return 0;
    }

    @Override
    public void memberAccepted(Ensemble ensemble, Member member) {
        if (!member.hasEmail()) {
            LOGGER.info("Member does not have email: {}", member.firstName());
            return;
        }

        ZonedDateTime startDateTimeInMemberTimeZone = startDateTimeInMemberTimeZone(ensemble, member.timeZone());

        String body = """
                Hi %s,
                                           
                You have registered for '%s', which happens on %s.
                Click <a href="%s">here</a> to join the Zoom. You can add this event to your Google Calendar
                by clicking <a href="%s">here</a>.
                """.formatted(member.firstName(),
                              ensemble.name(),
                              LONG_DATE_TIME_FORMATTER.format(startDateTimeInMemberTimeZone),
                              ensemble.zoomMeetingLink().toString(),
                              googleCalendarLinkCreator.createFor(ensemble));
        emailer.send(
                new EmailToSend("Ensembler Notification: Registration Confirmation", body, member.email()));
    }

    @Override
    public void ensembleCompleted(Ensemble ensemble) {
        ensemble.acceptedMembers()
                .map(memberService::findById)
                .filter(Member::hasEmail)
                .map(member -> emailForCompletedEnsemble(ensemble, member))
                .forEach(emailer::send);
    }

    @NotNull
    private EmailToSend emailForCompletedEnsemble(Ensemble ensemble, Member member) {
        return new EmailToSend("Ensembler Notification: Ensemble Completed",
                               bodyForCompletedEnsemble(ensemble, member),
                               member.email());
    }

    private String bodyForCompletedEnsemble(Ensemble ensemble, Member member) {
        final String ensembleCompletedBodyTemplate = """
                                                     Hi %s,
                                                     
                                                     Ensemble '%s' has been completed.
                                                     
                                                     The <a href="%s">video recording</a> is now available.
                                                     """;
        return ensembleCompletedBodyTemplate.formatted(member.firstName(), ensemble.name(), ensemble.recordingLink());
    }

    @NotNull
    private EmailToSend createEmailToSend(Ensemble ensemble, URI registrationLink, Member member) {
        String subject = createSubjectWith(ensemble, member.timeZone());
        String messageBody = createBodyWith(ensemble, registrationLink, member.timeZone());

        return new EmailToSend(subject, messageBody, member.email());
    }

    @NotNull
    private String createBodyWith(Ensemble ensemble, URI registrationLink, ZoneId zoneId) {
        return """
                New Ensemble '%s' has been scheduled for %s.
                <br/>
                Visit <a href="%s">MobReg</a> to register.
                """
                .formatted(ensemble.name(),
                           startDateTimeInMemberTimeZone(ensemble, zoneId).format(LONG_DATE_TIME_FORMATTER),
                           registrationLink.toString());
    }

    @NotNull
    private String createSubjectWith(Ensemble ensemble, ZoneId zoneId) {
        ZonedDateTime zonedDateTime = startDateTimeInMemberTimeZone(ensemble, zoneId);
        String localizedDateTime = zonedDateTime.format(SHORT_DATE_TIME_FORMATTER);
        return "Ensembler: New Ensemble Scheduled for " + localizedDateTime;
    }

    @NotNull
    private ZonedDateTime startDateTimeInMemberTimeZone(Ensemble ensemble, ZoneId zoneId) {
        return ensemble.startDateTime().withZoneSameInstant(zoneId);
    }

}
