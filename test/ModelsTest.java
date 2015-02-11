package models;


import com.avaje.ebean.Ebean;
import org.junit.*;
import static org.junit.Assert.*;

import play.libs.Yaml;
import play.test.WithApplication;
import java.util.List;

public class ModelsTest extends WithApplication {

    //run before tests
    @Before
    public void setup(){
        //testing user model so clear all pre existing user data in database
        Ebean.delete(User.find.all());
        //add data to database
        Ebean.save((List) Yaml.load("test-data.yml"));
    }

    @Test
    public void userAuthenticateTest() {
        assertNotNull(User.authenticate("bob@gmail.com", "secret"));
        assertNull(User.authenticate("bob@gmail.com", "badpassword"));
        assertNull(User.authenticate("tom@gmail.com", "secret"));
    }

    @Test
    public void createAndRetrieveUser() {
        User bob = User.find.where().eq("email", "bob@gmail.com").findUnique();
        assertNotNull(bob);
        assertEquals("Bob", bob.firstName);
    }
}
