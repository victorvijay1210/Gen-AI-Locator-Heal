package gettingStarted;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.testng.annotations.Test;

import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class APITest {
	
	public static void disableSslVerification() {
	    try {
	        TrustManager[] trustAllCerts = new TrustManager[]{
	            (TrustManager) new X509TrustManager() {
	                public X509Certificate[] getAcceptedIssuers() { return null; }
	                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
	                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
	            }
	        };

	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static String getEndpointFromSwaggerUsingOpenAI(String swaggerJson) {
	    String apiKey = "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"; // Your OpenAI API key

	    var client = OpenAIOkHttpClient.builder()
	            .apiKey(apiKey)
	            .build();

	    ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
	            .addUserMessage(String.format(
	                    "Given this Swagger/OpenAPI JSON, return only the GET endpoint for 'favorites' as a plain string URL. Do not include explanations or formatting. JSON: %s",
	                    swaggerJson
	            ))
	            .model(ChatModel.GPT_4_1)
	            .build();

	    ChatCompletion chatCompletion = client.chat().completions().create(params);
	    return chatCompletion.choices().get(0).message()._content().toString().trim();
	}
	

	    

	@Test
	public void testFavoritesEndpointFromOpenAI() throws IOException {
		disableSslVerification();
	    // Fetch swagger
	    String swaggerUrl = "https://airportgap.com/docs";
	    String swaggerJson = new String(new URL(swaggerUrl).openStream().readAllBytes());

	    // Get endpoint dynamically from OpenAI
	    String endpoint = getEndpointFromSwaggerUsingOpenAI(swaggerJson);
	    System.out.println("Extracted endpoint: " + endpoint);
	    RestAssured.useRelaxedHTTPSValidation();
	    // Hit API using Rest Assured
	    Response response = RestAssured
	            .given()
	            .header("Authorization", "Bearer i6D129vkuYbbYpzxffafu9CP") // Replace with real token
	            .when()
	            .get(endpoint)
	            .then()
	            .extract()
	            .response();

	    // Assert response code
	    assertEquals(response.getStatusCode(), 200);
	    System.out.println("Response: " + response.getBody().asPrettyString());
	}

}
