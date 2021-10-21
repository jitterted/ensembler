package com.jitterted.mobreg.adapter.out.email;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.port.Notifier;
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
        throw new UnsupportedOperationException();
    }
}
