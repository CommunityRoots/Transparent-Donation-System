package models;

import javax.persistence.*;
import javax.validation.Constraint;

import play.db.ebean.*;
import play.data.validation.*;

import java.lang.Integer;
import java.util.List;

@Entity
public class Need extends Model {

    Need(String title, int donatedAmount, int askAmount, String addedBy){
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
    @Constraints.Email
    public String addedBy;

    @Constraints.Required
    public String description;

    public static Finder<Integer, Need> find = new Finder<Integer,Need>(Integer.class, Need.class);

    public static List<Need> findByEmail(String email) {
        return Need.find.where()
                .eq("added_by", email)
                .findList();
    }

    public int progressPercentage(){
        return donatedAmount*100/askAmount;
    }
}
