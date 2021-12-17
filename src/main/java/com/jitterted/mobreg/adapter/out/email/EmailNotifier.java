package com.jitterted.mobreg.adapter.out.email;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
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
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

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
        Set<String> emails = memberService.findAll().stream()
                                          .map(Member::email)
                                          .filter(not(String::isEmpty))
                                          .collect(Collectors.toSet());
        String messageBody = """
                New Ensemble '%s' has been scheduled.
                <br/>
                Visit <a href="%s">MobReg</a> to register.
                """
                .formatted(ensemble.name(), registrationLink.toString());
        String subject = createSubjectWith(ensemble, DateTimeFormatting.PACIFIC_TIME_ZONE_ID);

        LOGGER.info("Sending New Ensemble '{}' emails to: {}", ensemble.name(), emails);
        emailer.send(subject, messageBody, emails);
        return 0;
    }

    @NotNull
    private String createSubjectWith(Ensemble ensemble, ZoneId zoneId) {
        ZonedDateTime zonedDateTime = startDateTimeInMemberTimeZone(ensemble, zoneId);
        String localizedDateTime = zonedDateTime.format(SHORT_DATE_TIME_FORMATTER);
        return "Ensembler: New Ensemble Scheduled for " + localizedDateTime;
    }

    @Override
    public void memberRegistered(Ensemble ensemble, Member member) {
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
        emailer.send("Ensembler Notification: Registration Confirmation",
                     body,
                     Set.of(member.email()));
    }

    @NotNull
    private ZonedDateTime startDateTimeInMemberTimeZone(Ensemble ensemble, ZoneId zoneId) {
        return ensemble.startDateTime().withZoneSameInstant(zoneId);
    }

}
