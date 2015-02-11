package need;

import models.Need;
import models.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NeedTest {

    @Test
    public void RetrieveNeedCorrelatingToEmail() {
        Need food = Need.find.where().eq("email", "bob@gmail.com").findUnique();
        assertNotNull(bob);
        assertEquals("Bob", bob.firstName);
    }

}
