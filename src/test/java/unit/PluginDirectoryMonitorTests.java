package unit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import protocol.ProtocolConfiguration;
import server.Server;
import utils.PluginDirectoryMonitor;

public class PluginDirectoryMonitorTests {
	private PluginDirectoryMonitor monitor;
	private Server server;
	private String directory;
	
	@Before
	public void setUp() throws IOException {
		server = new Server(100, new ProtocolConfiguration());
		directory = "./web";
		monitor = new PluginDirectoryMonitor(Paths.get(directory), server);
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
	public void testDirectoryMonitorJarUpserted() throws Exception {
		Method handleJar = monitor.getClass().getDeclaredMethod("handleJarUpserted", String.class);
		handleJar.setAccessible(true);
		handleJar.invoke(monitor, "./web/YoloSwagDefddaultPlugin-1.0.1-SNAPSHOT.jar");
		
	}
}
