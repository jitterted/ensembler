package com.jitterted.mobreg.adapter.out.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class SendGridEmailer implements Emailer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendGridEmailer.class);

    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    @Override
    public void send(EmailToSend emailToSend) {
        Email from = new Email("mobreg@tedmyoung.com", "Ensembler.dev"); // TODO: pull this into configuration
        Content content = new Content("text/html", emailToSend.body());

        Personalization personalization = new Personalization();
        personalization.addTo(new Email(emailToSend.recipient()));

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(emailToSend.subject());
        mail.addPersonalization(personalization);
        mail.addContent(content);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        try {
            request.setBody(mail.build());
            Response response = sg.api(request);
            LOGGER.info("Emails sent via SendGrid, response={}, to emails={}", response.getStatusCode(), toEmailsAsStrings(personalization));
        } catch (IOException e) {
            LOGGER.warn("Exception when trying to send email via SendGrid. Request=" + request.getBody(), e);
        }
    }

    @NotNull
    private String toEmailsAsStrings(Personalization personalization) {
        return personalization.getTos()
                              .stream()
                              .map(Email::getEmail)
                              .map(email -> email.split("@")[0])
                              .collect(Collectors.joining(","));
    }
}
