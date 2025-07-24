package gettingStarted;


import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;

public class AutoHealLocators {

	public WebElement getElementWithFallback(WebDriver driver, By mainLocator, List<By> fallbackLocators) {
	    try {
	        // Try the main/original locator first
	        return driver.findElement(mainLocator);
	    } catch (NoSuchElementException e) {
	        // If not found, try alternatives one by one
	        for (By locator : fallbackLocators) {
	            try {
	                return driver.findElement(locator);
	            } catch (NoSuchElementException ex) {
	                // Try next
	            }
	        }
	        // If none found, throw error
	        throw new NoSuchElementException("Element not found by main or fallback locators.");
	    }
	}
	
	
	public void test() {
	    WebDriver driver = new ChromeDriver();
	    driver.get("https://www.saucedemo.com/");
	    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
	    driver.manage().window().maximize();

	    // Main/original locators
	    By userNameMain = By.id("user-name");
	    By passwordMain = By.id("password");
	    By loginBtnMain = By.id("login-button");

	    // Fallback locators (from your extracted list)
	    List<By> userNameFallback = Arrays.asList(
	        By.name("user-name"),
	        By.xpath("//input[@id='user-name']"),
	        By.cssSelector("#user-name"),
	        By.className("input_error")
	    );

	    List<By> passwordFallback = Arrays.asList(
	        By.name("password"),
	        By.xpath("//input[@id='password']"),
	        By.cssSelector("#password"),
	        By.className("input_error")
	    );

	    List<By> loginBtnFallback = Arrays.asList(
	        By.name("login-button"),
	        By.xpath("//input[@id='login-button']"),
	        By.cssSelector("#login-button"),
	        By.className("submit-button")
	    );

	    getElementWithFallback(driver, userNameMain, userNameFallback).sendKeys("standard_user");
	    getElementWithFallback(driver, passwordMain, passwordFallback).sendKeys("secret_sauce");
	    getElementWithFallback(driver, loginBtnMain, loginBtnFallback).click();

	    // ...rest of your test
	    driver.quit();
	}


}
