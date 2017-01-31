package unit;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.ProtocolConfiguration;
import protocol.ResponseHeaders;

public class HttpResponseBuilderTest {

	private ProtocolConfiguration protocol;
	
	@Before
	public void setUp() {
		this.protocol = new ProtocolConfiguration();
	}
	
	@Test
	public void testCreate200Ok() throws Exception {
	    // Create temp file.
	    File temp = File.createTempFile("pattern", ".html");

	    // Delete temp file when program exits.
	    temp.deleteOnExit();

	    // Write to temp file
	    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
	    out.write("this is the file string");
	    out.close();

	    HttpResponseBuilder builder = new HttpResponseBuilder(200, protocol)
				.putHeader(this.protocol.getResponseHeader(ResponseHeaders.CONTENT_TYPE), "text/html").setFile(temp);
	    HttpResponse res = builder.generateResponse();

	    Map<String, String> header = res.getHeader();
	    assertEquals("text/html", header.get(this.protocol.getResponseHeader(ResponseHeaders.CONTENT_TYPE)));
	    assertEquals(temp, res.getFile());

		int actualStatus = res.getStatus();
		int expectedStatus = 200;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = this.protocol.getPhrase(expectedStatus);

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}
	
	@Test
	public void testCreate400BadRequest() {

		HttpResponseBuilder builder = new HttpResponseBuilder(400, this.protocol);
		HttpResponse res = builder.generateResponse();

		int actualStatus = res.getStatus();
		int expectedStatus = 400;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = this.protocol.getPhrase(expectedStatus);

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}

	@Test
	public void testCreate404NotFound() {
		HttpResponseBuilder builder = new HttpResponseBuilder(404, this.protocol);
		HttpResponse res = builder.generateResponse();

		int actualStatus = res.getStatus();
		int expectedStatus = 404;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = this.protocol.getPhrase(expectedStatus);

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}
	
	@Test
	public void testCreate505NotSupported() {
		HttpResponseBuilder builder = new HttpResponseBuilder(505, this.protocol);
		HttpResponse res = builder.generateResponse();
		int actualStatus = res.getStatus();
		int expectedStatus = 505;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = this.protocol.getPhrase(expectedStatus);

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}
	
	@Test
	public void testCreate304NotModified() {
		HttpResponseBuilder builder = new HttpResponseBuilder(304, this.protocol);
		HttpResponse res = builder.generateResponse();

		int actualStatus = res.getStatus();
		int expectedStatus = 304;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = this.protocol.getPhrase(expectedStatus);

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}

	@Test
	public void testCreate500InternalServerError() {
		HttpResponseBuilder builder = new HttpResponseBuilder(500, this.protocol);
		HttpResponse res = builder.generateResponse();

		int actualStatus = res.getStatus();
		int expectedStatus = 500;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = this.protocol.getPhrase(expectedStatus);

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}

	@Test
	public void testCreate501NotImplemented() {
		HttpResponseBuilder builder = new HttpResponseBuilder(501, this.protocol);
		HttpResponse res = builder.generateResponse();

		int actualStatus = res.getStatus();
		int expectedStatus = 501;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = this.protocol.getPhrase(expectedStatus);

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}
}
