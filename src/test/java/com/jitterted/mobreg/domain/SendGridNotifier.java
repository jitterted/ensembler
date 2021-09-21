package com.jitterted.mobreg.domain;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import java.io.IOException;

public class SendGridNotifier {
    private static final String SEND_GRID_TOKEN = "";

    public static void main(String[] args) throws IOException {
        Email from = new Email("ted@tedmyoung.com");
        String subject = "Ensembler Notification: New Video Recording Available";
        Email to = new Email("ted@tedmyoung.com");
        Content content = new Content("text/html", "and easy to do anywhere, even with Java");

        Personalization personalization = new Personalization();
        personalization.addTo(new Email(""));

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addPersonalization(personalization);
        mail.addContent(content);

        SendGrid sg = new SendGrid(SEND_GRID_TOKEN);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        System.out.println("Status code: " + response.getStatusCode());
        System.out.println("Body: '" + response.getBody() + "'");
        System.out.println("Headers: " + response.getHeaders());
    }
}
