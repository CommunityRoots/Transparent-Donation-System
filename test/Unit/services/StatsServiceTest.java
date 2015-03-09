package Unit.services;

import Services.StatsService;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class StatsServiceTest {

    @Test
    public void getInstanceTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                //get singleton instance
                StatsService statsService = StatsService.getInstance();
                assertNotNull(statsService);
            }
        });
    }

    @Test
    public void generateStatsTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                StatsService statsService = StatsService.getInstance();
                assertNotNull(statsService);
            }
        });
    }

    @Test
    public void getDateGeneratedTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                StatsService statsService = StatsService.getInstance();
                assertNotNull(statsService.getDateGenerated());
            }
        });
    }

    @Test
    public void calcNumRegisteredUsersTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                StatsService statsService = StatsService.getInstance();
                Map stats = statsService.getStats();
                assertNotEquals(0,stats.get("users"));
            }
        });
    }

    @Test
    public void calcNumCharitiesTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                StatsService statsService = StatsService.getInstance();
                assertNotNull(statsService);
                Map stats = statsService.getStats();
                assertNotEquals(0,stats.get("charities"));
            }
        });
    }

    @Test
    public void calcNumNeedsTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                StatsService statsService = StatsService.getInstance();
                assertNotNull(statsService);
                Map stats = statsService.getStats();
                assertNotEquals(0,stats.get("needs"));
            }
        });
    }

    @Test
    public void calcNumDonationsTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                StatsService statsService = StatsService.getInstance();
                assertNotNull(statsService);
                Map stats = statsService.getStats();
                assertNotEquals(0,stats.get("donations"));
            }
        });
    }

}
