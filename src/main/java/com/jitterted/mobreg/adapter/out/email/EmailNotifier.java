package com.jitterted.mobreg.adapter.out.email;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.port.Notifier;
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

    @Autowired
    public EmailNotifier(MemberService memberService, Emailer emailer) {
        this.memberService = memberService;
        this.emailer = emailer;
    }

    @Override
    public int newHuddleOpened(String description, URI registrationLink) {
        Set<String> emails = memberService.findAll().stream()
                                          .map(Member::email)
                                          .filter(not(String::isEmpty))
                                          .collect(Collectors.toSet());
        String messageBody = """
                New Ensemble '%s' has been scheduled.
                Visit %s to register.
                """
                .formatted(description, registrationLink.toString());
        emailer.send("Ensembler Notification: New Ensemble Scheduled", messageBody, emails);
        return 0;
    }

    @Override
    public void memberRegistered(Huddle huddle, Member member) {
        if (!member.hasEmail()) {
            LOGGER.info("Member does not have email: {}", member.firstName());
            return;
        }
        String body = """
                Hi %s,
                                           
                You have registered for the '%s', which happens on %s.
                The Zoom link is %s and you can add this event to your Google Calendar
                by clicking on this link: %s.
                """.formatted(member.firstName(),
                              huddle.name(),
                              huddle.startDateTime().toString(),
                              huddle.zoomMeetingLink().toString(),
                              "https://calendar.google.com/calendar/render");
        emailer.send("Ensembler Notification: Registration Confirmation",
                     body,
                     Set.of(member.email()));
    }

}
