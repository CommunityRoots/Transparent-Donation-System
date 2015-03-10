package Integration;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

public class RegisterTest {

    @Test
    public void doRegister() {
        HtmlUnitDriver webDriver = new HtmlUnitDriver(BrowserVersion.CHROME);
        running(testServer(3333),webDriver , new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333/register");
                browser.$("#first_name").text("Bernie");
                browser.$("#last_name").text("Campbell");
                browser.$("#email").text("bernie@hotmail.co.uk");
                browser.$("#password").text("secret1");
                browser.$("#password_confirmation").text("secret1");
                browser.$("button").click();
                assertThat(browser.url()).isEqualTo("http://localhost:3333/profile");

            }
        });
    }

    @Test
    public void doInvalidRegister() {
        HtmlUnitDriver webDriver = new HtmlUnitDriver(BrowserVersion.CHROME);
        running(testServer(3333),webDriver , new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333/register");
                browser.$("#first_name").text("Bernie");
                browser.$("#last_name").text("Campbell");
                browser.$("#email").text("bernie2@hotmail.co.uk");
                browser.$("#password").text("secret");
                browser.$("#password_confirmation").text("secret");
                browser.$("button").click();
                //assertThat(browser.url()).isEqualTo("http://localhost:3333/register");
                assertThat(browser.find(".error").getText().contains("Password must have 1 number, between 6-20 characters"));
            }
        });
    }
}
