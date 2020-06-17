package Base;
//        chrome.exe --remote-debugging-port=9222 --user-data-dir="C:\selenium\ChromeProfile"


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.apache.commons.io.FileUtils;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class testBase {

    public static Properties config = new Properties();
    public static Properties OR = new Properties();
    public static FileInputStream fis;
    public static String baseURL = "";
    public ExtentHtmlReporter htmlReporter;
    public ExtentReports extent;
    public ExtentTest test;

    @BeforeSuite
    public void setup() {

//        put the code for the init browser
//        chrome.exe --remote-debugging-port=9222 --user-data-dir="C:\selenium\ChromeProfile"

//        try {
//            Runtime.getRuntime().exec("cmd /c start cmd.exe /K chrome.exe --remote-debugging-port=9222 --user-data-dir=\"C:\\selenium\\ChromeProfile\"");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Code for init
        try {
            fis = new FileInputStream(
                    System.getProperty("user.dir") + "\\src\\test\\resources\\properties\\Config.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            config.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fis = new FileInputStream
                    (
                            System.getProperty("user.dir") + "\\src\\test\\resources\\properties\\OR.properties"
                    );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            OR.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\test\\resources\\executables\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);
//        driver.get(config.getProperty("testsiteurl"));
//        System.out.println(driver.getCurrentUrl());


        System.out.println(driver.getCurrentUrl());
        baseURL = driver.getCurrentUrl();

    }

    public void setTest(String TestName) {
        test = extent.createTest(TestName);
    }

    @BeforeTest
    public void setExtent() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);

        // specify location of the report
        htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "/test-output/myReport.html");

        htmlReporter.config().setDocumentTitle("Analytics Report"); // Tile of report
        htmlReporter.config().setReportName("Analytics test"); // Name of the report
        htmlReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        // Passing General information
        extent.setSystemInfo("Host name", driver.getCurrentUrl());
        extent.setSystemInfo("Environment", "Test");
        extent.setSystemInfo("user", System.getProperty("user.name"));

    }

    @AfterTest
    public void endReport() {
        extent.flush();
    }

    @AfterMethod
    public void extentTearDown(ITestResult result) throws IOException {

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);

        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "TEST CASE FAILED IS " + result.getName()); // to add name in extent report
            test.log(Status.FAIL, "TEST CASE FAILED IS " + result.getThrowable()); // to add error/exception in extent report
            String screenshotPath = getScreenshot(driver, result.getName());
            test.addScreenCaptureFromPath(screenshotPath);// adding screen shot
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.log(Status.SKIP, "Test Case SKIPPED IS " + result.getName());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, "Test Case PASSED IS " + result.getName());
            String screenshotPath = getScreenshot(driver, result.getName());
            test.addScreenCaptureFromPath(screenshotPath);// adding screen shot
        }
    }

    public static String getScreenshot(WebDriver driver, String screenshotName) throws IOException {
        String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);

        // after execution, you could see a folder "FailedTestsScreenshots" under src folder
        String destination = System.getProperty("user.dir") + "/test-output/Screenshots/" + screenshotName + dateName + ".png";
        File finalDestination = new File(destination);
        FileUtils.copyFile(source, finalDestination);
        return destination;
    }

    public void waitTill(String locator) {

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);

        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(OR.getProperty(locator))));

    }

    public String gtext(String locator) {

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);

        WebElement temp = driver.findElement(By.xpath(OR.getProperty(locator)));

        return (temp.getText());
    }

    public void entr(String locator) {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);

        if (locator.endsWith("_CSS")) {
            driver.findElement(By.cssSelector(OR.getProperty(locator))).sendKeys(Keys.ENTER);
        } else if (locator.endsWith("_XPATH")) {
            driver.findElement(By.xpath(OR.getProperty(locator))).sendKeys(Keys.ENTER);
        } else if (locator.endsWith("_ID")) {
            driver.findElement(By.id(OR.getProperty(locator))).sendKeys(Keys.ENTER);
        }
    }

    public void clr(String locator) {

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);


        if (locator.endsWith("_CSS")) {
            driver.findElement(By.cssSelector(OR.getProperty(locator))).clear();
        } else if (locator.endsWith("_XPATH")) {
            driver.findElement(By.xpath(OR.getProperty(locator))).clear();
        } else if (locator.endsWith("_ID")) {
            driver.findElement(By.id(OR.getProperty(locator))).clear();
        }
    }

    public void tabSwitch(int value) {

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);

        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(value));

    }

    public void type(String locator, String value) {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);

        if (locator.endsWith("_CSS")) {
            driver.findElement(By.cssSelector(OR.getProperty(locator))).sendKeys(value);
        } else if (locator.endsWith("_XPATH")) {
            driver.findElement(By.xpath(OR.getProperty(locator))).sendKeys(value);
        } else if (locator.endsWith("_ID")) {
            driver.findElement(By.id(OR.getProperty(locator))).sendKeys(value);
        }
    }

    public void click(String locator) {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);


        if (locator.endsWith("_CSS")) {
            driver.findElement(By.cssSelector(OR.getProperty(locator))).click();
        } else if (locator.endsWith("_XPATH")) {
            driver.findElement(By.xpath(OR.getProperty(locator))).click();
        } else if (locator.endsWith("_ID")) {
            driver.findElement(By.id(OR.getProperty(locator))).click();
        }
    }

    public void navigateToBaseAndRefresh() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);
        driver.navigate().to(baseURL);
        waitExplicit(5000);
        driver.navigate().refresh();
    }

    public void hovers(String locator) {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);
//        WebElement temp = driver.findElement(By.xpath(OR.getProperty(locator)));
        WebElement temp = null;
        if (locator.endsWith("_CSS")) {
            temp = driver.findElement(By.cssSelector(OR.getProperty(locator)));
        } else if (locator.endsWith("_XPATH")) {
            temp = driver.findElement(By.xpath(OR.getProperty(locator)));
        } else if (locator.endsWith("_ID")) {
            temp = driver.findElement(By.id(OR.getProperty(locator)));
        }
        Actions action = new Actions(driver);
        action.moveToElement(temp).build().perform();
    }

    public void doubleClick(String locator) {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);
        Actions actions = new Actions(driver);

        if (locator.endsWith("_CSS")) {
            WebElement elementLocator = driver.findElement(By.cssSelector(OR.getProperty(locator)));
            actions.doubleClick(elementLocator).perform();
        } else if (locator.endsWith("_XPATH")) {
            WebElement elementLocator = driver.findElement(By.xpath(OR.getProperty(locator)));
            actions.doubleClick(elementLocator).perform();
        } else if (locator.endsWith("_ID")) {
            WebElement elementLocator = driver.findElement(By.id(OR.getProperty(locator)));
            actions.doubleClick(elementLocator).perform();
        }
    }

    public void waitExplicit(int wait) {
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterSuite
    public void tearDown() {

        //        code to quit the browser

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriver driver = new ChromeDriver(options);

//        ((JavascriptExecutor)driver).executeScript("open(location, '_self').close();");

        try {
//            Uncomment this snippet on the production env only
//            Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");

            Runtime.getRuntime().exec("taskkill /f /im cmd.exe");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        driver.close();
    }
}
