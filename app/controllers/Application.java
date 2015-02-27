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

    public static Result index() {
        return ok(index.render());
    }

    public static Result needs(int page) {
        PagingList<Need> pagingList =  Need.find.where()
                .orderBy("urgency desc")
                .findPagingList(3);
        Page<Need> currentPage = pagingList.getPage(page - 1);
        List<Need> needList = currentPage.getList();

        int totalPageCount = pagingList.getTotalPageCount();

        return ok(needs.render(needList, page, totalPageCount));
    }
}
