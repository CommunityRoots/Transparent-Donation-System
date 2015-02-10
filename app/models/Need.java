package models;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;

import java.lang.Integer;

@Entity
public class Need extends Model {

    Need(String title, int donatedAmount, int askAmount, int addedBy){
        this.title = title;
        this.donatedAmount = donatedAmount;
        this.askAmount = askAmount;
        this.addedBy = addedBy;
    }
    //play framework changes these to private and adds getters + setters on run
    @Id
    public int id;

    @Constraints.Required
    public String title;

    @Constraints.Required
    public int donatedAmount;

    @Constraints.Required
    public int askAmount;

    @Constraints.Required
    public int addedBy;

    public static Finder<Integer, Need> find = new Finder<Integer,Need>(Integer.class, Need.class);
/*
    public static User authenticate(String email, String password) {
        return find.where().eq("email", email)
                .eq("password", password).findUnique();
    }*/
}
