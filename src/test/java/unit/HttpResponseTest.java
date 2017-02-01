package unit;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import protocol.HttpResponse;
import protocol.Keywords;
import protocol.Protocol;

public class HttpResponseTest {
	private String version;
	private int status;
	private String phrase;
	private Map<String, String> header;
	private File file;

	@Before
	public void setUp() {
		version = Protocol.getProtocol().getStringRep(Keywords.VERSION);
		status = 200;
		phrase = Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(this.status));
		header = new HashMap<>();
		file = null;
	}

	@Test
	public void testGetVersion() {
		version = "someweirdversion";
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
	
	@Test
	public void testGetHeader() {
		String value = "testValue";
		String key = "helloWorld".toLowerCase();
		header.put(key, value);

		HttpResponse res = new HttpResponse(version, status, phrase, header, file);
		assertEquals(value, res.getHeader().get(key));
	}
	
	@Test
	public void testWriteNoFile() throws Exception {
		version = "HTTP/1.1";
		phrase = "OK";
		String value = "testValue";
		String key = "helloWorld".toLowerCase();
		header.put(key, value);
		HttpResponse res = new HttpResponse(version, status, phrase, header, file);

		ByteArrayOutputStream out = new ByteArrayOutputStream();		
		String expected = version + " " + status + " " + phrase + "\r\n" +
					key + ": " + value + "\r\n\r\n";

		res.write(out);
		assertEquals(expected, out.toString());
	}
}
