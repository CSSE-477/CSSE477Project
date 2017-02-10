package app;

import java.util.Properties;

import server.Server;
import utils.PluginDirectoryMonitor;
import utils.ServerProperties;
import utils.SwsLogger;

/**
 * The entry point of the Simple Web Server (SWS).
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class SimpleWebServer {

	public static void main(String[] args) throws Exception {
		ServerProperties config = new ServerProperties();
		Properties properties = config.getProperties("config.properties");

		String rootDirectory = properties.getProperty("rootDirectory");
		String pluginDirectory = properties.getProperty("pluginDirectory");
		int port = Integer.parseInt(properties.getProperty("port"));


		// Create a run the server
		Server server = new Server(port);

		Thread runner = new Thread(server);
		runner.start();

		// Create and run the directory monitor
		PluginDirectoryMonitor monitor = new PluginDirectoryMonitor(pluginDirectory, server);
		Thread monitorRunner = new Thread(monitor);
		monitorRunner.start();

		// DONE: Instead of just printing to the console, use proper logging mechanism.
		// SL4J/Log4J are some popular logging framework
		SwsLogger.accessLogger.info(String.format("Simple Web Server started at port %d and serving the %s directory.",
				port, rootDirectory));
		
		// Wait for the unit.server thread to terminate
		runner.join();
	}
}
