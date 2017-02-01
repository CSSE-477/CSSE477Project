/*
 * DeleteRequestTest.java
 * Jan 25, 2017
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */
 
package integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.InetAddress;

import org.junit.AfterClass;
import org.junit.Before;
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

/**
 * 
 * @author Jesse Shellabarger
 */
public class DeleteRequestTests {

	private static Server server;
	private static File testFile;
	private static int port;
	
	private final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private final static JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private static HttpRequestFactory requestFactory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		String rootDirectory = "./web";
		testFile = new File(rootDirectory, "test.txt");
		
		port = 8080;
		server = new Server(port);
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
	
	@Before
	public void setUpBeforeMethod() throws Exception {
		if (!testFile.exists()) testFile.createNewFile();
	}
	
	@Test
	public void test404Response() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/notFound.txt");
		HttpRequest request = requestFactory.buildDeleteRequest(url);
		
		try {
			request.execute();
		} catch (HttpResponseException e) {
			int expected = 404;
			int actual = e.getStatusCode();
			assertEquals(expected, actual);
		}
	}
	
	@Test
	public void test200Response() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/test.txt");
		HttpRequest request = requestFactory.buildDeleteRequest(url);
		
		HttpResponse response = request.execute();
		int expected = 200;
		int actual = response.getStatusCode();
		assertEquals(expected, actual);
		
		assertTrue(!testFile.exists());
	}
	
	@Test
	public void test200ResponseHeaders() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/test.txt");
		HttpRequest request = requestFactory.buildDeleteRequest(url);
		
		HttpResponse response = request.execute();
		int expected = 200;
		int actual = response.getStatusCode();
		assertEquals(expected, actual);
		
		long length = response.getHeaders().getContentLength();
		assertEquals(0L, length);
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		server.stop();
		if (testFile.exists()) testFile.delete();
	}

}
