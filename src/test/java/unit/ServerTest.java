package unit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import server.Server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

public class ServerTest {


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testIsStoppedTrivial() {
		String fakeDir = "dir";
		Server server = new Server(0);
		assertEquals(true, server.isStopped());
	}

	@Test
	public void testIsStoppedNontrivial() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
		String fakeDir = "dir";
		Server server = new Server(0);
		Field welcomeSocket = server.getClass().getDeclaredField("welcomeSocket");
		welcomeSocket.setAccessible(true);
		SSLServerSocket socket = new SSLServerSocket() {
			@Override
			public String[] getEnabledCipherSuites() {
				return new String[0];
			}

			@Override
			public void setEnabledCipherSuites(String[] strings) {

			}

			@Override
			public String[] getSupportedCipherSuites() {
				return new String[0];
			}

			@Override
			public String[] getSupportedProtocols() {
				return new String[0];
			}

			@Override
			public String[] getEnabledProtocols() {
				return new String[0];
			}

			@Override
			public void setEnabledProtocols(String[] strings) {

			}

			@Override
			public void setNeedClientAuth(boolean b) {

			}

			@Override
			public boolean getNeedClientAuth() {
				return false;
			}

			@Override
			public void setWantClientAuth(boolean b) {

			}

			@Override
			public boolean getWantClientAuth() {
				return false;
			}

			@Override
			public void setUseClientMode(boolean b) {

			}

			@Override
			public boolean getUseClientMode() {
				return false;
			}

			@Override
			public void setEnableSessionCreation(boolean b) {

			}

			@Override
			public boolean getEnableSessionCreation() {
				return false;
			}
		};
		welcomeSocket.set(server, socket);
		assertEquals(false, server.isStopped());
		socket.close();
	}
}
