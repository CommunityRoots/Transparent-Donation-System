package Unit.models;

import models.Donation;
import models.Need;
import models.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class DonationTest {

    @Test
    public void findNeedDonatedTo(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                Donation donation = Donation.find.all().get(0);
                assertNotNull(donation.need);
            }
        });
    }

    @Test
    public void createDonationTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                User user = User.findByEmail("bob@gmail.com");
                Need need = Need.findByAdded(user.id).get(0);
                Donation donation = new Donation(need,user,10);
                assertNotNull(donation);
                assertEquals(10,donation.amount,0);
            }
        });
    }

}
