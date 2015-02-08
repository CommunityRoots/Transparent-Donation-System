package models;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;

@Entity
public class Need extends Model {

    Need(int donatedAmount, int askAmount, Long addedBy){
        this.donatedAmount = donatedAmount;
        this.askAmount = askAmount;
        this.addedBy = addedBy;
    }
    //play framework changes these to private and adds getters + setters on run
    @Id
    public Long id;

    @Constraints.Required
    public int donatedAmount;

    @Constraints.Required
    public int askAmount;

    @Constraints.Required
    public Long addedBy;

    //public static Finder<Long, Need> find = new Finder<Long,Need>(Long.class, Need.class);
/*
    public static User authenticate(String email, String password) {
        return find.where().eq("email", email)
                .eq("password", password).findUnique();
    }*/
}
