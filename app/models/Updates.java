package models;

import Services.EmailService;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
public class Updates extends Model {

    @Id
    public long id;

    @Constraints.Required
    public String message;

    public Date dateAdded;

    @ManyToOne
    public Need need;

    public Updates(String message,Need need){
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
        System.out.println("" + donations.size());
        List<String> emails = new ArrayList<>();
        for (Donation donation : donations) {
            if(donation.notify&&!emails.contains(donation.donator.email)) {
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
