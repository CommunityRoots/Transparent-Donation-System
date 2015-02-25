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

public class Application extends Controller {

    public static class Login {

        public String email;
        public String password;

        public String validate() {
            if (User.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }

    public static Result index() {
        return ok(index.render());
    }

    public static Result needs(int page) {
        PagingList<Need> pagingList =  Need.find.where()
                .orderBy("urgency desc")
                .findPagingList(6);
        Page<Need> currentPage = pagingList.getPage(page - 1);
        List<Need> needList = currentPage.getList();

        int totalPageCount = pagingList.getTotalPageCount();

        return ok(needs.render(needList, page, totalPageCount));
    }

    public static Result viewNeed(long id) {
        Need need = Need.find.byId(id);
        if(need == null){
            return redirect(
                    routes.Application.invalidNeed()
            );
        }
        return ok(viewNeed.render(Need.find.byId(id)));
    }

    public static Result invalidNeed()
    {
        return ok(invalidNeed.render());
    }

    public static Result login(){
        return ok(login.render(form(Login.class)));
    }


    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            return redirect(
                    routes.Profile.profile(1)
            );
        }
    }

    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Application.login()
        );
    }
}
