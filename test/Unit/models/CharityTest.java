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
                Charity charity = new Charity("TestCharity", "Dummy Charity fo test", "www.svp.ie");
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
                charity.editCharity("Test", "This is an edit test", "www.test.com");
                assertEquals(charity.name, "Test");
                assertEquals(charity.description, "This is an edit test");
                assertEquals(charity.website, "www.test.com");
            }
        });
    }
}
