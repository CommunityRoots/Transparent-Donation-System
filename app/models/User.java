package models;

import javax.persistence.*;

import org.mindrot.jbcrypt.BCrypt;
import play.db.ebean.*;
import play.data.validation.*;

import java.util.Date;


@Entity
public class User extends Model {

    public User(String email, String firstName, String lastName, String password){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = 4; //default
        this.joined = new Date();
    }
    //play framework changes these to private and adds getters + setters on run

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

    /*
        User Roles:
        1 - admin adder
        2 - community admin
        3 - volunteer
        4 - donator account
     */
    public int role;

    public String charity;

    public Date joined;

    public Date lastLogin;

    public static Finder<String, User> find = new Finder<String,User>(String.class, User.class);

    public static User authenticate(String email, String password) {
        if(find.byId(email) != null){
            if(BCrypt.checkpw(password, find.byId(email).password)){
                return find.where().eq("email",email).findUnique();
            }
        }
        return null;
    }

    public void changePassword(String password) {
        this.password = BCrypt.hashpw(password,BCrypt.gensalt(12));
        this.save();
    }

    public void changeEmail(String email){
        this.email = email;
        this.save();
    }

    public void changeRole(int role){
        this.role = role;
        this.save();
    }

    public void setCharity(String charity){
        this.charity = charity;
        this.save();
    }
}
