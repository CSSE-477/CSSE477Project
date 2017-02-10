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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import handlers.ConnectionHandler;
import protocol.*;
import servlet.AServletManager;
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
	private ServerSocket welcomeSocket;
	private boolean readyState;
	private HashMap<String, AServletManager> pluginRootToServlet;
    private PriorityQueue<HttpPriorityElement> requestQueue;
    private HashMap<String, Integer> valueMap;
	private ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

	/**
	 * @param port
	 */
	public Server(int port) {
		this.port = port;
		this.stop = false;
		this.readyState = false;
		this.pluginRootToServlet = new HashMap<>();
        this.valueMap = new HashMap<>();
        this.valueMap.put(Protocol.getProtocol().getStringRep(Keywords.GET), 2);
        this.valueMap.put(Protocol.getProtocol().getStringRep(Keywords.DELETE), 3);
        this.valueMap.put(Protocol.getProtocol().getStringRep(Keywords.HEAD), 1);
        this.valueMap.put(Protocol.getProtocol().getStringRep(Keywords.POST), 4);
        this.valueMap.put(Protocol.getProtocol().getStringRep(Keywords.PUT), 5);
        this.requestQueue = new PriorityQueue<>(10, new Comparator<HttpPriorityElement>() {
            @Override
            public int compare(HttpPriorityElement o1, HttpPriorityElement o2) {
                HttpRequest o1Req = o1.getRequest();
                HttpRequest o2Req = o2.getRequest();
                LocalDateTime now = LocalDateTime.now();
                if(o1.getTime().minusSeconds(1).isAfter(now) && !o2.getTime().minusSeconds(1).isAfter(now)) {
                    return -1;
                }
                int o1Total = getMethodVal(o1Req.getMethod());
                int o2Total = getMethodVal(o2Req.getMethod());
                int o1Length;
                try{
                    o1Length = Integer.parseInt(o1Req.getHeader()
                            .get(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH)));
                } catch (Exception e){
                    o1Length = 0;
                }
                int o2Length;
                try{
                    o2Length = Integer.parseInt(o2Req.getHeader()
                            .get(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH)));
                } catch (Exception e){
                    o2Length = 0;
                }
                o2Length += 1;
                o1Length += 1;
                o1Total = o1Total * getPayloadSizeFactor(o1Req.getMethod(), o1Length);
                o2Total = o2Total * getPayloadSizeFactor(o2Req.getMethod(), o2Length);
                if(o1Total < o2Total) {
                    return -1;
                }
                if(o1Total > o2Total) {
                    return 1;
                }
                return 0;
            }

            int getMethodVal(String method){
                if(method == null){
                    return 1;
                }
                Integer value = valueMap.get(method);
                if(value == null){
                    return 1;
                }
                return value;
            }

            int getPayloadSizeFactor(String method, int payloadSize){
                if(method == null) {
                    return 1;
                }
                if(method.equals(Protocol.getProtocol().getStringRep(Keywords.POST)) || method.equals(Protocol.getProtocol().getStringRep(Keywords.PUT))){
                    return payloadSize;
                }
                return 1;
            }
        });
	}
	
	/**
	 * The entry method for the main server thread that accepts incoming
	 * TCP connection request and creates a {@link ConnectionHandler} for
	 * the request.
	 */
	public void run() {
		try {
			this.welcomeSocket = new ServerSocket(port);
			// Now keep welcoming new connections until stop flag is set to true
			while(true) {
			    this.readyState = true;
				// Listen for incoming socket connection
				// This method block until somebody makes a request
				Socket connectionSocket = this.welcomeSocket.accept();
				// Come out of the loop if the stop flag is set

				if(this.stop){
				    this.readyState = false;
				    break;
                }

                InputStream inStream = null;
                OutputStream outStream = null;

                try {
                    inStream = connectionSocket.getInputStream();
                    outStream = connectionSocket.getOutputStream();
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
                HttpResponse response = null;
                try {
                    request = HttpRequest.read(inStream);
                    SwsLogger.accessLogger.info("Recieved Request: " + request.toString());
                } catch (ProtocolException pe) {
                    // We have some sort of protocol exception. Get its status code and
                    // create response
                    // We know only two kind of exception is possible inside
                    // fromInputStream
                    // Protocol.BAD_REQUEST_CODE and Protocol.NOT_SUPPORTED_CODE
                    int status = pe.getStatus();
                    response = (new HttpResponseBuilder(status)).generateResponse();
                } catch (Exception e) {
                    // For any other error, we will create bad request response as well
                    response = (new HttpResponseBuilder(400)).generateResponse();
                }

                if (response != null) {
                    // Means there was an error, now write the response object to the
                    // socket
                    try {
                        response.write(outStream);
                        // System.out.println(response);
                    } catch (Exception e) {
                        // We will ignore this exception
                        SwsLogger.errorLogger.error("Exception occured while sending HTTP resonponse!\n" + e.toString());
                    }
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
			SwsLogger.errorLogger.error(e.getMessage());
			pool.shutdownNow();
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
