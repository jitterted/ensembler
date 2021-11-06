package com.jitterted.mobreg.adapter.out.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class SendGridEmailer implements Emailer {

    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    @Override
    public void send(String subject, String body, Set<String> recipients) {
        Email from = new Email("mobreg@tedmyoung.com", "Mob Registration System (MobReg)"); // TODO: pull this into configuration
        Content content = new Content("text/html", body);

        Personalization personalization = new Personalization();
        personalization.addTo(from); // same as "To:" as the rest will be BCC'd
        recipients.forEach(recipient -> personalization.addBcc(new Email(recipient)));

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addPersonalization(personalization);
        mail.addContent(content);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        try {
            request.setBody(mail.build());
            Response response = sg.api(request);
        } catch (IOException e) {
        }
    }
}
