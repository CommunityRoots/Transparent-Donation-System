package models;

import com.avaje.ebean.Ebean;
import models.*;
import org.junit.*;
import static org.junit.Assert.*;

import play.Application;
import play.GlobalSettings;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ModelsTest extends WithApplication {

    FakeApplication fakeApp = Helpers.fakeApplication();

    FakeApplication fakeAppWithGlobal = fakeApplication(new GlobalSettings() {
        @Override
        public void onStart(Application app) {
            System.out.println("Starting FakeApplication");
            }
        });

    FakeApplication fakeAppWithMemoryDb = fakeApplication(inMemoryDatabase("test"));

    @Test
    public void findById() {
        running(fakeApplication(inMemoryDatabase("test")), new Runnable() {
            public void run() {
                new User("bob@gmail.com", "Bob", "secret").save();
                assertNotNull(User.authenticate("bob@gmail.com", "secret"));
                assertNull(User.authenticate("bob@gmail.com", "badpassword"));
                assertNull(User.authenticate("tom@gmail.com", "secret"));
            }
        });
    }
}
