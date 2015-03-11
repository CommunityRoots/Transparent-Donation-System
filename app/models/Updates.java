package models;

import javax.persistence.*;
import play.db.ebean.*;
import play.data.validation.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import Services.EmailService;


@Entity
public class Updates extends Model {

    @Id
    public long id;

    //public String title;

    @Constraints.Required
    public String message;

    public Date dateAdded;

    @ManyToOne
    public Need need;

    public Updates(String message,Need need){
        //this.title = title;
        this.message = message;
        this.emailUpdate(message);
        this.need = need;
        dateAdded = new Date();
        this.save();
    }

    public void emailUpdate(String message) {
        List<Donation> donations = Donation.find.where()
                .eq("need", need)
                .findList();
        List<String> emails = new ArrayList<>();
        for (Donation donation : donations) {
            if(donation.notify) {
                emails.add(donation.donator.email);
            }
        }
        if (!emails.isEmpty()) {
            EmailService emailService = new EmailService();
            emailService.sendMultipleEmails(emails, "An update about your donation",
                    "The need " + need.title + " has an update: " + message);
        }
    }

    public static Finder<Long, Updates> find = new Finder<Long,Updates>(Long.class, Updates.class);
}
