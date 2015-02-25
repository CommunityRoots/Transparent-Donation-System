package models;


import com.avaje.ebean.Ebean;
import org.junit.*;
import static org.junit.Assert.*;

import play.db.ebean.Model;
import play.libs.Yaml;
import play.test.WithApplication;
import java.util.List;

public class UserTest extends WithApplication {


    @Test
    public void userAuthenticateTest() {
        assertNotNull(User.authenticate("bob@gmail.com", "secret"));
        assertNull(User.authenticate("bob@gmail.com", "badpassword"));
        assertNull(User.authenticate("harry@gmail.com", "secret"));
    }

    @Test
    public void createAndRetrieveUser() {
        User bob = User.find.where().eq("email", "bob@gmail.com").findUnique();
        assertNotNull(bob);
        assertEquals("Bob", bob.firstName);
    }
}
