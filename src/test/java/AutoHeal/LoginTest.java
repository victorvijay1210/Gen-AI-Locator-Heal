package AutoHeal;

import java.io.FileWriter;
import java.time.Duration;import com.fasterxml.jackson.module.kotlin.ReflectionCache.BooleanTriState.False;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class LoginTest {
    private OpenAIClient client;
    private WebDriver driver;
    @BeforeTest
    public void Setup() {
        String apiKey = "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";
        client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();

        driver = new ChromeDriver();

        driver.navigate().to("https://www.saucedemo.com/");
    }


    @Test(priority = 0)
	public void testLogin() {
	    String prompt = """
	  Return a JSON object for all user-interactable elements (like input fields, buttons) on the page https://www.saucedemo.com/.
Each key must be the id or name of the field (for example, "user-name", "password", "login-button").
Each value must be an array of all possible valid locators for that field node, where each locator is an object with:
"Locator": the locator value (for example, the XPath, CSS selector, name, etc.)
"LocatorType": must be one of "Xpath", "Name", or "Css".
Return only the JSON object, and nothing else—no explanation, no markdown, no extra text.

	    """;

	    // You can use GPT-4o or gpt-4-0125-preview or similar model
	    ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
	            .addUserMessage(prompt)
	            .model(ChatModel.GPT_4_1) // or ChatModel.GPT_4_1
	            .build();

	    ChatCompletion chatCompletion = client.chat().completions().create(params);
	    String message = chatCompletion.choices().get(0).message()._content().toString();

	    // Clean up possible markdown (rare, but just in case)
	    message = message.replaceAll("```json", "").replaceAll("```", "").trim();

	    System.out.println("Response from GPT:\n" + message);

	    // Optional: Write to file
	    try (FileWriter writer = new FileWriter("src/test/java/JsonFile/GPTLocator.json")) {
	        writer.write(message);
	        System.out.println("Locators saved to Locator.json");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@Test(enabled = true,priority = 1)
	public void test() {
	    
		// driver = new ChromeDriver();
	   // driver.get("https://www.saucedemo.com/");
	    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
	    driver.manage().window().maximize();

	    LocatorAutoHeal.getElementWithAutoHeal(driver, By.id("user")).sendKeys("standard_user");
	    LocatorAutoHeal.getElementWithAutoHeal(driver, By.id("password")).sendKeys("secret_sauce");
	    LocatorAutoHeal.getElementWithAutoHeal(driver, By.id("login-button")).click();
	    driver.quit();
	}

}
