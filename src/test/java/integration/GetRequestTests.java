package integration;

import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;

import server.Server;
import utilities.FileCreationUtility;

import java.io.File;

public class GetRequestTests {
	private static Server server;
	private static String rootDirectory;
	private static String fileName;
	private static int port;
	
	private final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private final static JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private static HttpRequestFactory requestFactory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fileName = "index.html";
		rootDirectory = "web";
		port = 80;

		server = new Server(rootDirectory, port);
		Thread runner = new Thread(server);
		runner.start();
		
		// wait until the server is ready
		while (server.isStoped()) {}
		
	    requestFactory =
	        HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
	            @Override
	          public void initialize(HttpRequest request) {
	            request.setParser(new JsonObjectParser(JSON_FACTORY));
	          }
	        });
	}

	@Test
	public void testGet404NotFound() throws Exception {
		GenericUrl url = new GenericUrl("http://localhost:" + port + "/notFound.txt");
		HttpRequest request = requestFactory.buildGetRequest(url);
		
		try {
			request.execute();
		} catch (HttpResponseException e) {
			int expected = 404;
			int actual = e.getStatusCode();
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testGet200OkResponse() throws Exception {
		GenericUrl url = new GenericUrl("http://localhost:" + port + "/" + fileName);
		HttpRequest request = requestFactory.buildGetRequest(url);
		
		HttpResponse response = request.execute();
		int expected = 200;
		int actual = response.getStatusCode();
		assertEquals(expected, actual);
	}

	@Test
    public void testGet200AndCorrectObjectRespons() throws Exception {
	    GenericUrl url = new GenericUrl("http://localhost:" + port + "/" + "upload.html");
	    HttpRequest request = requestFactory.buildGetRequest(url);
        HttpResponse response = request.execute();
        int expected = 200;
        int actual = response.getStatusCode();
        assertEquals(expected, actual);
        // TODO: Make the below 512 bytes more flexible
        byte[] bytes = new byte[512];
        response.getContent().read(bytes, 0, 512);
        FileCreationUtility.writeToTestFile(new String(bytes, "UTF-8"));
        File file1 = new File("file1.txt");
        File file2 = new File("file2.txt");
        boolean isTwoEqual = FileUtils.contentEquals(file1, file2);
        assertTrue(isTwoEqual);
    }

	@AfterClass
	public static void tearDownAfterClass() {
		server.stop();
	}
}