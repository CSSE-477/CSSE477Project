package app;

import server.Server;
import utils.SwsLogger;
import java.util.Properties;

import server.ServerProperties;

/**
 * The entry point of the Simple Web Server (SWS).
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class SimpleWebServer {
	
	
	public static void main(String[] args) throws Exception {
		ServerProperties config = new ServerProperties();
		Properties properties = config.getProperties("./resources/config.properties");

		String rootDirectory = properties.getProperty("rootDirectory");
		int port = Integer.parseInt(properties.getProperty("port"));

		// Create a run the server
		Server server = new Server(rootDirectory, port);
		Thread runner = new Thread(server);
		runner.start();

		// DONE: Instead of just printing to the console, use proper logging mechanism.
		// SL4J/Log4J are some popular logging framework
		SwsLogger.accessLogger.info(String.format("Simple Web Server started at port %d and serving the %s directory ...%n", port, rootDirectory));
		
		// Wait for the server thread to terminate
		runner.join();
	}
}
