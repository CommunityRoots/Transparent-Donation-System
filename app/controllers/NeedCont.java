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

public class NeedCont extends Controller {

    public static Result viewNeed(long id) {
        Need need = Need.find.byId(id);
        if(need == null){
            return redirect(
                    routes.NeedCont.invalidNeed()
            );
        }
        return ok(viewNeed.render(Need.find.byId(id)));
    }

    public static Result invalidNeed()
    {
        return ok(invalidNeed.render());
    }
}
