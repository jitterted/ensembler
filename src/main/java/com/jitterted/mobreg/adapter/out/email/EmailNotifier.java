package com.jitterted.mobreg.adapter.out.email;

import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Primary
@Component
public class EmailNotifier implements Notifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotifier.class);

    private final MemberService memberService;
    private final Emailer emailer;
    private final GoogleCalendarLinkCreator googleCalendarLinkCreator = new GoogleCalendarLinkCreator();

    @Autowired
    public EmailNotifier(MemberService memberService, Emailer emailer) {
        this.memberService = memberService;
        this.emailer = emailer;
    }

    @Override
    public int ensembleScheduled(String description, URI registrationLink) {
        Set<String> emails = memberService.findAll().stream()
                                          .map(Member::email)
                                          .filter(not(String::isEmpty))
                                          .collect(Collectors.toSet());
        String messageBody = """
                New Ensemble '%s' has been scheduled.
                <br/>
                Visit <a href="%s">MobReg</a> to register.
                """
                .formatted(description, registrationLink.toString());
        LOGGER.info("Sending New Ensemble '{}' emails to: {}", description, emails);
        emailer.send("Ensembler Notification: New Ensemble Scheduled", messageBody, emails);
        return 0;
    }

    @Override
    public void memberRegistered(Ensemble ensemble, Member member) {
        if (!member.hasEmail()) {
            LOGGER.info("Member does not have email: {}", member.firstName());
            return;
        }
        String body = """
                Hi %s,
                                           
                You have registered for the '%s', which happens on %s.
                Click <a href="%s">here</a> to join the Zoom. You can add this event to your Google Calendar
                by clicking <a href="%s">here</a>.
                """.formatted(member.firstName(),
                              ensemble.name(),
                              ensemble.startDateTime().toString(),
                              ensemble.zoomMeetingLink().toString(),
                              googleCalendarLinkCreator.createFor(ensemble));
        emailer.send("Ensembler Notification: Registration Confirmation",
                     body,
                     Set.of(member.email()));
    }

}
