package integration;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.InetAddress;

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

import app.SimpleWebServer;
import protocol.Protocol;
import server.Server;

public class HeadRequestTests {
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
	public void testHead200OkResponse() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + fileName);
		HttpRequest request = requestFactory.buildHeadRequest(url);
		HttpResponse response = request.execute();

		int expected = 200;
		int actual = response.getStatusCode();
		assertEquals(expected, actual);
	}

	@Test
	public void testHead200OkResponseFromDefaultFile() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port);
		HttpRequest request = requestFactory.buildHeadRequest(url);
		HttpResponse response = request.execute();

		int expected = 200;
		int actual = response.getStatusCode();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testHead404NotFoundResponse() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/notFound.txt");
		HttpRequest request = requestFactory.buildHeadRequest(url);

		try {
			request.execute();
		} catch (HttpResponseException e) {
			int expected = 404;
			int actual = e.getStatusCode();
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testHead400BadRequestResponse() throws Exception {
		String emptyDir = "emptyDir";
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + emptyDir);
		HttpRequest request = requestFactory.buildHeadRequest(url);

		File f = new File("web", emptyDir);
		f.mkdir();

		try {
			request.execute();
		} catch (HttpResponseException e) {
			int expected = 400;
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
