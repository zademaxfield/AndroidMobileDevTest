package AndroidMobileDev;

import org.openqa.selenium.By;

public class MobileDevMain extends BaseDriver {


    public static void main(String[] args) throws Exception {
//        BaseDriver myBaseDriver = new BaseDriver();
//        myBaseDriver.setUp("android", "android-mobile-dev-release.apk", "emulator-5554", 200);
//
//        if (driver.findElement(By.id("devModeCheckBox")).getAttribute("checked").equalsIgnoreCase("false")) {
//            driver.findElement(By.id("devModeCheckBox")).click();
//        }
//
//        driver.findElement(By.id("toolsNetworkEnvAutoCompleteTextView")).clear();
//        driver.findElement(By.id("toolsNetworkEnvAutoCompleteTextView")).sendKeys("UAT");

        MobileDevMain myMobileDevMain = new MobileDevMain("android", "android-mobile-dev-release.apk", "emulator-5554", "UAT");

        myMobileDevMain.teardown();



    }


    public MobileDevMain(String os, String fileName, String testDevice, String networkEnvironment) throws Exception {

        setUp("android", "android-mobile-dev-release.apk", "emulator-5554", 200);

        if (driver.findElement(By.id("devModeCheckBox")).getAttribute("checked").equalsIgnoreCase("false")) {
            driver.findElement(By.id("devModeCheckBox")).click();
        }

        driver.findElement(By.id("toolsNetworkEnvAutoCompleteTextView")).clear();
        driver.findElement(By.id("toolsNetworkEnvAutoCompleteTextView")).sendKeys("UAT");

        Thread.sleep(10000);

//        mobileDevTeardown();


//        MainPage myMainPage = new MainPage(driver);

//        // Check for Developer Options checked and check it if not enabled.
//        if (myMainPage.developerModeCheckBox.getAttribute("checked").equalsIgnoreCase("false")) {
//            myMainPage.developerModeCheckBox.click();
//        }
//
//        myMainPage.selectNetworkEnvironment.clear();
//        myMainPage.selectNetworkEnvironment.sendKeys(networkEnvironment);


//        //Just for testing
//        Thread.sleep(10000);

    }

    public void mobileDevTeardown() throws Exception {
        teardown();

    }



}
