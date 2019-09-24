package AndroidMobileDev;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.testng.annotations.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.*;

public class BaseDriver {
    public AppiumDriver<MobileElement> driver;
    public String deviceSerial = "";
    public String testOS = "";
    //public AndroidMobileDev.AppiumService myAppiumService;
    public AppiumService myAppiumService = new AppiumService();

    protected AndroidMobileDevApp app;

    public String accessToken = "5b5e50c533cf4e00aa32c8caf1aa1d8fad972f9414a64f71abdce9d06d6a5248";
    public String stfURL = "http://10.109.45.146:7100";
    public String testDevice = "";
    public String androidAppPackage = "org.lds.ldstools.alpha";

//    @BeforeSuite(alwaysRun = true)
    public void removeFilesBeforeTest() {
        File reportsDirectory = new File ("/Users/zmaxfield/Documents/workspace/LDSToolsAppium/src/test/java/Reports");
        File screenshotDirectory = new File ("/Users/zmaxfield/Documents/workspace/LDSToolsAppium/screenshot");

        System.out.println("Start clean directories");
        try {
            FileUtils.cleanDirectory(reportsDirectory);
            FileUtils.cleanDirectory(screenshotDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @BeforeClass(alwaysRun = true)
    @Parameters({"os", "fileName", "testDevice", "startSleepTime"})
    public void setUp(String os, String fileName, String testDevice, int startSleepTime) throws Exception {

        int myPort;
        testOS = os;

        //Sleep so when multiple tests start they don't break
        System.out.println("Sleep Time: " + startSleepTime);
        Thread.sleep(startSleepTime);

        //Get Random Port Number
        myPort = getRandomPort();

        AppiumService.startAppiumService(os, myPort);

        driver = appiumCapabilities(os, fileName, testDevice, myPort);

        app = new AndroidMobileDevApp(driver);


    }




    @AfterMethod(alwaysRun = true)
    public void teardown() throws Exception {

        System.out.println("Start teardown");
        driver.quit();

        System.out.println("Stopping the Appium Service");
        myAppiumService.stopAppiumService();

        System.out.println("End teardown");
    }



    private AppiumDriver<MobileElement> appiumCapabilities(String os, String fileName, String testDevice, int myPort) throws Exception {
        String myAppPackage;
        String myUdid = null;

        //Android Setup
        if (os.equals("android")) {
            List<String> deviceList;

            if (testDevice.contains("STFZade")) {
                accessToken = "c6c814d0122047ab98c2af2a84eb09022a3dd0e82e944526896e9016eb121844";
                stfURL = "http://10.109.45.162:7100";
            }

            if (testDevice.contains("STFMain")) {
                accessToken = "7a51e3b2af8d4cdca761035facd677569b8c3fa4f04f491ca3ed7591372361c1";
                stfURL = "https://serenity.ldschurch.org";
            }




            if (testDevice.contains("STF")) {

                String deviceIPPort = "";


                String[] parts = testDevice.split("-");
                // String part1 = parts[0];


                deviceSerial = parts[1];
                System.out.println("SERIAL NUMBER: " + deviceSerial);

                testDevice = getRemoteIPPort(deviceIPPort);
                adbRemoteConnect(testDevice);
            }

            if (testDevice.contains("WIRELESS")) {

                String deviceIPPort;


                String[] parts = testDevice.split("-");
                // String part1 = parts[0];


                deviceSerial = parts[1];
                System.out.println("SERIAL NUMBER: " + deviceSerial);
                testDevice = deviceSerial;

                adbRemoteConnect(testDevice);
            }



            // set up appium
            File classpathRoot = new File(System.getProperty("user.dir"));
            File appDir = new File(classpathRoot, "../../../Selenium");
            File app = new File(appDir, fileName);

            DesiredCapabilities capabilities = new DesiredCapabilities();

            capabilities.setCapability("app", app.getAbsolutePath());
            myAppPackage = "org.lds.dev";
            androidAppPackage = myAppPackage;

            capabilities.setCapability("deviceName", testDevice);
            capabilities.setCapability("udid", testDevice);
            capabilities.setCapability("platformName", "android");
            capabilities.setCapability("automationName","uiautomator2");
            capabilities.setCapability("appPackage", myAppPackage);
            capabilities.setCapability("newCommandTimeout", 2000);


            capabilities.setCapability("unicodeKeyboard", "true");
            capabilities.setCapability("resetKeyboard", "true");
            capabilities.setCapability("deviceReadyTimeout", 60);
            capabilities.setCapability("noSign", true);
            capabilities.setCapability("gpsEnabled", true);



            capabilities.setCapability("fullReset", false);
            capabilities.setCapability("dontStopAppOnReset", true);
            //capabilities.setCapability("maxTypingFrequency", "8");

            capabilities.setCapability("normalizeTagNames", true);

            capabilities.setCapability("clearDeviceLogsOnStart", true);

            driver = new AndroidDriver<>(new URL("http://127.0.0.1:" + myPort + "/wd/hub"), capabilities);

            Thread.sleep(2000);

        }

        //Setup for iOS
        if (os.equals("ios")) {
            //String webDriverURL = "http://localhost:";
            //String webDriverPort;
            int tempPort;

            tempPort = myPort + 1000;
            //webDriverPort = String.valueOf(tempPort);
            //webDriverURL = webDriverURL + webDriverPort;

            if (testDevice.contains("REAL")) {

                String[] parts = testDevice.split("-");
                // String part1 = parts[0];

                testDevice = parts[1];

                myUdid = getUDIDfromDeviceName(testDevice);


                //myUdid = deviceUDID();
            }

            if (testDevice.contains("FBSIM")) {

                String[] parts = testDevice.split("-");
                //String part1 = parts[0];
                //Remove all whitespace
                //part1 = part1.trim();
                //part2 = part2.trim();

                testDevice = parts[1];

                myUdid = getUDIDfromDeviceNameFBSIM(testDevice);

                //Start Simulator
                startFbSim(myUdid, tempPort);

                //Just a test
                Thread.sleep(6000);

            }

            System.out.println("Device Name: " + testDevice + " UDID: " + myUdid);


            // set up appium
            File classpathRoot = new File(System.getProperty("user.dir"));
            File appDir = new File(classpathRoot, "../../../Selenium");
            File app = new File(appDir, fileName);
            myAppPackage = "org.lds.ldstools.alpha";

            DesiredCapabilities capabilities = new DesiredCapabilities();

            capabilities.setCapability("platformName", "iOS");
            capabilities.setCapability(CapabilityType.BROWSER_NAME, "iOS");
            capabilities.setCapability("deviceName",testDevice);
            capabilities.setCapability("automationName","XCUITest");
            capabilities.setCapability("browserName","");

//            capabilities.setCapability("fullReset", true);
            capabilities.setCapability("noReset", false);
            capabilities.setCapability("newCommandTimeout", 600);
            capabilities.setCapability("app", app.getAbsolutePath());
            capabilities.setCapability("launchTimeout", 900000);
            capabilities.setCapability("platformVersion", "12.4");
            capabilities.setCapability("nativeInstrumentsLib", false);
            capabilities.setCapability("clearSystemFiles", true);
            //capabilities.setCapability("allowTouchIdEnroll", true);
            capabilities.setCapability("maxTypingFrequency", 15);
            //capabilities.setCapability("appPackage", myAppPackage);
            //capabilities.setCapability("simpleIsVisibleCheck", true);
//            capabilities.setCapability("connectHardwareKeyboard", false);





            if (myUdid != null) {
                capabilities.setCapability("xcodeOrgId", "U3SP4KMCK6");
                capabilities.setCapability("xcodeSigningId", "iPhone Developer");
                capabilities.setCapability("udid", myUdid);
                capabilities.setCapability("wdaLocalPort", tempPort);
                capabilities.setCapability("waitForQuiescence", false);
            }


            driver = new IOSDriver<>(new URL("http://127.0.0.1:" + myPort + "/wd/hub"),capabilities);
        }

        return driver;
    }

    private int getRandomPort() throws Exception {
        //System.out.println("OS: " + os );
        //System.out.println("File Name: " + fileName);
        //String myUdid = "";
        Random randomPort = new Random();
        int myPort;
        int lowPort = 4500;
        int highPort = 4999;
        //Random randomSleep = new Random();
        //int sleepTime = 1000;
        //int lowSleep = 1000;
        //int highSleep = 9999;

        Boolean portOpen;

        //Check to see if the random port is open
        //If the port is in use try a different port
        do {
            myPort = randomPort.nextInt(highPort - lowPort) + lowPort;
            portOpen = portCheck(myPort);
        } while (portOpen.equals(false));


        //System.out.println("OS: " + os);
        //System.out.println("PORT: " + myPort);

        return myPort;

    }


    private String getRemoteIPPort(String myText) {
        String myIPPort;
        System.out.println("MY PORT: " + myText);
        String[] ipArray = StringUtils.substringsBetween(myText, "\"", "\"");

        for (String getIP : ipArray) {
            System.out.println("GET IP: " + getIP);
        }

        myIPPort = ipArray[ipArray.length -1];

        return myIPPort;
    }

    private String getAndroidHomePath() throws Exception {
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(new String[] {"/bin/echo $ANDROID_HOME"});
        //Process pr = run.exec(cmd);
        pr.waitFor();
        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;

        line = buf.readLine();
        System.out.println(line);

        return line;
    }


    private void adbRemoteConnect(String ipPort) throws Exception {
        //String pathToADB = "../../../android-sdks/platform-tools/adb";
        //String androidHome = getAndroidHomePath();
        String androidHome = System.getenv("ANDROID_HOME");
        String pathToADB = androidHome + "/platform-tools/adb";

        //String cmd
        // = "adb shell am force-stop org.lds.ldstools.alpha";
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(new String[] {pathToADB, "connect", ipPort});
        //Process pr = run.exec(cmd);
        pr.waitFor();
        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;

        while ((line=buf.readLine())!=null) {
            System.out.println(line);
        }
    }

    private void adbRemoteDisconnect(String ipPort) throws Exception {
        //String pathToADB = "../../../android-sdks/platform-tools/adb";
        //String androidHome = getAndroidHomePath();
        String androidHome = System.getenv("ANDROID_HOME");
        String pathToADB = androidHome + "/platform-tools/adb";

        //String cmd
        // = "adb shell am force-stop org.lds.ldstools.alpha";
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(new String[] {pathToADB, "disconnect", ipPort});
        //Process pr = run.exec(cmd);
        pr.waitFor();
        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;

        while ((line=buf.readLine())!=null) {
            System.out.println(line);
        }
    }

    private String getUDIDfbsim(String deviceNameSearch) throws Exception {
        String line;

        //System.out.println("UDID to Test: " + myUDID);
        Runtime run = Runtime.getRuntime();
        deviceNameSearch = "'" + deviceNameSearch + "'";
        System.out.println("EDIT TO SEARCH " + deviceNameSearch);

        //Process pr = run.exec(new String[] {"/bin/bash", "-c", "fbsimctl list ", " | grep", deviceNameSearch});
        Process pr = run.exec(new String[] {"/bin/bash", "-c", "fbsimctl list | grep " + deviceNameSearch});
        pr.waitFor();

        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

        line = buf.readLine();
        System.out.println(line);


        String[] parts = line.split(" | ");
        String simUDID = parts[0];
//        String simName = parts[1];
//        String simStatus = parts[2];
//        String simModel = parts[3];
//        String simOSVersion = parts[4];


        //System.out.println(deviceName);

        return simUDID;
    }

    private String getUDIDfromDeviceName(String deviceName) throws Exception {
        List<String> connectedUDID;
        String checkDeviceName;
        String returnUDID = "Not Found!";
        //Get UDID from devices
        connectedUDID = multipleDeviceUDID();

        //Match deviceName to UDID
        for(String oneUDID : connectedUDID){
            checkDeviceName = getDeviceName(oneUDID);
            if (checkDeviceName.equals(deviceName))	{
                returnUDID = oneUDID;
            }
        }

        return returnUDID;
    }


    private String deviceUDID() throws IOException{
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "/usr/local/bin/idevice_id -l");
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        line = r.readLine();
        System.out.println(line);
        return line;
    }

    private List<String> multipleDeviceUDID() throws Exception {
        List<String> line = new ArrayList<String>();
        String myLine;

        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(new String[] {"/usr/local/bin/idevice_id", "-l"});
        pr.waitFor();

        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        while ((myLine=buf.readLine())!=null) {
            //System.out.println(myLine);
            line.add(myLine);

        }

        return line;
    }

    private String getDeviceName(String myUDID) throws Exception {
        String deviceName;
        String line;

        //System.out.println("UDID to Test: " + myUDID);
        Runtime run = Runtime.getRuntime();
        //Process pr = run.exec(new String[] {"/usr/local/bin/ideviceinfo", "--udid", myUDID, "|", "grep", "DeviceName"});
        Process pr = run.exec(new String[] {"/bin/bash", "-c", "/usr/local/bin/ideviceinfo --udid " + myUDID + " | grep DeviceName"});
        pr.waitFor();

        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

        line = buf.readLine();
        //System.out.println(line);


        String[] parts = line.split(" ");
        //String part1 = parts[0];

        deviceName = parts[1];

        //System.out.println(deviceName);

        return deviceName;
    }


    private String getUDIDfromDeviceNameFBSIM(String deviceName) throws Exception {
        String simUDID;
        String deviceNameSearch = "| " + deviceName + " |";
        System.out.println("TO SEARCH: " + deviceNameSearch);

        simUDID = getUDIDfbsim(deviceNameSearch) ;

        return simUDID;
    }

    private void startFbSim(String myUdid, int tempPort) throws Exception {
        String line;
        String listenPort = Integer.toString(tempPort);
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(new String[] {"/bin/bash", "-c", "fbsimctl ", myUdid, " boot"});
        //Process pr = run.exec(new String[] {"/bin/bash", "-c", "fbsimctl ", myUdid, " boot",  "--", "listen", "--http ", listenPort});
        //Process pr = run.exec(new String[] {"/bin/bash", "-c", "fbsimctl ", myUdid });
        pr.waitFor();

        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

        line = buf.readLine();
        //System.out.println(line);
    }

    private void stopFbSim() throws Exception {
        String line;
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(new String[] {"/bin/bash", "-c", "fbsimctl shutdown" });
        pr.waitFor();

        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

        line = buf.readLine();
//        System.out.println(line);
    }




    private boolean portCheck(int port) throws IOException {
        // Assume no connection is possible.
        boolean result = false;

        try {
            Socket s = new Socket("localhost", port);
            s.close();
            System.out.println("Port " + port + " is in use.");
            result = false;
        }
        catch(SocketException e) {
            // Could not connect.
            //System.out.println("Port " + port + " is open.");
            result = true;
        }

        return result;
    }


    public void killProcess(String processToKill) throws Exception {
        String line = "";
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(new String[] {"/usr/bin/pkill", "-9", "-f", "-l", processToKill});
        pr.waitFor();
        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

        while ((line=buf.readLine())!=null) {
            System.out.println(line);
        }
    }


    public String getRunningOS() {
        String myOs = "";

        if (testOS.equalsIgnoreCase("android")) {
            myOs = "android";
        }

        if (testOS.equalsIgnoreCase("ios")) {
            myOs = "ios";
        }

        myOs = myOs.toLowerCase();
        //System.out.println("MY OS: "  + myOs);
        return myOs;
    }

    private void adbCommand(String myCommand) throws Exception {
        String androidHome = System.getenv("ANDROID_HOME");
        String pathToADB = androidHome + "/platform-tools/adb";



        if (myCommand.equals("stopApp")) {
            //String cmd = "adb shell am force-stop org.lds.ldstools.alpha";
            Runtime run = Runtime.getRuntime();
//            Process pr = run.exec(new String[] {pathToADB, "shell", "am", "force-stop", "org.lds.ldstools.alpha"});
            Process pr = run.exec(new String[] {pathToADB, "shell", "am", "force-stop", androidAppPackage});
            //Process pr = run.exec(cmd);
            pr.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "";
            while ((line=buf.readLine())!=null) {
                System.out.println(line);
            }
        }

        if (myCommand.equals("clearApp")) {
            //String cmd = "adb shell am force-stop org.lds.ldstools.alpha";
            Runtime run = Runtime.getRuntime();
            Process pr = run.exec(new String[] { pathToADB, "shell", "pm", "clear", androidAppPackage});
            //Process pr = run.exec(cmd);
            pr.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "";
            while ((line=buf.readLine())!=null) {
                System.out.println(line);
            }
        }
    }

    public void takeScreenShot() {
        String imagesLocation = "screenshot/";
        new File(imagesLocation).mkdirs(); // Insure directory is there

        try {
            File srcFile=driver.getScreenshotAs(OutputType.FILE);
            String filename= UUID.randomUUID().toString();
            System.out.println("Screenshot File: " + filename);
            File targetFile=new File(imagesLocation + filename +".png");
            FileUtils.copyFile(srcFile,targetFile);
        }
        catch(Exception e){
            System.out.println("Warning: Some Other exception");
        }

    }

    private void takeScreenShotDirectory(String filename, String imagesLocation) {
        try {
            File srcFile=driver.getScreenshotAs(OutputType.FILE);
            System.out.println("Screenshot File: " + filename);
            File targetFile=new File(imagesLocation + filename +".png");
            FileUtils.copyFile(srcFile,targetFile);
        }
        catch(Exception e){
            System.out.println("Warning: Some Other exception");
        }

    }


    private void screenshotAndLogs(String testName) throws Exception {
        LogEntries logEntries;
        List<String> myLogData = new ArrayList<String>();
        List<String> logTypes = new ArrayList<String>();
        //Get Random UUID
        String filename= UUID.randomUUID().toString();
        //Make DIR for random UUID
        String imagesLocation = "screenshot/" + filename +"/";
        String logFile = imagesLocation + testName + ".txt";
        new File(imagesLocation).mkdirs(); // Insure directory is there


        //Take Screen shot
        takeScreenShotDirectory(filename, imagesLocation);

        myLogData.add(testName);
        myLogData.add("******************* LOGS *********************");

        if (getRunningOS().contains("ios")) {
            //logTypes.add("syslog");
            logTypes.add("crashlog");
            logTypes.add("server");
        } else {
            logTypes.add("logcat");
//            logTypes.add("bugreport");
            logTypes.add("server");
        }

        //Set<String> logTypes = driver.manage().logs().getAvailableLogTypes();
        for(String myLog : logTypes) {
            //System.out.println(myLog);
            myLogData.add(" ******************* " + myLog +  " ******************* " );
            logEntries = driver.manage().logs().get(myLog);
            for (LogEntry entry : logEntries) {
                myLogData.add(entry.getMessage());
                //System.out.println(entry.getMessage());
            }

        }

        myLogData.add("******************* END LOGS *********************");

        PrintWriter pw = new PrintWriter(new FileOutputStream(logFile));
        for (String logItem : myLogData) {
            pw.println(logItem);
        }
        pw.close();

    }



}
