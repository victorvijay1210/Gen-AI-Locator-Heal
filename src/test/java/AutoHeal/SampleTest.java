package AutoHeal;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class SampleTest {

	@Test
	public void test() {
		// TODO Auto-generated method stub
		  WebDriver driver = new ChromeDriver();
		  driver.get("https://www.saucedemo.com/");
		  driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		  driver.manage().window().maximize();
		  driver.findElement(By.id("user")).sendKeys("standard_user");
		  driver.findElement(By.id("password")).sendKeys("secret_sauce");
		  driver.findElement(By.id("login-button")).click();
	}

}
