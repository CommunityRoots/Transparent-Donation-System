package Unit.models;

import models.Need;
import models.Updates;
import models.User;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class UpdatesTest {

    @Test
    public void addUpdateToNeed(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                User user = User.findByEmail("bob@gmail.com");
                Need need = new Need("TestNeed","test description", user, 23.50,"ireland",10,user.charity, Need.Category.Family);
                Updates updates = new Updates("This is a test update", need);
                assertNotEquals(need.getUpdates().size(), 0);
            }
        });
    }
}
