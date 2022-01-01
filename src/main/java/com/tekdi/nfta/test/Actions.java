package com.tekdi.nfta.test;

import static com.tekdi.nfta.test.NFTADriver.ObjectRepository;
import static com.tekdi.nfta.test.NFTADriver.stepcount;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.paulhammant.ngwebdriver.ByAngular;
import com.tekdi.nfta.config.Constant;
import com.tekdi.nfta.utils.LoggerUtil;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.logging.Level;

public class Actions {

	WebDriver driver;
        
        Logger logger = LoggerUtil.getLogger(); //Added by Mandar Wadhavekar
	//private FileHandler fh;
	//Logger logger = Logger.getLogger(Actions.class.getName()); 
         
	/* Commented by Mandar Wadhavekar for Refactoring
        Actions() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-YY_HHmmss");
			fh = new FileHandler(
					System.getProperty(Constant.PROJECT_ROOT_DIRECTORY.getValue()) + Constant.LOGS_PATH.getValue()
							+ "actionlogs_" + format.format(Calendar.getInstance().getTime()) + ".log");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			logger.setUseParentHandlers(false);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
        */

	/**
	 * openBrowser will instantiate a new instance of a specified browser
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */
//TODO: Add support for Firefox, Edge - Mandar Wadhavekar
	public String openBrowser(String locator, String browser) {
            //changed 'data' to 'browser' for better readability: Mandar Wadhavekar
		try {
			logger.fine("ActionMethod- openBrowser");
			if (browser.equalsIgnoreCase("Chrome")) {
				//Stopwatch timer = Stopwatch.createStarted(); //
                                LoggerUtil.startTimeMeasurement();
				WebDriverManager.chromedriver().setup();
				driver = new ChromeDriver();
                                
                                stepcount++; //increment step counter
                                LoggerUtil.stopTimeMeasurement("OpenBrowser() method ");
				logger.info("stepID: " + stepcount);
			} else {
				//Stopwatch timer = Stopwatch.createStarted();
				LoggerUtil.startTimeMeasurement();
                                //TODO: chrome options to be put outside e.g. properties file 
				WebDriverManager.chromedriver().setup();
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--no-sandbox");
				options.addArguments("--headless");
				options.addArguments("--disable-gpu");
				options.addArguments("--disable-dev-shm-usage");
				driver = new ChromeDriver(options);
				//logger.info("openBrowser action took: " + timer + " stepID: " + stepcount++);
                                stepcount++; //increment step counter
                                logger.log(Level.INFO, "stepID: {0}", stepcount);
                                LoggerUtil.stopTimeMeasurement("OpenBrowser() action");
			}
			driver.manage().window().maximize();
		} catch (Exception e) {

			/**
			 * Probable causes for getting exception in this case - 
                         * 
			 * 1. System error to initialize browser 
                         * 2. Browser crashed
			 * 
			 */
                        logger.log(Level.SEVERE, "Either failed to Initialize {0}", browser);                        
                        logger.log(Level.SEVERE, "Or  {0} Crashed.", browser);
                        LoggerUtil.logException(e); //Added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";

		}

		return Constant.KEYWORD_PASS.getValue();

	}

