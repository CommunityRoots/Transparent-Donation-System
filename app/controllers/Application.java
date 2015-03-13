package controllers;

import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import models.Need;
import play.cache.Cached;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.need.needs;
import views.html.tos;

import java.util.List;

public class Application extends Controller {

    //Cache the index. Index doesnt change
    @Cached(key = "homePage", duration = 10)
    public static Result index() {
        return ok(index.render());
    }

    public static Result needs(int page, long category) {
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

    @Cached(key ="tos", duration = 10)
    public static Result tos(){
        return ok(tos.render());
    }
}
