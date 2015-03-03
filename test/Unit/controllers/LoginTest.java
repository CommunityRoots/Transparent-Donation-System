package Unit.controllers;

import controllers.routes;
import org.junit.Test;
import play.filters.csrf.CSRF;
import play.filters.csrf.CSRFFilter;
import play.mvc.Result;
import play.test.FakeRequest;


import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class LoginTest {

    @Test
    public void callLogin() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result result = callAction(
                        routes.ref.Login.login(),
                        new FakeRequest(GET, "/").withSession(CSRF.TokenName(), CSRFFilter.apply$default$5().generateToken())
                );
                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/html");
                assertThat(charset(result)).isEqualTo("utf-8");
            }
        });
    }
}
