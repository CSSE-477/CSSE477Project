package unit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.jar.Manifest;

import org.junit.Before;
import org.junit.Test;

import server.Server;
import utils.FakeManifest;
import utils.PluginDirectoryMonitor;

public class PluginDirectoryMonitorTests {
	private PluginDirectoryMonitor monitor;
	private Server server;
	private String directory;
	
	@Before
	public void setUp() throws IOException {
		server = new Server(100);
		directory = "./web";
		monitor = new PluginDirectoryMonitor(directory, server);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testDirectoryMonitorRegister() throws Exception {
		Field keys = monitor.getClass().getDeclaredField("keys");
		keys.setAccessible(true);
		Map<String, String> map = (Map<String, String>)keys.get(monitor);
		
		int expected = 1;
		int actual = map.keySet().size();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDirectoryMonitorReadManifestNoContextRoot() throws Exception {
		Method readManifest = monitor.getClass().getDeclaredMethod("initializeManifestValues", Manifest.class, String.class);
		readManifest.setAccessible(true);

		FakeManifest fm = new FakeManifest("client.Person", "");
		String expected = "";
		String entryPoint = (String) readManifest.invoke(monitor, fm, "path/to/jar");
		assertEquals(expected, entryPoint);
	}
	
	@Test
	public void testDirectoryMonitorReadManifestNoEntryPoint() throws Exception {
		Method readManifest = monitor.getClass().getDeclaredMethod("initializeManifestValues", Manifest.class, String.class);
		readManifest.setAccessible(true);

		FakeManifest fm = new FakeManifest("", "userapp");
		String expected = "";
		String entryPoint = (String) readManifest.invoke(monitor, fm, "path/to/jar");
		assertEquals(expected, entryPoint);
	}

	@Test
	public void testDirectoryMonitorReadManifestSuccess() throws Exception {
		Method readManifest = monitor.getClass().getDeclaredMethod("initializeManifestValues", Manifest.class, String.class);
		readManifest.setAccessible(true);

		String expected = "client.Person";
		FakeManifest fm = new FakeManifest(expected, "userapp");
		String entryPoint = (String) readManifest.invoke(monitor, fm, "path/to/jar");
		assertEquals(expected, entryPoint);
	}

//	@Test
//	public void testDirectoryMonitorJarUpserted() throws Exception {
//		Method handleJar = monitor.getClass().getDeclaredMethod("handleJarUpserted", String.class);
//		handleJar.setAccessible(true);
//		handleJar.invoke(monitor, "./web/YoloSwagDefaultPlugin-1.0.1-SNAPSHOT.jar");
//		
//	}
}
