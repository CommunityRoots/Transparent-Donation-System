package models;


import org.hibernate.validator.constraints.Email;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;


@Entity
public class Donation extends Model {

    @Id
    public long id;
    @Email
    @ManyToOne
    public User donator;
    public double amount;
    @ManyToOne
    public Need need;
    public Date date;
    public boolean notify = true;

    public Donation(Need need,User donator, double amount){
        this.need = need;
        this.donator = donator;
        this.amount = amount;
        this.date = new Date();
        this.save();
    }

    public static Model.Finder<Long, Donation> find = new Model.Finder<Long,Donation>(Long.class, Donation.class);

    public static List<Donation> findByDonator(String email) {
        return Donation.find.where()
                .eq("donator.email", email)
                .findList();
    }

    public void unsub() {
        this.notify = false;
        this.save();
    }

    public void sub() {
        this.notify = true;
        this.save();
    }

    public boolean isSubbed() {
        return notify;
    }

}
