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

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import handlers.ConnectionHandler;
import handlers.Counter;
import servlet.AServletManager;
import utils.SwsLogger;


/**
 * This represents a welcoming server for the incoming
 * TCP request from a HTTP client such as a web browser. 
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Server implements Runnable, IDirectoryListener {
	private int port;
	private boolean stop;
	private ServerSocket welcomeSocket;
	private boolean readyState;
	private HashMap<String, AServletManager> pluginRootToServlet;

	/**
	 * @param port
	 */
	public Server(int port) {
		this.port = port;
		this.stop = false;
		this.readyState = false;
		this.pluginRootToServlet = new HashMap<>();
	}
	
	/**
	 * The entry method for the main server thread that accepts incoming
	 * TCP connection request and creates a {@link ConnectionHandler} for
	 * the request.
	 */
	public void run() {
		Map<InetAddress, Counter> addressMap = new HashMap<InetAddress, Counter>();
		try {
			this.welcomeSocket = new ServerSocket(port);
			// Now keep welcoming new connections until stop flag is set to true
			while(true) {
			    this.readyState = true;
				// Listen for incoming socket connection
				// This method block until somebody makes a request
				Socket connectionSocket = this.welcomeSocket.accept();
				InetAddress address = connectionSocket.getInetAddress();
				System.err.println(address.toString());
				
				Counter counter = addressMap.get(address);
				boolean serviceRequest = false;
				if(counter == null) {
					System.err.println("new user");
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
				
				// Create a handler for this incoming connection and start the handler in a new thread
				ConnectionHandler handler = new ConnectionHandler(connectionSocket, this.pluginRootToServlet);
				new Thread(handler).start();
			}
			this.welcomeSocket.close();
		}
		catch(Exception e) {
			SwsLogger.errorLogger.error(e.getMessage());
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
		this.pluginRootToServlet.put(contextRoot, manager);
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
