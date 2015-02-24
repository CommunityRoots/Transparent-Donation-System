package models;

import javax.persistence.*;
import org.joda.time.*;
import org.joda.time.DateTime;
import play.data.format.Formats;
import play.db.ebean.*;
import play.data.validation.*;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Need extends Model {

    public Need(String title, double askAmount, String addedBy){
        this.title = title;
        this.donatedAmount = 0;
        this.askAmount = askAmount;
        this.addedBy = addedBy;
        this.dateAdded = DateTime.now();
    }

    public Need() {}
    //play framework changes these to private and adds getters + setters on run
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public long id;

    @Constraints.Required
    @Constraints.MaxLength(20)
    public String title;

    @Constraints.Required
    public double donatedAmount;

    @Constraints.Required
    public double askAmount;

    @Constraints.Required
    @Constraints.Email
    public String addedBy;

    @Constraints.Required
    @Constraints.MaxLength(600)
    public String description;

    @Formats.DateTime(pattern="dd/MM/yyyy")
    public DateTime dateAdded;

    public static Finder<Long, Need> find = new Finder<Long,Need>(Long.class, Need.class);

    public static List<Need> findByEmail(String email) {
        return Need.find.where()
                .eq("added_by", email)
                .findList();
    }

    public static List<Need> findByDonatedTo(String email){
        List<Donation> donationsByUserToNeed = Donation.find.where()
                .eq("donator_email",email)
                .findList();
        LinkedList<Need> needsDonatedToByUser = new LinkedList<>();
        for(Donation donation : donationsByUserToNeed){
            needsDonatedToByUser.add(Need.findById(donation.needId));
        }
        return needsDonatedToByUser;

    }

    public static Need findById(long id){
        return Need.find.where()
                .eq("id",id)
                .findUnique();
    }

    public double progressPercentage(){
        return donatedAmount*100/askAmount;
    }

    public void addNeed(String title,String description, String user, double amount ) {
        this.title =title;
        this.description =description;
        this.addedBy = user;
        this.askAmount =amount;
        this.dateAdded = DateTime.now();
        this.save();
    }

    public long daysSinceNeedAdded(){
        return ((Days.daysBetween(dateAdded,DateTime.now()).getDays()));
    }
}