	/**
	 * navigateTo will navigate to a specified destination
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String navigateTo(String locator, String urlString) {
		//Stopwatch timer = Stopwatch.createStarted();
                LoggerUtil.startTimeMeasurement(); // removed Google's timer
		try {
			logger.fine("ActionMethod- navigateTo");
			driver.navigate().to(urlString);
		} catch (Exception e) {
                        LoggerUtil.logException(e, "Got exception in navigateTo method while going to "+urlString);
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}
                stepcount++; //increment step counter
                logger.log(Level.INFO, "stepID: {0}", stepcount);
		LoggerUtil.stopTimeMeasurement("navigateTo "+urlString);
		return Constant.KEYWORD_PASS.getValue();
	}

	public String navigateToRefresh(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted(); //
                LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- navigateToRefresh");
			driver.navigate().refresh();
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}
                stepcount++; //increment step counter
                logger.log(Level.INFO, "stepID: {0}", stepcount);
		logger.info("navigateToRefresh action took ");
                LoggerUtil.stopTimeMeasurement();
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * enterText to a input field using locator as ID
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String enterTextByID(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
                LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- enterTextByID");
			driver.findElement(By.id(ObjectRepository.getProperty(locator))).sendKeys(data);
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar 
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
                
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("enterTextByID action"+stepcount);
               
;
		//logger.info("enterTextByID action took: " + timer + " stepID: " + stepcount++);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * enterText to a input field using locator as Name
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String enterTextByName(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- enterTextByName");
			driver.findElement(By.name(ObjectRepository.getProperty(locator))).sendKeys(data);
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("enterTextByName"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * enterText to a input field using locator as XPath
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String enterTextByXpath(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- enterTextByXpath");
			driver.findElement(By.xpath(ObjectRepository.getProperty(locator))).sendKeys(data);
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();

		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("enterTextByXpath action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * enterText to a input field using locator as CSS Selectors
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String enterTextByCss(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- enterTextByCss");
			if (data.isEmpty()) {
				return "-- No Data is provided --";
			} else {
				driver.findElement(By.cssSelector(ObjectRepository.getProperty(locator))).sendKeys(data);
			}
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("enterTextByCss action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * enterText to a input field using locator as Class Name
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String enterTextByClassName(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- enterTextByClassName");
			driver.findElement(By.className(ObjectRepository.getProperty(locator))).sendKeys(data);
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("enterTextByClassName action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * clickButton using locator as ID
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String clickElementByID(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickElementByID");
			driver.findElement(By.id(ObjectRepository.getProperty(locator))).click();
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickElementByID action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * clickButton using locator as Name
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String clickElementByName(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickElementByName");
			driver.findElement(By.name(ObjectRepository.getProperty(locator))).click();
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();

		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickElementByName action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * clickButton using locator as XPath
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String clickElementByXpath(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickElementByXpath");

			WebDriverWait wait = new WebDriverWait(driver, 30); // TODO: Remove this hardcoding into configuration
																// (Mandar)
			logger.info("Ready to wait");
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(ObjectRepository.getProperty(locator))))
					.click();
			logger.info("Wait until complete");
			logger.fine("ActionMethod- clickElementByXpath");
			// webElement =
			// driver.findElement(By.xpath(ObjectRepository.getProperty(locator)));
			// webElement.click();

			// driver.findElement(By.xpath(ObjectRepository.getProperty(locator))).click();
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
                 LoggerUtil.stopTimeMeasurement("clickElementByXpath action"+stepcount);
		 return Constant.KEYWORD_PASS.getValue();
	}

	public String setCounter(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
                logger.fine("setCounter method "+data);
		 LoggerUtil.startTimeMeasurement();
		try {
			String str = driver.findElement(By.xpath(ObjectRepository.getProperty(locator))).getAttribute("value");

			logger.fine("getAttribute:" + str);

			Integer seatcount = Integer.parseInt(str);
			logger.fine("Value of count is " + seatcount);

			for (int i = seatcount; i > 0; i--) {
				logger.fine("The value of i is: " + i);
				driver.findElement(By.cssSelector("#decr82")).click(); //TODO: This looks like hard coding? Specific to JTicketing.Check with Ankita
				Thread.sleep(1000);
			}

			return Constant.KEYWORD_PASS.getValue() + " ";

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			//logger.info("setCounter: " + timer + " stepID: " + stepcount++);
                   	 stepcount++; //increment step counter
            		 logger.log(Level.INFO, "stepID: {0}", stepcount);
                     LoggerUtil.stopTimeMeasurement("setCounter action "+stepcount);
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}

	}

	/**
	 * clickButton using locator as CSS Selector
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String clickElementByCss(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickElementByCss");
			WebDriverWait wait = new WebDriverWait(driver, 30); // TODO: Remove this hardcoding into configuration
																// (Mandar)
			logger.info("Ready to wait");
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(ObjectRepository.getProperty(locator))))
					.click();
			logger.info("Wait until complete");

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " " + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickElementByCss action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

        /**
	 * Switch to iframe for a specific locator
	 * 
	 * @param locator
	 * 
	 * @return
	 */
	
