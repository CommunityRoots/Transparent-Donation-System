package Integration;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

public class NeedsTest {

    @Test
    public void addNeed() {
        final HtmlUnitDriver webDriver = new HtmlUnitDriver(BrowserVersion.CHROME);
        running(testServer(3333),webDriver , new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                //log bob in. bob has leader level access
                browser.goTo("http://localhost:3333/login");
                browser.$("#email").text("bob@gmail.com");
                browser.$("#password").text("secret");
                browser.$("button").click();
                assertThat(browser.url()).isEqualTo("http://localhost:3333/profile");
                browser.$("button#showAddNeed").click();
                assertThat(browser.find("addNeedForm").size()==1);
                browser.$("#needTitle").text("SelTest");
                browser.$("#amount").text("100");
                browser.$("#location").text("Webb");
                Select urgencySelect = new Select(webDriver.findElement(By.id("urgency")));
                urgencySelect.selectByVisibleText("10");
                System.out.println(urgencySelect.getOptions().get(0).getText());
                Select categorySelect = new Select(webDriver.findElement(By.id("category")));
                categorySelect.selectByVisibleText("Family");
                browser.$("#needDescription").text("Inserted through test");
                browser.$("button#addNeedButton").click();
                assertThat(browser.$("#needAdded").getText().equals("Need has been added successfully"));
            }
        });
    }

    @Test
    public void viewNeed() {
        final HtmlUnitDriver webDriver = new HtmlUnitDriver(BrowserVersion.CHROME);
        running(testServer(3333),webDriver , new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                //go to page of needs
                browser.goTo("http://localhost:3333/needs");
                //click on need
                browser.$(".viewNeedLink").first().click();
                //assert that go to need page
                assertThat(browser.url().contains("viewNeed"));
            }
        });
    }
}
