package unit;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import handlers.ConnectionHandler;
import protocol.HttpRequest;
import protocol.HttpResponseBuilder;
import servlet.AServletManager;

public class ConnectionHandlerTest {
	private ConnectionHandler ch;

	@Before
	public void setUp() {
		Socket s = new Socket();
		Map<String, AServletManager> contextRootToServlet = new HashMap<>();
		HttpRequest request = null;
		ch = new ConnectionHandler(s, request, contextRootToServlet);
	}
	
	@Test
	public void testGetContextRootFromUriSimple() throws Exception {
		Method m = ch.getClass().getDeclaredMethod("getContextRootFromUri", String.class);
		m.setAccessible(true);
		
		String uri = "/userapp/users/1";
		String expected = "userapp";
		String actual = (String) m.invoke(ch, uri);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetContextRootFromUriEdge() throws Exception {
		Method m = ch.getClass().getDeclaredMethod("getContextRootFromUri", String.class);
		m.setAccessible(true);
		
		String uri = "/";
		String expected = "";
		String actual = (String) m.invoke(ch, uri);
		assertEquals(expected, actual);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInterceptResponseForGzipNoGzip() throws Exception {
		Map<String, String> headers = new HashMap<>();
		HttpRequest request = new FakeHttpRequest(headers);
		Field chHttpRequest = ch.getClass().getDeclaredField("request");
		chHttpRequest.setAccessible(true);
		chHttpRequest.set(ch, request);
		
		// compare current headers to headers after interception
		HttpResponseBuilder responseBuilder = new HttpResponseBuilder();
		Field h = responseBuilder.getClass().getDeclaredField("header");
		h.setAccessible(true);
		Map<String, String> builderHeaders = (Map<String, String>) h.get(responseBuilder);
		int expectedLength = builderHeaders.size();

		Method m = ch.getClass().getDeclaredMethod("interceptResponseForGzip", HttpResponseBuilder.class);
		m.setAccessible(true);
		m.invoke(ch, responseBuilder);

		builderHeaders = (Map<String, String>) h.get(responseBuilder);
		int actualLength = builderHeaders.size();
		assertEquals(expectedLength, actualLength);
	}

	@Test
	public void testInterceptResponseForGzipWithGzip() throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept-Encoding", "gzip");
		HttpRequest request = new FakeHttpRequest(headers);
		Field chHttpRequest = ch.getClass().getDeclaredField("request");
		chHttpRequest.setAccessible(true);
		chHttpRequest.set(ch, request);
		
		// compare current headers to headers after interception
		HttpResponseBuilder responseBuilder = new HttpResponseBuilder();
		Field h = responseBuilder.getClass().getDeclaredField("header");
		h.setAccessible(true);
		Map<String, String> builderHeaders = (Map<String, String>) h.get(responseBuilder);
		int expectedLength = builderHeaders.size() + 1;
		
		Method m = ch.getClass().getDeclaredMethod("interceptResponseForGzip", HttpResponseBuilder.class);
		m.setAccessible(true);
		m.invoke(ch, responseBuilder);

		builderHeaders = (Map<String, String>) h.get(responseBuilder);
		int actualLength = builderHeaders.size();
		assertEquals(expectedLength, actualLength);
	}

	private class FakeHttpRequest extends HttpRequest {
		private Map<String, String> headers;

		public FakeHttpRequest(Map<String, String> headers) {
			this.headers = headers;
		}
		
		@Override
		public Map<String, String> getHeader() {
			return this.headers;
		}
	}
}
