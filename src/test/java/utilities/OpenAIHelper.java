package utilities;

import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;

public class OpenAIHelper {


    public static String GetLocatorsForPageAsJson(String pageSource) {

    	  String apiKey = "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";

       var client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(String.format("Return only a JSON array of locators from the page source %s, provide the locator for the elements described in JSON format like {\"locatorName\":\"name of the locator should match the label name if exist\", \"locatorType\":\"id|name|css|xpath|linkText\", \"locator\":\"value\"}. Do not include any explanations, markdown formatting, or additional text.",pageSource))
                .model(ChatModel.GPT_4_1)
                .build();
        ChatCompletion chatCompletion = client.chat().completions().create(params);

        return chatCompletion.choices().get(0).message()._content().toString();
    }
}
