package models;

import Services.EmailService;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Entity
public class Token extends Model {
    //token only lasts for certain amount of days
    private static final int EXPIRATION = 1;

    @Id
    public String token;

    @Constraints.Required
    @Formats.NonEmpty
    public String email;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreated;

    public static Model.Finder<String, Token> find = new Finder(String.class, Token.class);

    public static Token findByToken(String token) {
        return find.where().eq("token", token).findUnique();
    }

    public boolean isExpired() {
        return dateCreated != null && Calendar.getInstance().getTime().after(expirationTime());
    }

    private Date expirationTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateCreated);
        cal.add(Calendar.DATE, EXPIRATION);
        return cal.getTime();
    }

    public static Token getNewToken(User user) {
        Token token = new Token();
        token.token = UUID.randomUUID().toString();
        token.email = user.email;
        token.dateCreated = new Date();
        token.save();
        return token;
    }

    public static void sendMail(User user) throws MalformedURLException {
        String mail = user.email;
        String name = user.firstName;
        String subject = "Forgot Password";
        Token token = getNewToken(user);
        String urlString = "http://communityroots.net/reset/"+token.token;
        URL url = new URL(urlString);
        String content = "Link to change your password "+ url;
        EmailService emailService = new EmailService();
        emailService.sendEmail(name,mail,subject,content);
    }

    public static void checkIfTokensAreValid(){
        //loop through tokens checking if they are valid
        List<Token> tokenList = Token.find.all();
        for (Token token : tokenList){
            if(token.isExpired()){
                token.delete();
            }
        }
    }

}
