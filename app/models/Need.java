package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@Entity
public class Need extends Model {

    public Need(String title,String description, User user, double amount ,String location, int urgency, Charity charity, Category category) {
        this.title =title;
        this.description =description;
        this.addedBy = user;
        this.askAmount =amount;
        this.dateAdded = new Date();
        this.location = location;
        this.urgency = urgency;
        this.charity = charity;
        this.category=category;
        this.closed =false;
        this.save();
    }

    public Need() {}

    public enum Category {
        Family, Education
    }
    //play framework changes these to private and adds getters + setters on run
    @Id
    public long id;

    @Constraints.Required
    @Constraints.MaxLength(20)
    public String title;

    @Constraints.Required
    public double donatedAmount;

    @Constraints.Required
    public double askAmount;

    @ManyToOne
    public User addedBy;

    @Constraints.Required
    @Constraints.MaxLength(600)
    public String description;

    @ManyToOne
    public Charity charity;
    public String location;
    public String fullName;

    public boolean paidToCharity;

    public Category category;

    @Constraints.Min(1)
    @Constraints.Max(10)
    public int urgency;

    public Date dateAdded;
    public Date dateClosed;
    public Date datePaidToCharity;

    public boolean closed;
    
    @OneToMany
    public List<Donation> donations = new LinkedList<>();

    @OneToMany
    public List<Updates> updates = new LinkedList<>();

    public static Finder<Long, Need> find = new Finder<Long,Need>(Long.class, Need.class);

    public static List<Need> findByDonatedTo(long id){
        List<Donation> donationsByUserToNeed = Donation.find.where()
                .eq("donator_id",id)
                .findList();
        LinkedList<Need> needsDonatedToByUser = new LinkedList<>();
        for(Donation donation : donationsByUserToNeed){
            needsDonatedToByUser.add(Need.find.byId(donation.need.id));
        }
        return needsDonatedToByUser;

    }

    public static List<Need> findByAdded(long id) {
        return Need.find.where().eq("added_by_id",id).findList();
    }

    public int progressPercentage(){
        return (int) Math.round(donatedAmount*100/askAmount);
    }

    public void addNeed(String title,String description, User user, double amount ,String location, int urgency, Charity charity, Category category) {
        this.title =title;
        this.description =description;
        this.addedBy = user;
        this.askAmount =amount;
        this.dateAdded = new Date();
        this.location = location;
        this.urgency = urgency;
        this.charity = charity;
        this.category=category;
        this.closed =false;
        this.save();
    }

    public long daysSinceNeedAdded(){
        Date today = new Date();
        return ( (today.getTime() - dateAdded.getTime()) / (1000 * 60 * 60 * 24) );
    }

    public void addDonation(Need needId,User userId, double amount){
        Donation donation = new Donation(needId,userId,amount);
        donations.add(donation);
        donatedAmount +=amount;
        if(donatedAmount >= askAmount){
            closed =true;
            dateClosed = new Date();
            Updates updates = new Updates("The need has received the full amount of money asked for. Thank you.",this);
        }
        this.save();
        donation.save();
    }


    public List<Updates> getUpdates(){
        List<Updates> updates =  Updates.find.where()
                .eq("need", this)
                .orderBy("dateAdded desc")
                .findList();
        return updates;
    }


    public void editNeed(String title, String description, String location, double amount, int urgency, Category category){
        this.title =title;
        this.description = description;
        this.location =location;
        this.askAmount =amount;
        this.urgency = urgency;
        this.category = category;
        this.save();
    }

    public boolean deleteNeed(){
        if(donatedAmount==0){
            this.delete();
            return true;
        }
        return false;
    }

    public String formattedDate(Date date){
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

    public static List<Need> needsToBePaidOut(){
        return Need.find.where().eq("closed",1).eq("paidToCharity",0).findList();
    }

    public void markAsPaidToCharity(){
        this.paidToCharity = true;
        this.datePaidToCharity = new Date();
        Updates update = new Updates("€"+donatedAmount+" has been sent to "+charity.name+" to fulfil this need.",this);
        this.save();
    }

    public void markAsClosed(){
        this.closed=true;
        this.save();
    }

    public static int numNeedsToBePaidOut(){
        return Need.find.where().eq("closed",1).eq("paidToCharity",0).findRowCount();
    }
}
