package app;

import handlers.DeleteRequestHandlerFactory;
import handlers.GetRequestHandlerFactory;
import handlers.HeadRequestHandlerFactory;
import handlers.PostRequestHandlerFactory;
import handlers.PutRequestHandlerFactory;
import protocol.ProtocolConfiguration;
import protocol.ProtocolElements;
import server.Server;
import handlers.IRequestHandlerFactory;
import utils.PluginDirectoryMonitor;
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
		String pluginDirectory = properties.getProperty("pluginDirectory");
		int port = Integer.parseInt(properties.getProperty("port"));

		ProtocolConfiguration protocol = getProtocolConfiguration();

		// Create a run the server
		Server server = new Server(port, protocol);
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

	public static HashMap<String, IRequestHandlerFactory> getPopulatedFactoryHash(String rootDirectory, ProtocolConfiguration protocol){
		// Add factories to the map or create them in-line if that is preferable, then return below
		HashMap<String, IRequestHandlerFactory> factoryMap = new HashMap<>();
		factoryMap.put(protocol.getProtocolElement(ProtocolElements.GET),
				new GetRequestHandlerFactory(rootDirectory, protocol));
		factoryMap.put(protocol.getProtocolElement(ProtocolElements.HEAD),
				new HeadRequestHandlerFactory(rootDirectory, protocol));
		factoryMap.put(protocol.getProtocolElement(ProtocolElements.POST),
				new PostRequestHandlerFactory(rootDirectory, protocol));
		factoryMap.put(protocol.getProtocolElement(ProtocolElements.PUT),
				new PutRequestHandlerFactory(rootDirectory, protocol));
		factoryMap.put(protocol.getProtocolElement(ProtocolElements.DELETE),
				new DeleteRequestHandlerFactory(rootDirectory, protocol));
		return factoryMap;
	}

	public static ProtocolConfiguration getProtocolConfiguration(){
		return new ProtocolConfiguration();
	}
}
