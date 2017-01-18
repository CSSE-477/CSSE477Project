package server_tests;

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
	public void testGetServiceRateTrivial() {
		String fakeDir = "dir";
		Server server = new Server(fakeDir, 0, null);
		Long expected = Long.MIN_VALUE;
		float EPSILON = 0.001f;
		assertEquals(expected, server.getServiceRate(), EPSILON);
	}
	
	@Test
	public void testGetServiceRateNonTrivial() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String fakeDir = "dir";
		Server server = new Server(fakeDir, 0, null);
		double expected = 1000;
		float EPSILON = 0.0000001f;
		long serviceTimeVal = 5;
		long connectionsVal = 5;
		Field serviceTime = server.getClass().getDeclaredField("serviceTime");
		serviceTime.setAccessible(true);
		serviceTime.set(server, serviceTimeVal);
		Field connections = server.getClass().getDeclaredField("connections");
		connections.setAccessible(true);
		connections.set(server, connectionsVal);
		assertEquals(expected, server.getServiceRate(), EPSILON);
	}
	
	@Test
	public void testIsStoppedTrivial() {
		String fakeDir = "dir";
		Server server = new Server(fakeDir, 0, null);
		boolean expected = true;
		assertEquals(expected, server.isStoped());
	}

	@Test
	public void testIsStoppedNontrivial() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
		String fakeDir = "dir";
		Server server = new Server(fakeDir, 0, null);
		boolean expected = false;
		Field welcomeSocket = server.getClass().getDeclaredField("welcomeSocket");
		welcomeSocket.setAccessible(true);
		ServerSocket socket = new ServerSocket();
		welcomeSocket.set(server, socket);
		assertEquals(expected, server.isStoped());
		socket.close();
	}
}
