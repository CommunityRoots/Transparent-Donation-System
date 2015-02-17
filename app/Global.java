import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

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

}