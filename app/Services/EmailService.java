package Services;


import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;

import java.util.List;

public class EmailService {

    private Email email = new Email();

    public void sendEmail(String name, String sendEmail, String subject, String content){
        email.setFrom("CommunityRoots.net <info@communityroots.net>");
        email.addTo(name+"<"+sendEmail+">");
        email.setSubject(subject);
        email.setBodyHtml(content);
        MailerPlugin.send(email);
    }

    public void sendMultipleEmails(List<String> emails, String subject, String content){
        email.setFrom("CommunityRoots.net <info@communityroots.net>");
        email.setSubject(subject);
        email.setBodyHtml(content);
        for(String sendEmail: emails) {
            email.addTo("<" + sendEmail + ">");
            MailerPlugin.send(email);
        }
    }
}
