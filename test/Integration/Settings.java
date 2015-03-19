package Integration;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

public class Settings {

    @Test
    public void settings() {
        HtmlUnitDriver webDriver = new HtmlUnitDriver(BrowserVersion.CHROME);
        running(testServer(3333), webDriver, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333/login");
                browser.$("#email").text("bob@gmail.com");
                browser.$("#password").text("secret");
                browser.$("button").click();
                browser.goTo("http://localhost:3333/settings");
                browser.$("#oldPassword").text("wrongpassword");
                browser.$("#newPassword").text("kreklrrekl");
                browser.$("#retypePassword").text("kreklrrekl");
                browser.$("#changePassButton").submit();
                assertThat(browser.find("p.error").getText().contains("Invalid password"));
            }
        });
    }
}
