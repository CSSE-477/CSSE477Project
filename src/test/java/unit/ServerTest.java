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
		ServerSocket socket = new ServerSocket();
		welcomeSocket.set(server, socket);
		assertEquals(false, server.isStopped());
		socket.close();
	}
}
