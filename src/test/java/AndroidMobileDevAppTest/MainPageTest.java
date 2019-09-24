package AndroidMobileDevAppTest;

import AndroidMobileDev.BaseDriver;
import AndroidMobileDev.BasePage;
import AndroidMobileDev.MainPage;

import org.testng.annotations.Test;

import java.util.List;


public class MainPageTest extends BaseDriver {

    @Test (groups = {"jft"} )
    public void mainPageUAT() throws Exception {
        MainPage myMainPage = new MainPage(driver);

        // Check for Developer Options checked and check it if not enabled.
        if (myMainPage.developerModeCheckBox.getAttribute("checked").equalsIgnoreCase("false")) {
            myMainPage.developerModeCheckBox.click();
        }

        myMainPage.selectNetworkEnvironment.clear();
        myMainPage.selectNetworkEnvironment.sendKeys("UAT");


        //Just for testing
        Thread.sleep(10000);

    }

}
