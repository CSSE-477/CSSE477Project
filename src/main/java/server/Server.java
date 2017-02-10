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

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.util.HashMap;

import handlers.ConnectionHandler;
import servlet.AServletManager;
import utils.SwsLogger;

import javax.net.ssl.*;


/**
 * This represents a welcoming server for the incoming
 * TCP request from a HTTP client such as a web browser. 
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Server implements Runnable, IDirectoryListener {

	private int port;
	private boolean stop;
	private SSLServerSocket welcomeSocket;
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
		try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("/home/csse/keystore.jks"),"password".toCharArray());

            // Create key manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "password".toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();

            // Create trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();

            // Initialize SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(km,  tm, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

            // Create server socket
            this.welcomeSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(this.port);

            // Now keep welcoming new connections until stop flag is set to true
			while(true) {
			    this.readyState = true;
				// Listen for incoming socket connection
				// This method block until somebody makes a request
                try{
                    SSLSocket sslSocket = (SSLSocket) this.welcomeSocket.accept();
                    sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
                    //TODO: Put this back in after testing - it was severly throwing off things
                    sslSocket.startHandshake();

                    // Come out of the loop if the stop flag is set
                    if(this.stop){
                        this.readyState = false;
                        break;
                    }

                    // Create a handler for this incoming connection and start the handler in a new thread
                    ConnectionHandler handler = new ConnectionHandler(sslSocket, this.pluginRootToServlet);
                    new Thread(handler).start();
                } catch (SSLException | SocketException e){
                    SwsLogger.errorLogger.error(e);
                }
			}
			this.welcomeSocket.close();
		}
		catch(Exception e) {
			SwsLogger.errorLogger.error(e);
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
