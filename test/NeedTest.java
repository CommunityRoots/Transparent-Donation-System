package need;

import models.Need;
import models.User;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class NeedTest {

    @Test
    public void RetrieveNeedCorrelatingToEmail() {
        List<Need> food = Need.findByEmail("bob@gmail.com");
        assertNotNull(food);
        assertEquals("food", food.get(0).title);
        assertNotEquals(0,food.size());
    }

}
