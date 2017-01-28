package unit;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import protocol.HttpResponse;
import protocol.Protocol;

public class HttpResponseFactoryTest {
	private String connection;
	
	@Before
	public void setUp() {
		connection = Protocol.CLOSE;
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
	    
	    HttpResponse res = HttpResponseFactory.create200OK(temp, connection);

	    Map<String, String> header = res.getHeader();
	    assertEquals("text/html", header.get(Protocol.CONTENT_TYPE));
	    assertEquals(temp, res.getFile());

		int actualStatus = res.getStatus();
		int expectedStatus = Protocol.OK_CODE;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = Protocol.OK_TEXT;

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}
	
	@Test
	public void testCreate400BadRequest() {
		HttpResponse res = HttpResponseFactory.create400BadRequest(connection);

		int actualStatus = res.getStatus();
		int expectedStatus = Protocol.BAD_REQUEST_CODE;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = Protocol.BAD_REQUEST_TEXT;

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}

	@Test
	public void testCreate404NotFound() {
		HttpResponse res = HttpResponseFactory.create404NotFound(connection);

		int actualStatus = res.getStatus();
		int expectedStatus = Protocol.NOT_FOUND_CODE;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = Protocol.NOT_FOUND_TEXT;

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}
	
	@Test
	public void testCreate505NotSupported() {
		HttpResponse res = HttpResponseFactory.create505NotSupported(connection);

		int actualStatus = res.getStatus();
		int expectedStatus = Protocol.NOT_SUPPORTED_CODE;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = Protocol.NOT_SUPPORTED_TEXT;

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}
	
	@Test
	public void testCreate304NotModified() {
		HttpResponse res = HttpResponseFactory.create304NotModified(connection);

		int actualStatus = res.getStatus();
		int expectedStatus = Protocol.NOT_MODIFIED_CODE;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = Protocol.NOT_MODIFIED_TEXT;

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}

	@Test
	public void testCreate500InternalServerError() {
		HttpResponse res = HttpResponseFactory.create500InternalServerError(connection);

		int actualStatus = res.getStatus();
		int expectedStatus = Protocol.INTERNAL_SERVER_ERROR_CODE;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = Protocol.INTERNAL_SERVER_ERROR_TEXT;

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}

	@Test
	public void testCreate501NotImplemented() {
		HttpResponse res = HttpResponseFactory.create501NotImplemented(connection);

		int actualStatus = res.getStatus();
		int expectedStatus = Protocol.NOT_IMPLEMENTED_CODE;
		String actualPhrase = res.getPhrase();
		String expectedPhrase = Protocol.NOT_IMPLEMENTED_TEXT;

		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedPhrase, actualPhrase);
	}
}
