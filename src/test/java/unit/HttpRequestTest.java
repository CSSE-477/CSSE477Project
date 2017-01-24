package unit;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import protocol.HttpRequest;

public class HttpRequestTest {
	String sampleRequestLine;

	@Before
	public void setUp() {
		sampleRequestLine = "GET /somedir/page.html HTTP/1.1\r";
	}

	@Test
	public void testGetMethod() throws Exception {
		InputStream input = new ByteArrayInputStream(sampleRequestLine.getBytes());
		HttpRequest request = HttpRequest.read(input);
		
		String expected = "GET";
		String actual = request.getMethod();
		assertEquals(expected, actual);
	}

	@Test
	public void testGetUri() throws Exception {
		InputStream input = new ByteArrayInputStream(sampleRequestLine.getBytes());
		HttpRequest request = HttpRequest.read(input);
		
		String expected = "/somedir/page.html";
		String actual = request.getUri();
		assertEquals(expected, actual);
	}

	@Test
	public void testGetVersion() throws Exception {
		InputStream input = new ByteArrayInputStream(sampleRequestLine.getBytes());
		HttpRequest request = HttpRequest.read(input);
		
		String expected = "HTTP/1.1";
		String actual = request.getVersion();
		assertEquals(expected, actual);
	}
	
//	@Test
//	public void testGetHeader() throws Exception {
//		
//	}
}
