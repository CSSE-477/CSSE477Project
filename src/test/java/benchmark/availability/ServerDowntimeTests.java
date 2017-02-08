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
		GenericUrl url = new GenericUrl("http://477-19.csse.rose-hulman.edu:8080/serverexplosion.bat");
		HttpRequest request = requestFactory.buildGetRequest(url);
		Future<HttpResponse> f = request.executeAsync();
		
		try {
			long failedRequestSentTime = System.currentTimeMillis();
			f.get(35, TimeUnit.MILLISECONDS);			
		} catch (TimeoutException e) {
			// swallow whole
			// no spitting
		}
		
//		while (true) {
//			
//		}
	}
}
