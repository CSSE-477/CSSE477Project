package handlers;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;
import protocol.ProtocolException;

/**
 * This class is responsible for handling a incoming request
 * by creating a {@link HttpRequest} object and sending the appropriate
 * response be creating a {@link HttpResponse} object. It implements
 * {@link Runnable} to be used in multi-threaded environment.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class ConnectionHandler implements Runnable {
	private Socket socket;
	private HashMap<String, IRequestHandlerFactory> requesthandlerFactoryMap;
	
	public ConnectionHandler(Socket socket, HashMap<String, IRequestHandlerFactory> requestHandlerFactoryMap) {
		this.socket = socket;
		this.requesthandlerFactoryMap = requestHandlerFactoryMap;
	}

	/**
	 * The entry point for connection handler. It first parses
	 * incoming request and creates a {@link HttpRequest} object,
	 * then it creates an appropriate {@link HttpResponse} object
	 * and sends the response back to the client (web browser).
	 */
	public void run() {
		// TODO: This class is a classic example of how not to code things. Refactor this code to make it
		// cohesive and extensible
		InputStream inStream = null;
		OutputStream outStream = null;
		
		try {
			inStream = this.socket.getInputStream();
			outStream = this.socket.getOutputStream();
		}
		catch(Exception e) {
			// Cannot do anything if we have exception reading input or output stream
			// May be have text to log this for further analysis?
			e.printStackTrace();
			return;
		}
		
		// At this point we have the input and output stream of the socket
		// Now lets create a HttpRequest object
		HttpRequest request = null;
		HttpResponse response = null;
		try {
			request = HttpRequest.read(inStream);
			System.out.println(request);
		}
		catch(ProtocolException pe) {
			// We have some sort of protocol exception. Get its status code and create response
			// We know only two kind of exception is possible inside fromInputStream
			// Protocol.BAD_REQUEST_CODE and Protocol.NOT_SUPPORTED_CODE
			int status = pe.getStatus();
			if(status == Protocol.BAD_REQUEST_CODE) {
				response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
			}
			if (status == Protocol.NOT_SUPPORTED_CODE) {
				response = HttpResponseFactory.create505NotSupported(Protocol.CLOSE);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			// For any other error, we will create bad request response as well
			response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
		
		if(response != null) {
			// Means there was an error, now write the response object to the socket
			try {
				response.write(outStream);
//				System.out.println(response);
			}
			catch(Exception e){
				// We will ignore this exception
				e.printStackTrace();
			}

			return;
		}
		
		// We reached here means no error so far, so lets process further
		try {
			// Fill in the code to create a response for version mismatch.
			// You may want to use constants such as Protocol.VERSION, Protocol.NOT_SUPPORTED_CODE, and more.
			// You can check if the version matches as follows
			if(!request.getVersion().equalsIgnoreCase(Protocol.VERSION)) {
				// Here you checked that the "Protocol.VERSION" string is not equal to the  
				// "request.version" string ignoring the case of the letters in both strings
				// TODO: Fill in the rest of the code here
			}
			IRequestHandlerFactory factory = this.requesthandlerFactoryMap.get(request.getMethod());
			if(factory == null){
                response = HttpResponseFactory.create505NotSupported(Protocol.CLOSE);
            }
            else{
                response = factory.getRequestHandler().handleRequest(request);
            }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		

		// TODO: So far response could be null for protocol version mismatch.
		// So this is a temporary patch for that problem and should be removed
		// after a response object is created for protocol version mismatch.
		if(response == null) {
			response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		}
		
		try{
			// Write response and we are all done so close the socket
			response.write(outStream);
//			System.out.println(response);
			socket.close();
		}
		catch(Exception e){
			// We will ignore this exception
			e.printStackTrace();
		} 
	}
}
