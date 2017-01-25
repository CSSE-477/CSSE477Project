package integration;

import static org.junit.Assert.*;

import app.SimpleWebServer;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
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

import server.Server;
import utils.FileCreationUtility;

import java.io.File;
import java.net.InetAddress;

public class GetRequestTests {
	private static Server server;
	private static String fileName;
	private static int port;
	
	private final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private final static JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private static HttpRequestFactory requestFactory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fileName = "index.html";
		String rootDirectory = "web";
		port = 8080;
		server = new Server(port, SimpleWebServer.getPopulatedFactoryHash(rootDirectory));
		Thread runner = new Thread(server);
		runner.start();

		int sleepAmount = 1000;
		int retries = 10;
		while(!server.isReady()) {
            if (retries > 0) {
                Thread.sleep(sleepAmount);
            }
            else{
                break;
            }
            retries = retries - 1;
        }
	    requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(new JsonObjectParser(JSON_FACTORY)));
	}

	@Test
	public void testGet404NotFound() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/notFound.txt");
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
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + fileName);
		HttpRequest request = requestFactory.buildGetRequest(url);
		
		HttpResponse response = request.execute();
		int expected = 200;
		int actual = response.getStatusCode();
		assertEquals(expected, actual);
	}

	@Test
	public void testGet200OkResponseDefaultFile() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/");
		HttpRequest request = requestFactory.buildGetRequest(url);
		
		HttpResponse response = request.execute();
		int expected = 200;
		int actual = response.getStatusCode();
		assertEquals(expected, actual);
	}

	@Test
    public void testGet200AndCorrectObjectResponse() throws Exception {
	    GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + "upload.html");
	    HttpRequest request = requestFactory.buildGetRequest(url);
        HttpResponse response = request.execute();
        int expected = 200;
        int actual = response.getStatusCode();
        assertEquals(expected, actual);
        byte[] bytes = new byte[512];
        // response.getContent().read(bytes, 0, 512);
        FileCreationUtility.writeToTestFile(new String(bytes, "UTF-8"));
        File file1 = new File("file1.txt");
        File file2 = new File("file2.txt");
        boolean isTwoEqual = FileUtils.contentEquals(file1, file2);
        assertTrue(isTwoEqual);
    }

	@Test
	public void testGet404NotFoundResponseEmptyDirectory() throws Exception {
		String emptyDir = "emptyGetDir";
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + emptyDir);
		HttpRequest request = requestFactory.buildGetRequest(url);

		File f = new File("web", emptyDir);
		f.mkdir();

		try {
			request.execute();
		} catch (HttpResponseException e) {
			int expected = 404;
			int actual = e.getStatusCode();
			assertEquals(expected, actual);
		} finally {
			f.delete();
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		server.stop();
	}
}