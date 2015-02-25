package models;


import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;

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
