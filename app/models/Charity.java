package models;

import play.db.ebean.Model;

import javax.persistence.Id;
import java.util.ArrayList;

public class Charity extends Model {

    @Id
    public String name;


}
