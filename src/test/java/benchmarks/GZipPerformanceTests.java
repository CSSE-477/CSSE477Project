package benchmarks;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;

public class GZipPerformanceTests {
	private String defaultFileName;

	private final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private final static JsonFactory JSON_FACTORY = new JacksonFactory();

	private static HttpRequestFactory requestFactory;

	@Before
	public void setUp() {
		this.defaultFileName = "index.html";

	    requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(new JsonObjectParser(JSON_FACTORY)));
	}

	@Test
	public void testResponseTime() throws Exception{
		long total = 0L;

		for (int i=0; i < 1000; i++) {
			GenericUrl url = new GenericUrl("http://477-06.csse.rose-hulman.edu:8080/" + this.defaultFileName);
			HttpRequest request = requestFactory.buildGetRequest(url);
			
			long startTime = System.currentTimeMillis();
			request.execute();
			long endTime = System.currentTimeMillis();
			
			total += endTime-startTime;
		}

		Double averageTime = total/1000.0;
		System.err.println("Average response time over 1000 requests: " + averageTime);
		assertTrue(true);
	}
}