	public String switchToFrame(String locator) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			//logger.fine("switchToFrame");
			driver.switchTo().frame(driver.findElement(By.xpath(ObjectRepository.getProperty(locator))));		
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();

		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("switchToFrame action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();

	}
	/**
	 * accept alert
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String acceptAlert(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- acceptAlert");
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.alertIsPresent());
			driver.switchTo().alert().accept();
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();

		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("acceptAlert action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();

	}

	/**
	 * dismiss alert
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String dismissAlert(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- dismissAlert");
			driver.switchTo().alert().dismiss();
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();

		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("dismissAlert action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * clickOnLinkByLinkText allows user to click the link by using the complete
	 * text present in the link
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String clickOnLinkByLinkTextByData(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickOnLinkByLinkTextByData");
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.linkText(data)));
			driver.findElement(By.linkText(data)).click();
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickOnLinkByLinkTextByData action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	public String clickOnLinkByLinkTextByLocator(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickOnLinkByLinkTextByLocator");
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions
					.visibilityOfAllElementsLocatedBy(By.linkText(ObjectRepository.getProperty(locator))));
			driver.findElement(By.linkText(ObjectRepository.getProperty(locator))).click();
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickOnLinkByLinkTextByLocator action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();

	}

	/**
	 * clickOnLinkByLinkText allows user to click the link by using the complete
	 * text present in the link
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String clickOnLinkByLinkText(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickOnLinkByLinkText");
			driver.findElement(By.linkText(ObjectRepository.getProperty(locator))).click();
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickOnLinkByLinkText action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();

	}

	/**
	 * clickOnLinkByPartialLinkText allows user to click the link by using the
	 * partial text present in the link
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String clickOnLinkByPartialLinkText(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickOnLinkByPartialLinkText");
			driver.findElement(By.partialLinkText(ObjectRepository.getProperty(locator))).click();
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickOnLinkByPartialLinkText action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();

	}

	/**
	 * fileupload using locator as XPath
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String fileupload(String locator, String filePath) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- fileupload");
			driver.findElement(By.xpath(ObjectRepository.getProperty(locator)))
					.sendKeys(System.getProperty("user.dir") + filePath);

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getStackTrace();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("fileupload action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * scorllDown using locator as XPath
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String scrollDown(String locator, String data) {
             //Name modified for typo: Mandar Wadhavekar
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- scorllDown");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("scrollDown action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	public String scrollDownPage(String locator, String data) {
            //Name modified for typo: Mandar Wadhavekar
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- scorllDownPage");

			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0,800)", "");
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("scrollDownPage action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * scrollUp using locator as XPath
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String scrollUp(String locator, String data) {
            //name modified for typo by MAndar Wadhavekar
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- scorllUp");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(document.body.scrollHeight, 0)");

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("scrollUp action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * selectCheckBoxByCss using locator as XPath
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String selectCheckBoxByCss(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- selectCheckBoxByCss");
			WebElement element = driver.findElement(By.cssSelector(ObjectRepository.getProperty(locator)));
			if (data.equalsIgnoreCase("Yes")) {
				element.click();
			}

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("selectCheckBoxByCss action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * selectClassDropdownByXpath using locator as XPath
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String selectClassDropdownByXpath(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- selectClassDropdownByXpath");
			Thread.sleep(2000);
			Select s = new Select(driver.findElement(By.xpath(ObjectRepository.getProperty(locator))));
			s.selectByVisibleText(data);

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("selectClassDropdownByXpath action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * Application specific Keywords
	 * 
	 */

