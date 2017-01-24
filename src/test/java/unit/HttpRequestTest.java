package unit;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import protocol.HttpRequest;
import protocol.ProtocolException;

public class HttpRequestTest {
	String sampleRequestLine;

	@Before
	public void setUp() {
		sampleRequestLine = "GET /somedir/page.html HTTP/1.1\r";
	}
	
	@Test(expected=ProtocolException.class)
	public void testBadProtocolRequestCode() throws Exception {
		String requestLine = "GET";
		InputStream input = new ByteArrayInputStream(requestLine.getBytes());
		HttpRequest.read(input);
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

	@Test
	public void testGetHeader() throws Exception {
		String key = "testHeader".toLowerCase();
		String value = "testValue";
		String req = sampleRequestLine + key + ": " + value + "\r";

		InputStream input = new ByteArrayInputStream(req.getBytes());
		HttpRequest request = HttpRequest.read(input);

		Map<String, String> headers = request.getHeader();
		String actual = headers.get(key);
		assertEquals(value, actual);
	}
	
	@Test
	public void testGetHeaders() throws Exception {
		String key1 = "testHeader".toLowerCase();
		String value1 = "testValue";
		String key2 = "yolo";
		String value2 = "swag";
		
		String req = sampleRequestLine + key1 + ": " + value1 + "\r" +
						key2 + ": " + value2 + "\r";
		InputStream input = new ByteArrayInputStream(req.getBytes());
		HttpRequest request = HttpRequest.read(input);

		Map<String, String> headers = request.getHeader();
		assertEquals(value1, headers.get(key1));
		assertEquals(value2, headers.get(key2));
	}
}
