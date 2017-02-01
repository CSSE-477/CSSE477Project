package unit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;

import app.SimpleWebServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import protocol.ProtocolConfiguration;
import server.Server;

public class ServerTest {

	private static ProtocolConfiguration protocol;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		protocol = new ProtocolConfiguration();
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
		Server server = new Server(0, protocol);
		assertEquals(true, server.isStoped());
	}

	@Test
	public void testIsStoppedNontrivial() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
		String fakeDir = "dir";
		Server server = new Server(0, protocol);
		Field welcomeSocket = server.getClass().getDeclaredField("welcomeSocket");
		welcomeSocket.setAccessible(true);
		ServerSocket socket = new ServerSocket();
		welcomeSocket.set(server, socket);
		assertEquals(false, server.isStoped());
		socket.close();
	}
}
