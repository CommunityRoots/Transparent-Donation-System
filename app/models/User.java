package models;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;

@Entity
public class User extends Model {

    User(String email, String name, String password){
        this.email = email;
        this.name = name;
        this.password = password;
    }
    //play framework changes these to private and adds getters + setters on run
    @Id
    public Long id;

    @Constraints.Email
    public String email;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String password;

    public static Finder<Long, User> find = new Finder<Long,User>(Long.class, User.class);

    public static User authenticate(String email, String password) {
        return find.where().eq("email", email)
                .eq("password", password).findUnique();
    }
}
