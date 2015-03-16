package Unit.models;

import models.Charity;
import models.Need;
import models.User;
import java.util.List;
import org.junit.Test;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class NeedTest extends WithApplication {

    @Test
    public void RetrieveNeedCorrelatingToAdder() {
        User user = User.findByEmail("bob@gmail.com");
        List<Need> food = Need.findByAdded(user.id);
        assertNotNull(food);
        assertNotEquals(0,food.size());
    }

    @Test
    public void progressPercentageTest(){
        User user = User.findByEmail("bob@gmail.com");
        Charity charity = Charity.find.all().get(0);
        Need need =  new Need("food","need food",user,50,"Ireland",10,charity, Need.Category.Family);
        assertEquals(0, need.progressPercentage(),0);
    }

}