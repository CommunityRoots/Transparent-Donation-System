package Integration;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

public class IndexTest {

    @Test
    public void checkIndex() {
        HtmlUnitDriver webDriver = new HtmlUnitDriver(BrowserVersion.CHROME);
        running(testServer(3333),webDriver , new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                //check index page loads
                browser.goTo("http://localhost:3333");
                assertThat(browser.$("h2.slogan").getTexts().get(0)).isEqualTo("Welcome to");
                //move to about section
                browser.$("a#aboutLink").click();
                assertThat(browser.url()).isEqualTo("http://localhost:3333/#about");
            }
        });
    }
}
