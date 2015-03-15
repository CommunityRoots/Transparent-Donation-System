package controllers;

import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import models.Charity;
import models.Need;
import play.mvc.Result;
import views.html.charity.invalidCharity;

import java.util.List;

import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class Charities {

    public static Result viewCharity(int page, long id) {

        Charity charity = null;
        charity = Charity.find.byId(id);
        if(charity == null){
            return redirect(
                    routes.Charities.invalidCharity()
            );
        }

        PagingList<Need> pagingList =  Need.find.where()
                .eq("charity_id", id)
                .orderBy("dateAdded desc")
                .findPagingList(5);


        Page<Need> currentPage = pagingList.getPage(page - 1);
        List<Need> needList = currentPage.getList();

        int totalPageCount = pagingList.getTotalPageCount();

        return ok(views.html.charity.charity.render(charity,needList,page,totalPageCount));
    }

    public static Result invalidCharity()
    {
        return ok(invalidCharity.render());
    }
}
