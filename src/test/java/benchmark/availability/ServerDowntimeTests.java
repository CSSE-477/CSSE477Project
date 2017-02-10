package benchmark.availability;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;

public class ServerDowntimeTests {
	private static HttpRequestFactory requestFactory;
	private final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private final static JsonFactory JSON_FACTORY = new JacksonFactory();

	@BeforeClass
	public static void setUpBeforeClass() {
		requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(new JsonObjectParser(JSON_FACTORY)));
	}

	@Test
	public void testServerDowntimeToRestart() throws Exception {
		int attempts = 10;
		double totalTimeToRespond = 0.0;

		for (int i = 0; i < attempts; i++) {
			String hostPort = "http://477-19.csse.rose-hulman.edu:8080";
			GenericUrl url = new GenericUrl(hostPort + "/serverexplosion.bat");
			HttpRequest request = requestFactory.buildGetRequest(url);
			Future<HttpResponse> f = request.executeAsync();
	
			long failedRequestSentTime = System.nanoTime();
			try {
				f.get(35, TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {
				// swallow whole
				// no spitting
			}
	
			// build a valid request to test response
			url = new GenericUrl(hostPort);
			request = requestFactory.buildGetRequest(url);
			long successRequestSentTime = System.nanoTime();
	
			while (true) {
				try {
					successRequestSentTime = System.nanoTime();
					request.execute();
					break;
				} catch (Exception e) {
					// failed because server was still down
				}
			}
	
			totalTimeToRespond += (successRequestSentTime - failedRequestSentTime) / Math.pow(10.0, 9.0);
		}
		
		System.out.println("It took " + totalTimeToRespond / attempts + " seconds on average to respond in " + attempts + " trials.");
	}
}
