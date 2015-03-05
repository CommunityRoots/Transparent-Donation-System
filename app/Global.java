import com.avaje.ebean.Ebean;
import controllers.Admin;
import models.Token;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;
import play.libs.Akka;
import play.libs.F;
import play.libs.Yaml;
import play.mvc.Http;
import play.mvc.Result;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
        Logger.info("Application started");
        String mode = play.api.Play.current().mode().toString();
        if(mode.equals("Test")||mode.equals("Dev")){
            if (User.find.findRowCount()==0) {
                Ebean.save((List) Yaml.load("test-data.yml"));
                Logger.info("Data loaded into database");
            }
        } else{
            if (User.find.findRowCount()==0) {
                Ebean.save((List) Yaml.load("initial-data.yml"));
                Logger.info("Data loaded into database");
            }
        }
        if(!mode.equals("Test")) {
            Akka.system().scheduler().schedule(
                    FiniteDuration.create(0, TimeUnit.MILLISECONDS),
                    FiniteDuration.create(24, TimeUnit.HOURS),
                    new Runnable() {
                        @Override
                        public void run() {
                            Token.checkIfTokensAreValid();
                            Logger.info("Token cleanup" + System.currentTimeMillis());
                        }
                    },
                    Akka.system().dispatcher()
            );
        }
    }
}