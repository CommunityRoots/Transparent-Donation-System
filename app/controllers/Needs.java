package controllers;

import models.Need;
import models.Updates;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.need.invalidNeed;
import views.html.need.viewNeed;

import java.util.List;

public class Needs extends Controller {

    public static Result viewNeed(long id) {

        User user = null;
        String email = session().get("email");
        if(email!=null){
            user = User.findByEmail(email);
        }
        Need need = Need.find.byId(id);
        if(need == null){
            return redirect(
                    routes.Needs.invalidNeed()
            );
        }

        return ok(viewNeed.render(need,user,need.getUpdates()));
    }

    public static Result invalidNeed()
    {
        return ok(invalidNeed.render());
    }
}
