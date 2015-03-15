package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Charity extends Model {

    @Id
    public long id;

    public String name;

    public String description;

    public String website;

    @OneToMany
    public List<User> members = new LinkedList<>();

    public Charity(String name, String description, String website){
        this.name = name;
        this.website =website;
        this.description =description;
    }

    public static Finder<Long, Charity> find = new Finder<Long,Charity>(Long.class, Charity.class);

    public void editCharity(String name, String description, String website){
        this.name = name;
        this.website = website;
        this.description = description;
        this.save();
    }
}
