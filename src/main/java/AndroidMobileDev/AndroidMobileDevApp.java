package AndroidMobileDev;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class AndroidMobileDevApp {
    private final AppiumDriver<MobileElement> driver;

    public AndroidMobileDevApp(AppiumDriver<MobileElement> driver) {
        this.driver = driver;
    }

    //Main Page
    public MainPage loginPage() {
        return new MainPage(driver);
    }

}
