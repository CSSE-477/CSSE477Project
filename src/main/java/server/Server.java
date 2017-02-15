/*
 * Server.java
 * Oct 7, 2012
 *
 * Simple Web Server (SWS) for CSSE 477
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 */
 
package server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import handlers.ConnectionHandler;
import handlers.Counter;
import protocol.*;
import servlet.AServletManager;
import utils.ServerProperties;
import utils.SwsLogger;

/**
 * This represents a welcoming server for the incoming
 * TCP request from a HTTP client such as a web browser. 
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Server implements Runnable, IDirectoryListener {
	private static final int POOL_SIZE = 20;
	private int port;
	private boolean stop;
	private SSLServerSocket welcomeSocket;
	private boolean readyState;
	private HashMap<String, AServletManager> pluginRootToServlet;
    private PriorityQueue<HttpPriorityElement> requestQueue;
	private ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

	/**
	 * @param port
	 */
	public Server(int port) {
		this.port = port;
		this.stop = false;
		this.readyState = false;
		this.pluginRootToServlet = new HashMap<>();
        this.requestQueue = new PriorityQueue<>(10, new HttpPriorityElementComparator());
	}

	/**
	 * Initialize an SSLContext for the server to open a socket against
	 * @return SSLContext
	 * @throws Exception
	 */
	private SSLContext getSSLContext() throws Exception {
        SSLContext sslContext = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
	        keyStore.load(new FileInputStream("/home/csse/keystore.jks"), "password".toCharArray());
	
	        // Create key manager
	        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
	        keyManagerFactory.init(keyStore, "password".toCharArray());
	        KeyManager[] km = keyManagerFactory.getKeyManagers();

	        // Create trust manager
	        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
	        trustManagerFactory.init(keyStore);
	        TrustManager[] tm = trustManagerFactory.getTrustManagers();

	        // Initialize SSLContext
	        sslContext = SSLContext.getInstance("TLSv1");
	        sslContext.init(km,  tm, null);
        } catch (Exception e) {
        	SwsLogger.errorLogger.error("Error in getting ssl context: ", e);
        }
        return sslContext;
	}

	/**
	 * The entry method for the main server thread that accepts incoming
	 * TCP connection request and creates a {@link ConnectionHandler} for
	 * the request.
	 */
	@SuppressWarnings("null")
	public void run() {
		Map<InetAddress, Counter> addressMap = new HashMap<InetAddress, Counter>();
		try {
			SSLContext sslContext = getSSLContext();
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

            // Create server socket
            this.welcomeSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(this.port);

            // Now keep welcoming new connections until stop flag is set to true
			while(true) {
			    this.readyState = true;
				// Listen for incoming socket connection
				// This method block until somebody makes a request
			    SSLSocket connectionSocket = null;
                try{
                    connectionSocket = (SSLSocket) this.welcomeSocket.accept();
                    connectionSocket.setEnabledCipherSuites(connectionSocket.getSupportedCipherSuites());
                    connectionSocket.startHandshake();
                } catch (Exception e){
                    SwsLogger.errorLogger.error(e);
                    continue;
                }
				InetAddress address = connectionSocket.getInetAddress();
				
				Counter counter = addressMap.get(address);
				boolean serviceRequest = false;
				if(counter == null) {
					addressMap.put(address, new Counter());
					serviceRequest = true;
				} else {
					serviceRequest = counter.increment();
				}
				if (!serviceRequest) {
					SwsLogger.accessLogger.info(address.toString() + " has sent too many requests too quickly. Denying access.");
					connectionSocket.close();
					continue;
				}
				
				// Come out of the loop if the stop flag is set
				if(this.stop){
				    this.readyState = false;
				    break;
                }

                InputStream inStream = null;

                try {
                    inStream = connectionSocket.getInputStream();
                } catch (Exception e) {
                    // Cannot do anything if we have exception reading input or output
                    // stream
                    // May be have text to log this for further analysis?
                    SwsLogger.errorLogger.error("Exception while creating socket connections!\n" + e.toString());
                    return;
                }

                // At this point we have the input and output stream of the socket
                // Now lets create a HttpRequest object
                HttpRequest request = null;
                try {
                    request = HttpRequest.read(inStream);
                    SwsLogger.accessLogger.info("Recieved Request: " + request.toString());
                } catch (Exception e) {
                    SwsLogger.errorLogger.error("Bad Request", e);
                    continue;
                }

    			// FIXME: after benchmarking, fix this garbage code - collin
    			if (request.getUri().equals("/serverexplosion.bat")) {
    				String collin = null;
    				collin.indexOf("explosion");
    			}
				HttpPriorityElement newElement = new HttpPriorityElement(request,
                        new ConnectionHandler(connectionSocket, request, this.pluginRootToServlet));

				this.requestQueue.add(newElement);

				ConnectionHandler handler = this.requestQueue.poll().getHandler();

				if(handler != null) {
                    pool.execute(handler);
                }
			}
			this.welcomeSocket.close();
		}
		catch(Exception e) {
			SwsLogger.errorLogger.error("Server exception accepting connection...", e);
			pool.shutdownNow();
			System.exit(-1);
		}
	}

	/**
	 * Stops the server from listening further.
	 */
	public synchronized void stop() {
		if(this.stop)
			return;
		
		// Set the stop flag to be true
		this.stop = true;
		try {
			// This will force welcomeSocket to come out of the blocked accept() method 
			// in the main loop of the start() method
			Socket socket = new Socket(InetAddress.getLocalHost(), port);
			
			// We do not have any other job for this socket so just close it
			socket.close();
		}
		catch(Exception e){SwsLogger.errorLogger.error(e.getMessage());}
	}

	public synchronized boolean isReady() {
	    return this.readyState;
    }

	/**
	 * Checks if the server is stopeed or not.
	 * @return
	 */
	public boolean isStopped() {
		return this.welcomeSocket == null || this.welcomeSocket.isClosed();
	}

	@Override
	public void addPlugin(String contextRoot, AServletManager manager) {
		// Add plugin route to local plugin map
		this.pluginRootToServlet.put(contextRoot, manager);

		// use HTTP post to register microservice at gateway
		try {
			ServerProperties config = new ServerProperties();
			Properties properties = config.getProperties("config.properties");

			String gatewayHost = properties.getProperty("gatewayHost");
			int gatewayPort = Integer.parseInt(properties.getProperty("gatewayPort"));

			SSLContext sslContext = getSSLContext();
			SSLSocketFactory socketFactory = sslContext.getSocketFactory();
			Socket socket = socketFactory.createSocket(gatewayHost, gatewayPort);

			String method = Protocol.getProtocol().getStringRep(Keywords.POST);
			String uri = "/microserviceregistration";
			String version = "HTTP/1.1";
			Map<String, String> header = new HashMap<>();
			char[] body = contextRoot.toCharArray();
			header.put("Host", InetAddress.getLocalHost().toString().trim());
			header.put("Content-Length", String.valueOf(body.length));
			header.put("User-Agent", "#YOLOSWAG *dabs* microservice");

			HttpRequest request = new HttpRequest(method, uri, version, header, body);

			//TODO: create HttpRequest write() function to write to outputstream

		} catch (Exception e) {
			SwsLogger.errorLogger.error("Microservice registration failed!", e);
		}
	}

	@Override
	public void removePlugin(String contextRoot) {
		AServletManager manager = this.pluginRootToServlet.get(contextRoot);
		if (manager != null) {
			manager.destroy();
		}
		this.pluginRootToServlet.put(contextRoot, null);
	}
}
