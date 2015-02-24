package models;


import org.hibernate.validator.constraints.Email;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "donation")
public class Donation {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public long id;
    public long needId;
    @Email
    public String donatorEmail;
    public double amount;

    public Donation(long needId,String donatorEmail, double amount){
        this.needId = needId;
        this.donatorEmail= donatorEmail;
        this.amount = amount;
    }

    public static Model.Finder<Long, Donation> find = new Model.Finder<Long,Donation>(Long.class, Donation.class);

    public static Need findById(int id){
        return Need.find.where()
                .eq("id",id)
                .findUnique();
    }

    public static List<Donation> findByDonatorEmail(String email) {
        return Donation.find.where()
                .eq("donatorEmail", email)
                .findList();
    }
}
