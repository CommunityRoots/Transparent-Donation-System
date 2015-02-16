package need;

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

    @Before
    public void setup(){
        //testing user model so clear all pre existing user data in database
        Ebean.delete(User.find.all());
        Ebean.delete(Need.find.all());
        //add data to database
        Ebean.save((List) Yaml.load("test-data.yml"));
    }

    @Test
    public void RetrieveNeedCorrelatingToEmail() {
        List<Need> food = Need.findByEmail("bob@gmail.com");
        assertNotNull(food);
        assertEquals("food", food.get(0).title);
        assertNotEquals(0,food.size());
    }

}