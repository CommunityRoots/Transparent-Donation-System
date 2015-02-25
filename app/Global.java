import com.avaje.ebean.Ebean;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;
import play.libs.F;
import play.libs.Yaml;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;

import static play.mvc.Results.notFound;

public class Global extends GlobalSettings {
    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{CSRFFilter.class};
    }

    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader request){
        return F.Promise.<Result>pure(notFound(
                views.html.notFoundPage.render()
        ));
    }
    @Override
    public void onStart(Application app)	{
        String mode = play.api.Play.current().mode().toString();
        if(mode.equals("Test")||mode.equals("Dev")){
            if (User.find.findRowCount()==0) {
                Ebean.save((List) Yaml.load("test-data.yml"));
            }
        }

    }

}