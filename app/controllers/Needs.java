package controllers;

import models.*;
import play.Routes;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import java.util.List;

import static play.data.Form.form;

public class Needs extends Controller {

    public static Result viewNeed(long id) {

        User user = null;
        String email = session().get("email");
        if(email!=null){
            user = User.find.byId(email);
        }
        Need need = Need.find.byId(id);
        if(need == null){
            return redirect(
                    routes.Needs.invalidNeed()
            );
        }
        return ok(viewNeed.render(Need.find.byId(id),user));
    }

    public static Result invalidNeed()
    {
        return ok(invalidNeed.render());
    }
}
