package models;

import javax.persistence.*;


import play.db.ebean.*;
import play.data.validation.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@Entity
public class Need extends Model {

    public Need(String title, double askAmount, User addedBy, Category category){
        this.title = title;
        this.donatedAmount = 0;
        this.askAmount = askAmount;
        this.addedBy = addedBy;
        this.dateAdded = new Date();
        this.category = category;
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

    public Category category;

    @Constraints.Min(1)
    @Constraints.Max(10)
    public int urgency;

    public Date dateAdded;

    //1 = closed 0 = open
    public int closed;
    
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
        this.closed =0;
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
            closed =1;
        }
        this.save();
        donation.save();
    }

    public void addUpdate(Need need, String title,String message){
        Updates update = new Updates(title,message,need);
        update.save();
        updates.add(update);
    }

    public void editNeed(String title, String description, String location, double amount, int urgency){
        this.title =title;
        this.description = description;
        this.location =location;
        this.askAmount =amount;
        this.urgency = urgency;
        this.save();
    }

    public boolean deleteNeed(){
        if(donatedAmount==0){
            this.delete();
            return true;
        }
        return false;
    }
}
