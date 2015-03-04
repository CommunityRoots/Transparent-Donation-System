package controllers;

import models.*;
import play.Routes;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.need.needs;
import views.html.index;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import java.util.List;

import static play.data.Form.form;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result needs(int page, int category) {
        PagingList<Need> pagingList;
        if(category == 2) {
            pagingList = Need.find.where()
                    .orderBy("urgency desc")
                    .findPagingList(9);
        }
        else {
            pagingList =  Need.find.where()
                    .eq("category", category)
                    .orderBy("urgency desc")
                    .findPagingList(9);
        }

        Page<Need> currentPage = pagingList.getPage(page - 1);
        List<Need> needList = currentPage.getList();

        int totalPageCount = pagingList.getTotalPageCount();

        return ok(needs.render(needList, page, totalPageCount,category));
    }
}
