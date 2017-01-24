package unit;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import protocol.HttpResponse;

public class HttpResponseTest {
	private String version;
	private int status;
	private String phrase;
	private Map<String, String> header;
	private File file;

	@Before
	public void setUp() {
		version = "defaultVersion";
		status = 200;
		phrase = "defaultPhrase";
		header = new HashMap<>();
		file = null;
	}

	@Test
	public void testGetVersion() {
		version = "HTTP/1.1";
		HttpResponse res = new HttpResponse(version, status, phrase, header, file);
		assertEquals(version, res.getVersion());
	}
	
	@Test
	public void testGetStatus() {
		status = 404;
		HttpResponse res = new HttpResponse(version, status, phrase, header, file);
		assertEquals(status, res.getStatus());
	}
	
	@Test
	public void testGetPhrase() {
		phrase = "Bad Request";
		HttpResponse res = new HttpResponse(version, status, phrase, header, file);
		assertEquals(phrase, res.getPhrase());
	}
	
	@Test
	public void testGetFile() {
		HttpResponse res = new HttpResponse(version, status, phrase, header, file);
		assertEquals(file, res.getFile());
	}
}
