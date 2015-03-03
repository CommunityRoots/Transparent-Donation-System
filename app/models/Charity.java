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

    @OneToMany
    public List<User> members = new LinkedList<>();

    public Charity(String name){
        this.name = name;
    }

    public static Finder<Long, Charity> find = new Finder<Long,Charity>(Long.class, Charity.class);
}
