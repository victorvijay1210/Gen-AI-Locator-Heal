package gettingStarted;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.ai.LoginPage;
import utilities.LocatorReader;
import utilities.OpenAIHelper;


public class TestNGTest {

    private OpenAIClient client;
    private WebDriver driver;

    @BeforeTest
    public void Setup() {
        String apiKey = "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";
        client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();

        driver = new ChromeDriver();

        driver.navigate().to("http://eaapp.somee.com/Account/Login");
    }



    @Test
    public void testLogin() {

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage("Get the page source from http://eaapp.somee.com and get all the locators like ID, Name, XPath and CSS")
                .model(ChatModel.GPT_4_1)
                .build();
        ChatCompletion chatCompletion = client.chat().completions().create(params);

        String message = chatCompletion.choices().get(0).message()._content().toString();

        System.out.println("Response from GPT:" + message);
    }

    @Test
    public void testLoginForUserFromAI() {
        System.out.println("Testing User Login from AI Locators");

        var jsonResponse = OpenAIHelper.GetLocatorsForPageAsJson(driver.getPageSource());

        LoginPage loginPage = new LoginPage(driver, jsonResponse);
        loginPage.performLogin("admin", "password");
    }

    @Test
    public void testLogoff() {
        System.out.println("Testing the log off operation");
    }

    @Test
    public void testSettings() {
        System.out.println("Testing the settings operation");
    }

}
