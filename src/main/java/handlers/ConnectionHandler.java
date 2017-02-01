package handlers;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import protocol.*;
import utils.SwsLogger;

/**
 * This class is responsible for handling a incoming request by creating a
 * {@link HttpRequest} object and sending the appropriate response be creating a
 * {@link HttpResponse} object. It implements {@link Runnable} to be used in
 * multi-threaded environment.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class ConnectionHandler implements Runnable {
	private Socket socket;
	private HashMap<String, IRequestHandlerFactory> requestHandlerFactoryMap;
	private ProtocolConfiguration protocol;

	public ConnectionHandler(Socket socket, HashMap<String, IRequestHandlerFactory> requestHandlerFactoryMap,
							 ProtocolConfiguration protocol) {
		this.socket = socket;
		this.requestHandlerFactoryMap = requestHandlerFactoryMap;
		this.protocol = protocol;
	}

	/**
	 * The entry point for connection handler. It first parses incoming request
	 * and creates a {@link HttpRequest} object, then it creates an appropriate
	 * {@link HttpResponse} object and sends the response back to the client
	 * (web browser).
	 */
	public void run() {
		InputStream inStream = null;
		OutputStream outStream = null;

		try {
			inStream = this.socket.getInputStream();
			outStream = this.socket.getOutputStream();
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
			request = HttpRequest.read(inStream, this.protocol);
			SwsLogger.accessLogger.info("Recieved Request:\n" + request.toString());
		} catch (ProtocolException pe) {
			// We have some sort of protocol exception. Get its status code and
			// create response
			// We know only two kind of exception is possible inside
			// fromInputStream
			// Protocol.BAD_REQUEST_CODE and Protocol.NOT_SUPPORTED_CODE
			int status = pe.getStatus();
            response = (new HttpResponseBuilder(status, this.protocol)).generateResponse();
		} catch (Exception e) {
			// For any other error, we will create bad request response as well
            response = (new HttpResponseBuilder(400, this.protocol)).generateResponse();
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

			return;
		}

		// We reached here means no error so far, so lets process further

		// Fill in the code to create a response for version mismatch.
		// You may want to use constants such as Protocol.VERSION,
		// Protocol.NOT_SUPPORTED_CODE, and more.
		// You can check if the version matches as follows
		if (!request.getVersion().equalsIgnoreCase(this.protocol.getProtocolElement(ProtocolElements.VERSION))) {
			response = (new HttpResponseBuilder(400, this.protocol)).generateResponse();
		} else {
			IRequestHandlerFactory factory = this.requestHandlerFactoryMap.get(request.getMethod());
			if (factory == null) {
                response = (new HttpResponseBuilder(501, this.protocol)).generateResponse();
			} else {
				response = factory.getRequestHandler().handleRequest(request);
			}
		}

		// So this is a temporary patch for that problem and should be removed
		// after a response object is created for protocol version mismatch.
		if (response == null) {
            response = (new HttpResponseBuilder(400, this.protocol)).generateResponse();
		}

		try {
			// Write response and we are all done so close the socket
			response.write(outStream);
			// System.out.println(response);
			socket.close();
		} catch (Exception e) {
			// We will ignore this exception
			SwsLogger.errorLogger.error("Error while writing to socket! \n" + e.toString());
		}
	}
}
