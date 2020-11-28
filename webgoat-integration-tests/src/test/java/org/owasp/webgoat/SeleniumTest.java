package org.owasp.webgoat;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class SeleniumTest extends IntegrationTest {

	private static String OS = System.getProperty("os.name").toLowerCase();

	static {
		if (null == System.getProperty("webdriver.gecko.driver")) {
			if (OS.indexOf("win") > -1) {
				System.setProperty("webdriver.gecko.driver", "C:\\Program Files\\Mozilla Firefox\\geckodriver.exe");
			} else if (OS.indexOf("mac") > -1) {
				System.setProperty("webdriver.gecko.driver", "/Applications/Firefox.app/Contents/MacOS/geckodriver");
			} else {
				System.setProperty("webdriver.gecko.driver","/usr/local/bin/geckodriver");
			}
		}
	}
	private WebDriver driver;

	@BeforeEach
	public void setUpAndLogin() {
		try {
			FirefoxBinary firefoxBinary = new FirefoxBinary();
			firefoxBinary.addCommandLineOptions("--headless");

			FirefoxOptions firefoxOptions = new FirefoxOptions();
			firefoxOptions.setBinary(firefoxBinary);
			driver = new FirefoxDriver(firefoxOptions);
			driver.get(url("/login"));
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			// Login
			driver.findElement(By.name("username")).sendKeys(getWebgoatUser());
			driver.findElement(By.name("password")).sendKeys("password");
			driver.findElement(By.className("btn")).click();

			// Check if user exists. If not, create user.
			if (driver.getCurrentUrl().equals(url("/login?error"))) {
				driver.get(url("/registration"));
				driver.findElement(By.id("username")).sendKeys(getWebgoatUser());
				driver.findElement(By.id("password")).sendKeys("password");
				driver.findElement(By.id("matchingPassword")).sendKeys("password");
				driver.findElement(By.name("agree")).click();
				driver.findElement(By.className("btn-primary")).click();
			}
		} catch (IllegalStateException e) {
			System.err.println("Web driver not found here: "+System.getProperty("webdriver.gecko.driver"));
		}

	}

	@AfterEach
	public void tearDown() {
		try {
			driver.close();
		} catch (Exception e) {

		}
	}

	@Test
	public void sqlInjection() {
		
		if (null==driver) return;

		driver.get(url("/start.mvc#lesson/SqlInjection.lesson"));
		driver.get(url("/start.mvc#lesson/SqlInjection.lesson/1"));
		driver.findElement(By.id("restart-lesson-button")).click();
		driver.get(url("/start.mvc#lesson/SqlInjection.lesson/0"));
		driver.get(url("/start.mvc#lesson/SqlInjection.lesson/1"));
		driver.findElement(By.name("query")).sendKeys(SqlInjectionLessonTest.sql_2);
		driver.findElement(By.name("query")).submit();

		driver.get(url("/start.mvc#lesson/SqlInjection.lesson/2"));
		driver.findElements(By.name("query")).get(1).sendKeys(SqlInjectionLessonTest.sql_3);
		driver.findElements(By.name("query")).get(1).submit();

		driver.get(url("/start.mvc#lesson/SqlInjection.lesson/3"));
		driver.findElements(By.name("query")).get(2).sendKeys(SqlInjectionLessonTest.sql_4_drop);
		driver.findElements(By.name("query")).get(2).submit();

		driver.get(url("/start.mvc#lesson/SqlInjection.lesson/3"));
		driver.findElements(By.name("query")).get(2).clear();
		driver.findElements(By.name("query")).get(2).sendKeys(SqlInjectionLessonTest.sql_4_add);
		driver.findElements(By.name("query")).get(2).submit();
		driver.findElements(By.name("query")).get(2).clear();
		driver.findElements(By.name("query")).get(2).sendKeys(SqlInjectionLessonTest.sql_4_drop);
		driver.findElements(By.name("query")).get(2).submit();

		driver.get(url("/start.mvc#lesson/SqlInjection.lesson/4"));
		driver.findElements(By.name("query")).get(3).sendKeys(SqlInjectionLessonTest.sql_5);
		driver.findElements(By.name("query")).get(3).submit();

		driver.get(url("/start.mvc#lesson/SqlInjection.lesson/8"));
		driver.findElement(By.name("account")).sendKeys("Smith'");
		driver.findElement(By.name("operator")).sendKeys("OR");
		driver.findElement(By.name("injection")).sendKeys("'1'='1");
		driver.findElement(By.name("Get Account Info")).click();

		driver.get(url("/start.mvc#lesson/SqlInjection.lesson/9"));
		driver.findElement(By.name("userid")).sendKeys(SqlInjectionLessonTest.sql_10_userid);
		driver.findElement(By.name("login_count")).sendKeys(SqlInjectionLessonTest.sql_10_login_count);
		driver.findElements(By.name("Get Account Info")).get(1).click();
	}

}
