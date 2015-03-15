package controllers;

import Services.StatsService;
import models.Charity;
import models.Need;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.admin;
import views.html.admin.payOut;

import java.util.HashMap;
import java.util.List;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class Admin extends Controller {

    public static class AddCharity {
        public String charityName;
        public String website;
        public String description;

        public String validate(){
            if(charityName.length()==0 || charityName == null){
                return "Please enter charity name";
            }
            return null;
        }
    }

    public static class AddLeader {
        public String leaderEmail;

        public long charity;

        public String validate()
        {
            User user = User.findByEmail(leaderEmail);
            if(user == null){
                return "No user with that email. User must already be registered";
            }
            return null;
        }
    }

    public static Result admin(){
        String email = session().get("email");
        User user = User.findByEmail(email);
        List<Charity> charityList = Charity.find.all();
        if(user == null || user.role>1){
            return redirect(routes.Application.index());
        }
        StatsService statsService = StatsService.getInstance();
        HashMap<String,Long> stats = statsService.getStats();

        return ok(admin.render(form(AddCharity.class),form(AddLeader.class),charityList,
                stats.get("needs"),stats.get("charities"),stats.get("users"),stats.get("donations"),statsService.getDateGenerated()));
    }

    public static Result addLeader(){
        List<Charity> charityList = Charity.find.all();
        Form<AddLeader> addLeaderForm = form(AddLeader.class).bindFromRequest();
        StatsService statsService = StatsService.getInstance();
        HashMap<String,Long> stats = statsService.getStats();
        if(addLeaderForm.hasErrors()){
            return badRequest(admin.render(form(AddCharity.class), addLeaderForm, charityList,
                    stats.get("needs"), stats.get("charities"), stats.get("users"), stats.get("donations"), statsService.getDateGenerated()));
        } else {
            String email = addLeaderForm.get().leaderEmail;
            long charityId = addLeaderForm.get().charity;
            User user = User.findByEmail(email);
            user.setCharity(Charity.find.byId(charityId));
            user.changeRole(2);
            return admin();
        }
    }

    public static Result addCharity(){
        Form<AddCharity> addCharityForm = form(AddCharity.class).bindFromRequest();
        List<Charity> charityList = Charity.find.all();
        StatsService statsService = StatsService.getInstance();
        HashMap<String,Long> stats = statsService.getStats();
        if (addCharityForm.hasErrors()) {
            return badRequest(admin.render(addCharityForm,form(AddLeader.class),charityList,
                    stats.get("needs"),stats.get("charities"),stats.get("users"),stats.get("donations"),statsService.getDateGenerated()));
        } else {
            String charityName = addCharityForm.get().charityName;
            Charity charity = new Charity(charityName);
            charity.save();
            return admin();
        }
    }

    public static Result payOut(){
        return ok(payOut.render(Need.needsToBePaidOut()));
    }

    public static Result markAsPaid(long needId){
        User user = User.findByEmail(session().get("email"));
        Need need = Need.find.byId(needId);
        if(user.role!=1 || !need.closed){
            return redirect(routes.Application.index());
        }
        need.markAsPaidToCharity();
        flash("success","Need has been marked as paid to charity");
        return payOut();
    }
}
