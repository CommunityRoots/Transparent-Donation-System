package models;

import play.db.ebean.Model;

import javax.persistence.Id;
import java.util.Date;

public class Update extends Model {

    @Id
    public long id;

    public String title;

    public String message;

    public Date dateAdded;

    public Need need;
}
