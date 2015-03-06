package controllers;

import com.avaje.ebean.PagingList;
import com.avaje.ebean.Page;
import models.Need;
import models.Updates;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.need.invalidNeed;
import views.html.need.viewNeed;

import java.util.List;

public class Needs extends Controller {

    public static Result viewNeed(int page, long id) {

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

        PagingList<Updates> pagingList =  Updates.find.where()
                    .eq("need", need)
                    .orderBy("dateAdded desc")
                .findPagingList(6);


        Page<Updates> currentPage = pagingList.getPage(page - 1);
        List<Updates> updateList = currentPage.getList();

        int totalPageCount = pagingList.getTotalPageCount();

        return ok(viewNeed.render(need,user,updateList,page,totalPageCount));
    }

    public static Result invalidNeed()
    {
        return ok(invalidNeed.render());
    }
}
