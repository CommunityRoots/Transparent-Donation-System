package Unit.controllers;


import controllers.routes;
import org.junit.Test;
import play.filters.csrf.CSRF;
import play.filters.csrf.CSRFFilter;
import play.mvc.Result;
import play.test.FakeRequest;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentType;

public class ApplicationTest {

    @Test
    public void callIndex() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result result = callAction(
                        routes.ref.Application.index(),
                        new FakeRequest(GET, "/").withSession(CSRF.TokenName(), CSRFFilter.apply$default$5().generateToken())
                );
                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/html");
                assertThat(charset(result)).isEqualTo("utf-8");
            }
        });
    }

    @Test
    public void callNeeds() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result result = callAction(
                        routes.ref.Application.needs(1,1),
                        new FakeRequest(GET, "/").withSession(CSRF.TokenName(), CSRFFilter.apply$default$5().generateToken())
                );
                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/html");
                assertThat(charset(result)).isEqualTo("utf-8");
            }
        });
    }
}
