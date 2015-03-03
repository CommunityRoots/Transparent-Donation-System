package Integration;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

public class LoginTest {
    @Test
    public void doValidLogin() {
        HtmlUnitDriver webDriver = new HtmlUnitDriver(BrowserVersion.CHROME);
        running(testServer(3333),webDriver , new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                //check index page loads
                browser.goTo("http://localhost:3333/login");
                browser.$("#email").text("bob@gmail.com");
                browser.$("#password").text("secret");
                browser.$("button").click();
                assertThat(browser.url()).isEqualTo("http://localhost:3333/profile");
            }
        });
    }

    @Test
    public void doInValidLogin() {
        HtmlUnitDriver webDriver = new HtmlUnitDriver(BrowserVersion.CHROME);
        running(testServer(3333),webDriver , new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                //check index page loads
                browser.goTo("http://localhost:3333/login");
                browser.$("#email").text("bob@gmail.com");
                browser.$("#password").text("secret12");
                browser.$("button").click();
                assertThat(browser.url()).contains("http://localhost:3333/login");
            }
        });
    }
}
