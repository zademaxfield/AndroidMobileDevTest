package AndroidMobileDev;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class MainPage extends BasePage {

//    private AppiumDriver<MobileElement> driver;

    public MainPage(AppiumDriver<MobileElement> driver) {
        super(driver);
        Duration myDuration = Duration.ofSeconds(10);
        PageFactory.initElements(new AppiumFieldDecorator(driver, myDuration), this);
    }

    //Version
    @AndroidFindBy(id = "versionTextView")
    public MobileElement version;

    //Expiration Date
    @AndroidFindBy(id = "expiresTextView")
    public MobileElement expirationDate;

    //Developer Mode Check Box
    @AndroidFindBy(id = "devModeCheckBox")
    public MobileElement developerModeCheckBox;

    //Tools Label
    @AndroidFindBy(id = "toolsLabel")
    public MobileElement toolsLabel;

    //Environment Label
    @AndroidFindBy(id = "toolsNetworkEnvLabel")
    public MobileElement environmentLabel;

    //Select Network Environment
    @AndroidFindBy(id = "toolsNetworkEnvAutoCompleteTextView")
    public MobileElement selectNetworkEnvironment;



}
