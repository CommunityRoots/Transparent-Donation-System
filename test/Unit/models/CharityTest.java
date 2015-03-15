package Unit.models;

import models.Charity;
import models.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class CharityTest {

    @Test
    public void addCharityTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                int numCharities = Charity.find.findRowCount();
                Charity charity = new Charity("TestCharity", "www.test.com", "Dummy Charity fo test");
                charity.save();
                int numCharitiesAfterAdd = Charity.find.findRowCount();
                assertNotEquals(numCharities, numCharitiesAfterAdd);
            }
        });

    }

    @Test
    public void editCharityTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                User user = User.findByEmail("bob@gmail.com");
                Charity charity = user.charity;
                charity.editCharity("Test", "www.test2.com", "This is an edit test");
                assertEquals(charity.name, "Test");
                assertEquals(charity.description, "This is an edit test");
                assertEquals(charity.website, "www.test2.com");
            }
        });
    }
}
