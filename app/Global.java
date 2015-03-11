import Services.StatsService;
import com.avaje.ebean.Ebean;
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

import static play.mvc.Results.internalServerError;
import static play.mvc.Results.notFound;

public class Global extends GlobalSettings {

    //CSRF token
    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{CSRFFilter.class};
    }
    //page to display when handler not found
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader request){
        return F.Promise.<Result>pure(notFound(
                views.html.notFoundPage.render()
        ));
    }
    //page to display when an error occurs
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable t) {
        String mode = play.api.Play.current().mode().toString();
        if(mode.equals("Test")||mode.equals("Dev")) {
          t.printStackTrace();
        }
        return F.Promise.<Result>pure(internalServerError(
                views.html.error.render()
        ));
    }

    @Override
    public void onStart(Application app)	{
        String mode = play.api.Play.current().mode().toString();
        Logger.info("Application started");
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
                            StatsService.getInstance().generateStats();
                            Logger.info("Stats scheduled for update");
                        }
                    },
                    Akka.system().dispatcher()
            );
        }
    }

    @Override
    public void onStop(Application app) {
        Logger.info("Application shutdown");
    }

}