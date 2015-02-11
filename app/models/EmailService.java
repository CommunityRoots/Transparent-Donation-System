package models;


import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;

public class EmailService {

    Email email = new Email();

    public void sendEmail(String name, String sendEmail, String subject, String content){
        email.setFrom("CommunityRoots.net <info@communityroots.net>");
        email.addTo(name+"<"+sendEmail+">");
        email.setSubject(subject);
        email.setBodyText(content);
        MailerPlugin.send(email);
    }
}
