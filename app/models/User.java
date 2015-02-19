package models;

import javax.persistence.*;

import org.mindrot.jbcrypt.BCrypt;
import play.db.ebean.*;
import play.data.validation.*;

@Entity
public class User extends Model {

    public User(String email, String firstName, String lastName, String password){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = "user"; //default
    }
    //play framework changes these to private and adds getters + setters on run
    public Long id;

    @Id
    @Constraints.Required
    public String email;

    @Constraints.Required
    public String firstName;

    @Constraints.Required
    public String lastName;

    @Constraints.MinLength(6)
    @Constraints.MaxLength(20)
    @Constraints.Required
    public String password;

    public String confirmationToken;

    public String role;

    public static Finder<String, User> find = new Finder<String,User>(String.class, User.class);

    public static User authenticate(String email, String password) {
        if(find.byId(email) != null){
            if(BCrypt.checkpw(password, find.byId(email).password)){
                return find.where().eq("email",email).findUnique();
            }
        }
        return null;
    }

    public static User findByConfirmationToken(String token) {
        return find.where().eq("confirmationToken", token).findUnique();
    }

    public void changePassword(String password) {
        this.password =  BCrypt.hashpw(password,BCrypt.gensalt(12));
        this.save();
    }

    public void changeEmail(String email){
        this.email = email;
        this.save();
    }
}
