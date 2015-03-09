package Unit.controllers;


import controllers.routes;
import models.Need;
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

public class ProfileTest {
    @Test
    public void callProfile() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result result = callAction(
                        routes.ref.Profile.profile(1),
                        new FakeRequest(GET, "/").withSession(CSRF.TokenName(), CSRFFilter.apply$default$5().generateToken()).withSession("email","fred@gmail.com")
                );
                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/html");
                assertThat(charset(result)).isEqualTo("utf-8");
            }
        });
    }

    @Test
    public void callSettings() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result result = callAction(
                        routes.ref.Profile.settings(),
                        new FakeRequest(GET, "/").withSession(CSRF.TokenName(), CSRFFilter.apply$default$5().generateToken()).withSession("email","fred@gmail.com")
                );
                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/html");
                assertThat(charset(result)).isEqualTo("utf-8");
            }
        });
    }

    @Test
    public void callListVolunteers() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result result = callAction(
                        routes.ref.Profile.listVolunteers(1),
                        new FakeRequest(GET, "/").withSession(CSRF.TokenName(), CSRFFilter.apply$default$5().generateToken()).withSession("email","fred@gmail.com")
                );
                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/html");
                assertThat(charset(result)).isEqualTo("utf-8");
            }
        });
    }

    @Test
    public void callEditNeed() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Need need = Need.find.findList().get(1);
                Result result = callAction(
                        routes.ref.Profile.editNeed(need.id),
                        new FakeRequest(GET, "/").withSession(CSRF.TokenName(), CSRFFilter.apply$default$5().generateToken()).withSession("email", "fred@gmail.com")
                );
                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/html");
                assertThat(charset(result)).isEqualTo("utf-8");
            }
        });
    }
}
