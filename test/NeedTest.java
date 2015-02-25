package models;

import models.Donation;
import models.Need;
import models.User;
import java.util.List;
import com.avaje.ebean.Ebean;
import org.junit.Before;
import org.junit.Test;
import play.libs.Yaml;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class NeedTest extends WithApplication {

    @Test
    public void RetrieveNeedCorrelatingToEmail() {
        List<Need> food = Need.findByEmail("bob@gmail.com");
        assertNotNull(food);
        assertNotEquals(0,food.size());
    }

    @Test
    public void progressPercentageTest(){
        Need need =  new Need("food",50,User.find.byId("bob@hotmail.com"));
        assertEquals(0, need.progressPercentage());
    }

}