	/**
	 * clickButton using button text
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String clickButtonByText(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickButtonByText");

			driver.findElement(ByAngular.buttonText(ObjectRepository.getProperty(locator))).click();

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";

		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickButtonByText action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * clickDropdownByXpath using locator as XPath
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String clickDropdownByXpath(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickDropdownByXpath");
			if (data.isEmpty()) {
				return " -- No Data is provided --";
			} else {
				Wait<WebDriver> wait = new WebDriverWait(driver, 30);
				wait.until(
						ExpectedConditions.visibilityOfElementLocated(By.xpath(ObjectRepository.getProperty(locator))));
				driver.findElement(By.xpath(ObjectRepository.getProperty(locator))).click();
				List<WebElement> options = driver.findElements(
						By.xpath(ObjectRepository.getProperty(locator) + "//ul[@class='chzn-results']/li"));
				for (WebElement option : options) {
					if (option.getText().equalsIgnoreCase(data)) {
						option.click();
						break;
					}
				}
			}
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickDropdownByXpath action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}
	
	public String clickDropdownByCss(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickDropdownByCss");
			if (data.isEmpty()) {
				return " -- No Data is provided --";
			} else {
				Wait<WebDriver> wait = new WebDriverWait(driver, 30);
				wait.until(
						ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ObjectRepository.getProperty(locator))));
				driver.findElement(By.cssSelector(ObjectRepository.getProperty(locator))).click();
				List<WebElement> options = driver.findElements(
						By.cssSelector(ObjectRepository.getProperty(locator) + " > div > ul > li"));
				for (WebElement option : options) {
					if (option.getText().equalsIgnoreCase(data)) {
						option.click();
						break;
					}
				}
			}
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickDropdownByCss action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}
 /**
  * TODO: Understand more about this code
  * @return 
  */
	public String waitForPageLoad() {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- waitForPageLoad");
			logger.fine("entered");
			Wait<WebDriver> wait = new WebDriverWait(driver, 30);
			wait.until(new Function<WebDriver, Boolean>() {
				public Boolean apply(WebDriver driver) {
					logger.fine("Current Window State : " + String
							.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState")));
					return String.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"))
							.equals("complete");
				}
			});
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("waitForPageLoad action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	public String elementToBeClickableDropdown(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- elementToBeClickableDropdown");
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(ObjectRepository.getProperty(locator))))
					.click();
			driver.findElement(By.xpath(ObjectRepository.getProperty(locator))).click();
			Thread.sleep(1000);
			List<WebElement> options = driver
					.findElements(By.xpath(ObjectRepository.getProperty(locator) + "/div/ul/li"));
			for (WebElement option : options) {
				if (option.getText().equals(data)) {
					option.click();
					break;
				}
			}
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();

		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("elementToBeClickableDropdown action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * verifyPopupMessage using locator as CSS Selector
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String verifyPopupMessage(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			WebDriverWait wait = new WebDriverWait(driver, 10);
			WebElement element = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.cssSelector(ObjectRepository.getProperty(locator))));
			logger.fine(element.getText());
			return Constant.KEYWORD_PASS.getValue() + " " + element.getText();

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
                   	 stepcount++; //increment step counter
            		 logger.log(Level.INFO, "stepID: {0}", stepcount);
                     LoggerUtil.stopTimeMeasurement("verifyPopupMessage action"+stepcount);
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}

	}

	public String verifyPopupMessageByXpath(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- verifyPopupMessageByXpath");
			WebDriverWait wait = new WebDriverWait(driver, 10);
			WebElement element = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.xpath(ObjectRepository.getProperty(locator))));
			logger.info(element.getText());
			return Constant.KEYWORD_PASS.getValue() + " " + element.getText();

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
                   	 stepcount++; //increment step counter
            		 logger.log(Level.INFO, "stepID: {0}", stepcount);
                     LoggerUtil.stopTimeMeasurement("verifyPopupMessageByXpath action"+stepcount);
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}

	}

	/**
	 * pause
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String pause(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- pause");
			Thread.sleep(2000); //TODO: pick the value from .properties file
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("pause action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();

	}

	/**
	 * clickOnRadioByCss using locator as CSS Selector
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String clickOnRadioByCss(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickOnRadioByCss");
			if (data.equalsIgnoreCase("Yes")) {
				driver.findElement(By.cssSelector(ObjectRepository.getProperty("openfornominationYes"))).click();
			} else if (data.equalsIgnoreCase("No")) {
				driver.findElement(By.cssSelector(ObjectRepository.getProperty("openfornominationNo"))).click();
			}

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickOnRadioByCss action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();

	}

	public String clickOnRadioWithLabel(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickOnRadioWithLabel");
			if (data.equalsIgnoreCase("Yes")) {
				WebDriverWait wait = new WebDriverWait(driver, 30);
				wait.until(ExpectedConditions.elementToBeClickable(
						By.xpath(ObjectRepository.getProperty(locator) + "//label[contains(text(),'Yes')]"))).click();
				driver.findElement(By.xpath(ObjectRepository.getProperty(locator + "//label[contains(text(),'Yes')]")))
						.click();
				logger.fine(locator);
			} else if (data.equalsIgnoreCase("No")) {
				WebDriverWait wait = new WebDriverWait(driver, 30);
				wait.until(ExpectedConditions.elementToBeClickable(
						By.xpath(ObjectRepository.getProperty(locator) + "//label[contains(text(),'No')]"))).click();
				driver.findElement(By.xpath(ObjectRepository.getProperty(locator + "//label[contains(text(),'No')]")))
						.click();
			} else if (data.equalsIgnoreCase("Private")) {
				WebDriverWait wait = new WebDriverWait(driver, 30);
				wait.until(ExpectedConditions.elementToBeClickable(
						By.xpath(ObjectRepository.getProperty(locator) + "//label[contains(text(),'Private')]")))
						.click();
				driver.findElement(
						By.xpath(ObjectRepository.getProperty(locator + "//label[contains(text(),'Private')]")))
						.click();
			} else if (data.equalsIgnoreCase("Create Online Event")) {
				WebDriverWait wait = new WebDriverWait(driver, 30);
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
						ObjectRepository.getProperty(locator) + "//label[contains(text(),'Create Online Event')]")))
						.click();
				driver.findElement(By.xpath(
						ObjectRepository.getProperty(locator + "//label[contains(text(),'Create Online Event')]")))
						.click();
			} else if (data.equalsIgnoreCase("Choose from existing")) {
				WebDriverWait wait = new WebDriverWait(driver, 30);
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
						ObjectRepository.getProperty(locator) + "//label[contains(text(),'Choose from existing')]")))
						.click();
				driver.findElement(By.xpath(
						ObjectRepository.getProperty(locator + "//label[contains(text(),'Choose from existing')]")))
						.click();
			}

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickOnRadioWithLabel action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();

	}

	public String clickOnRadioWithValue(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- clickOnRadioWithValue");
			if (data.equalsIgnoreCase("Yes")) {
				WebDriverWait wait = new WebDriverWait(driver, 30);
				wait.until(ExpectedConditions
						.elementToBeClickable(By.xpath(ObjectRepository.getProperty(locator) + "[@value='1']")))
						.click();
				driver.findElement(By.xpath(ObjectRepository.getProperty(locator + "//input[@value='1']"))).click();
				logger.fine(locator);
			} else if (data.equalsIgnoreCase("No")) {
				WebDriverWait wait = new WebDriverWait(driver, 30);
				wait.until(ExpectedConditions
						.elementToBeClickable(By.xpath(ObjectRepository.getProperty(locator) + "//input[@value='0']")))
						.click();
				driver.findElement(By.xpath(ObjectRepository.getProperty(locator + "//input[@value='0']"))).click();
			}

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("clickOnRadioWithValue action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();

	}

	/**
	 * VerifyAPICallStatusIs200
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String VerifyAPICallStatusIs200(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- VerifyAPICallStatusIs200");
			given().log().all().when().header("Authorization", "Bearer " + ObjectRepository.getProperty("apikey"))
					.get(data).then().log().all().assertThat().statusCode(equalTo(200));

		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("VerifyAPICallStatusIs200 action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();

	}

	/**
	 * enterClearTextByCss using locator as Css
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String enterClearTextByCss(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- enterClearTextByCss");
			driver.findElement(By.cssSelector(ObjectRepository.getProperty(locator))).clear();
			driver.findElement(By.cssSelector(ObjectRepository.getProperty(locator))).sendKeys(data);
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();

		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("enterClearTextByCss action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * enterClearTextByXpath using locator as XPath
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String enterClearTextByXpath(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- enterClearTextByXpath");
			driver.findElement(By.xpath(ObjectRepository.getProperty(locator))).clear();
			driver.findElement(By.xpath(ObjectRepository.getProperty(locator))).sendKeys(data);
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();

		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("enterClearTextByXpath action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * enterClearTextByXpath using locator as ID
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String enterClearTextByID(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- enterClearTextByID");
			driver.findElement(By.id(ObjectRepository.getProperty(locator))).clear();
			driver.findElement(By.id(ObjectRepository.getProperty(locator))).sendKeys(data);
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + e.getMessage();

		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("enterClearTextByID action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * quit browser
	 * 
	 * @param locator
	 * @param data
	 * @return
	 */

	public String quitBrowser(String locator, String data) {
		//Stopwatch timer = Stopwatch.createStarted();
		 LoggerUtil.startTimeMeasurement();
		try {
			logger.fine("ActionMethod- quitBrowser");
			driver.quit();
		} catch (Exception e) {
                        LoggerUtil.logException(e); //added by Mandar Wadhavekar
			return Constant.KEYWORD_FAIL.getValue() + " (Cause of Failure >> " + e.getMessage() + " )";
		}
		 stepcount++; //increment step counter
		 logger.log(Level.INFO, "stepID: {0}", stepcount);
         LoggerUtil.stopTimeMeasurement("quitBrowser action"+stepcount);
		return Constant.KEYWORD_PASS.getValue();
	}

	/**
	 * Not a keyword
	 */

	public void takesScreenshot(String filename, String testStepResult) throws IOException {
		File scrFile = null;
		if (ObjectRepository.getProperty("takescreeshot_all").equals("Y")) {
			try {
				logger.fine("ActionMethod- takesScreenshot");
				scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(scrFile, new File(System.getProperty(Constant.PROJECT_ROOT_DIRECTORY.getValue())
						+ "/screenshots/" + filename + ".png"));
			} catch (Exception e) {
                                LoggerUtil.logException(e); //added by Mandar Wadhavekar
				logger.warning(Constant.ERROR_SCREENSHOT.getValue() + driver);

			}

		} else if (testStepResult.startsWith(Constant.KEYWORD_FAIL.getValue())
				&& ObjectRepository.getProperty("takescreeshot_failure").equals("Y")) {
			try {
				scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(scrFile, new File(System.getProperty(Constant.PROJECT_ROOT_DIRECTORY.getValue())
						+ "/screenshots/" + filename + ".png"));
			} catch (Exception e) {
                                LoggerUtil.logException(e); //added by Mandar Wadhavekar
				logger.warning(Constant.ERROR_SCREENSHOT.getValue() + driver);
			}

		}
	}
}
