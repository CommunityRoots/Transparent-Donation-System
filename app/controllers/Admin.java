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
import views.html.need.editNeed;

import java.util.HashMap;
import java.util.List;

import static play.data.Form.form;
import static play.mvc.Results.ok;

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
            if(website.contains("http")){
                return "Website should in form of www.charityAddress.com";
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
            else if(user.charity!=null){
                return "User is already part of a charity";
            }
            return null;
        }
    }

    public static Result admin(){
        String email = session().get("email");
        User user = User.findByEmail(email);
        Charity charity = user.charity;
        List<Charity> charityList = Charity.find.all();
        if(user == null || user.role>1){
            return redirect(routes.Application.index());
        }
        StatsService statsService = StatsService.getInstance();
        HashMap<String,Long> stats = statsService.getStats();

        return ok(admin.render(form(AddCharity.class),form(AddLeader.class),charityList,
                stats.get("needs"),stats.get("charities"),stats.get("users"),
                stats.get("donations"),statsService.getDateGenerated(),Need.numNeedsToBePaidOut()));
    }

    public static Result addLeader(){
        String email = session().get("email");
        User user = User.findByEmail(email);
        List<Charity> charityList = Charity.find.all();
        Form<AddLeader> addLeaderForm = form(AddLeader.class).bindFromRequest();
        StatsService statsService = StatsService.getInstance();
        HashMap<String,Long> stats = statsService.getStats();
        if(addLeaderForm.hasErrors()){
            return badRequest(admin.render(form(AddCharity.class), addLeaderForm, charityList,
                    stats.get("needs"), stats.get("charities"), stats.get("users"), stats.get("donations"),
                    statsService.getDateGenerated(),Need.numNeedsToBePaidOut()));
        } else {
            String leaderEmail = addLeaderForm.get().leaderEmail;
            long charityId = addLeaderForm.get().charity;
            User leader = User.findByEmail(leaderEmail);
            leader.setCharity(Charity.find.byId(charityId));
            leader.changeRole(2);
            return admin();
        }
    }

    public static Result addCharity(){
        String email = session().get("email");
        User user = User.findByEmail(email);
        Form<AddCharity> addCharityForm = form(AddCharity.class).bindFromRequest();
        List<Charity> charityList = Charity.find.all();
        StatsService statsService = StatsService.getInstance();
        HashMap<String,Long> stats = statsService.getStats();
        if (addCharityForm.hasErrors()) {
            return badRequest(admin.render(addCharityForm,form(AddLeader.class),charityList,
                    stats.get("needs"),stats.get("charities"),stats.get("users"),stats.get("donations"),
                    statsService.getDateGenerated(),Need.numNeedsToBePaidOut()));
        } else {
            String charityName = addCharityForm.get().charityName;
            String website = addCharityForm.get().website;
            String description = addCharityForm.get().description;
            Charity charity = new Charity(charityName,website,description);
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
