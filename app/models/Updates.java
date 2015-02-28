package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Updates extends Model {

    @Id
    public long id;

    public String title;

    public String message;

    public Date dateAdded;

    @ManyToOne
    public Need need;

    public Updates(String title, String message,Need need){
        this.title = title;
        this.message = message;
        this.need = need;
        dateAdded = new Date();
    }
}
