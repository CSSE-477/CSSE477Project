package integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.api.client.http.ByteArrayContent;
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

public class PutRequestTests {
	private static Server server;
	private static String rootDirectory;
	private static String fileName;
	private static String directoryName;
	private static int port;
	
	private final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private final static JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private static HttpRequestFactory requestFactory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		fileName = "test.txt";
		directoryName = "directory";
		rootDirectory = "web";
		
		File testFile = new File(rootDirectory, fileName);
		if (testFile.exists()) {
			testFile.delete();
		}
		testFile.createNewFile();
		
		FileWriter writer = new FileWriter(testFile);
		writer.write("This is the initialization content in file.txt!");

		writer.close();
		
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
	public void testPut400BadRequestFileIsDirectory() throws Exception {
//	    System.out.println(InetAddress.getLocalHost().getHostName());
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + directoryName);
		String requestBody = "This is PUT request content!";
		HttpRequest request = requestFactory.buildPutRequest(url, ByteArrayContent.fromString("text/plain", requestBody));
		request.getHeaders().setContentType("application/json");
		
		try {
			request.execute();
		} catch (HttpResponseException e) {
			int expectedCode = 400;
			int actualCode = e.getStatusCode();
			assertEquals(expectedCode, actualCode);
		}
	}
	
	@Test
	public void testPut200OKFileOverwritten() throws Exception {
//	    System.out.println(InetAddress.getLocalHost().getHostName());
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + fileName);
		String requestBody = "This is PUT request content!";
		HttpRequest request = requestFactory.buildPutRequest(url, ByteArrayContent.fromString("text/plain", requestBody));
		request.getHeaders().setContentType("application/json");
		
		HttpResponse response = request.execute();
		int expectedCode = 200;
		int actualCode = response.getStatusCode();
		assertEquals(expectedCode, actualCode);
		
		String actualContent = requestBody;
		byte[] responseContentByteArray = new byte[Math.toIntExact(response.getHeaders().getContentLength())];
		response.getContent().read(responseContentByteArray);
		assertTrue(actualContent.equals(new String(responseContentByteArray)));
	}
	
	@Test
	public void testPut500InternalServerErrorFileLocked() throws Exception {
//	    System.out.println(InetAddress.getLocalHost().getHostName());
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + fileName);
		String requestBody = "This is PUT request content!";
		HttpRequest request = requestFactory.buildPutRequest(url, ByteArrayContent.fromString("text/plain", requestBody));
		request.getHeaders().setContentType("application/json");
		
		File testFile = new File(rootDirectory, fileName);
		testFile.setWritable(false);
		
		try {
			request.execute();
		} catch (HttpResponseException e) {
			int expectedCode = 500;
			int actualCode = e.getStatusCode();
			assertEquals(expectedCode, actualCode);
		}
		
		testFile.setWritable(true);
	}

	@AfterClass
	public static void tearDownAfterClass() {
		server.stop();
		
		File testFile = new File(rootDirectory, fileName);
		if (testFile.exists()) {
			testFile.delete();
		}
	}
}