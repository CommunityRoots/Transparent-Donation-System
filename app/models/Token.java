package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class Token extends Model {
    //token only lasts for certain amount of time
    private static final int EXPIRATION = 1;

    @Id
    public String token;

    @Constraints.Required
    @Formats.NonEmpty
    public Long userId;

    @Constraints.Required
    @Formats.NonEmpty
    public String email;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    public static Model.Finder<String, Token> find = new Finder(String.class, Token.class);

    public static Token findByTokenAndType(String token) {
        return find.where().eq("token", token).findUnique();
    }

    public boolean isExpired() {
        return dateCreation != null && dateCreation.before(expirationTime());
    }

    private Date expirationTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, -EXPIRATION);
        return cal.getTime();
    }

    private static Token getNewToken(User user, String email) {
        Token token = new Token();
        token.token = UUID.randomUUID().toString();
        token.userId = user.id;
        token.email = email;
        token.save();
        return token;
    }

    public static void sendmail(User user) throws MalformedURLException {
            String mail = user.email;
            String name = user.firstName;
            String subject = "Forgot Password";
            Token token = getNewToken(user, user.email);
            String urlString = "http://communityroots.net/reset/"+token.token;
            URL url = new URL(urlString);
            String content = "Link to change your password "+ url;
            EmailService emailService = new EmailService();
            emailService.sendEmail(name,mail,subject,content);
    }

}
