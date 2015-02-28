package models;


import org.hibernate.validator.constraints.Email;
import play.db.ebean.Model;

import javax.annotation.Nullable;
import javax.persistence.*;
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

    public Donation(Need need,User donator, double amount){
        this.need = need;
        this.donator = donator;
        this.amount = amount;
    }

    public static Model.Finder<Long, Donation> find = new Model.Finder<Long,Donation>(Long.class, Donation.class);


    public static List<Donation> findByDonator(String email) {
        return Donation.find.where()
                .eq("donator.email", email)
                .findList();
    }

}
