package Services;


import models.Charity;
import models.Donation;
import models.Need;
import models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

//Singleton
public class StatsService {

    private Date dateStatsGenerated;

    private HashMap<String, Long> stats = new HashMap<>();

    private static StatsService instance = null;


    protected StatsService() {
        generateStats();
    }

    //Get Singleton
    public static StatsService getInstance() {
        if(instance == null) {
            synchronized(StatsService.class) {
                if(instance == null) {
                    instance = new StatsService();
                }
            }
        }
        return instance;
    }

    public void generateStats(){
        stats.put("users",calcNumRegisteredUsers());
        stats.put("charities",calcNumCharities());
        stats.put("needs",calcNumNeeds());
        stats.put("donations",calcNumDonations());
        dateStatsGenerated = new Date();
    }

    public HashMap<String, Long> getStats(){
        return stats;
    }

    public String getDateGenerated(){
        return new SimpleDateFormat("dd-MM-yyyy HH:MM").format(dateStatsGenerated);
    }

    private long calcNumRegisteredUsers(){
        return User.find.findRowCount();
    }

    private long calcNumCharities(){
        return Charity.find.findRowCount();
    }

    private long calcNumNeeds(){
        return Need.find.findRowCount();
    }

    private long calcNumDonations(){
        return Donation.find.findRowCount();
    }

}
