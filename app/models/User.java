package models;

import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.net.MalformedURLException;


@Entity
public class User extends Model {

    public User(String email, String firstName, String lastName, String password){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = 4; //default
        this.validated = false;
        this.joined = new Date();
    }
    //play framework changes these to private and adds getters + setters on run

    @Id
    public long id;

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

    public boolean validated;

    /*
        User Roles:
        1 - admin adder
        2 - community admin
        3 - volunteer
        4 - Donator account
     */
    public int role;

    @ManyToOne
    public Charity charity;

    public Date joined;

    public Date lastLogin;

    public static Finder<String, User> find = new Finder<String,User>(String.class, User.class);

    public static User findByEmail(String email){
        return User.find.where().eq("email",email).findUnique();
    }

    public static User authenticate(String email, String password) {
        if(findByEmail(email) != null){
            if(BCrypt.checkpw(password, findByEmail(email).password)){
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
        this.update();
    }

    public void changeRole(int role){
        this.role = role;
        this.save();
    }

    public void setCharity(Charity charity){
        this.charity = charity;
        this.save();
    }

    public void setLastLogin(){
        this.lastLogin = new Date();
        this.save();
    }

    public void validate() {
        this.validated = true;
        this.save();
    }

}
