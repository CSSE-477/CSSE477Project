package app;

import server.Server;
import server.handlers.IRequestHandlerFactory;
import utils.ServerProperties;
import utils.SwsLogger;

import java.util.HashMap;
import java.util.Properties;

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
		int port = Integer.parseInt(properties.getProperty("port"));

		// Create a run the server
		Server server = new Server(rootDirectory, port, getPopulatedFactoryHash());
		Thread runner = new Thread(server);
		runner.start();

		// DONE: Instead of just printing to the console, use proper logging mechanism.
		// SL4J/Log4J are some popular logging framework
		SwsLogger.accessLogger.info(String.format("Simple Web Server started at port %d and serving the %s directory.", port, rootDirectory));
		
		// Wait for the server thread to terminate
		runner.join();
	}

	public static HashMap<String, IRequestHandlerFactory> getPopulatedFactoryHash(){
		HashMap<String, IRequestHandlerFactory> requestHandlerFactoryMap = new HashMap();
		// Add factories to the map or create them in-line if that is preferable, then return below
		return requestHandlerFactoryMap;
	}
}
