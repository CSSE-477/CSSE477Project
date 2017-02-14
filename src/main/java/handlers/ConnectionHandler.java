package handlers;

import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.Keywords;
import protocol.Protocol;
import servlet.AServletManager;
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
	private Map<String, AServletManager> contextRootToServlet;
	private HashMap<String, HttpResponse> cache;
	private HttpRequest request;

	private static final String DEFAULT_ROOT = "";

	public ConnectionHandler(Socket socket, HttpRequest httpRequest,
			Map<String, AServletManager> contextRootToServlet) {
		this.socket = socket;
		this.contextRootToServlet = contextRootToServlet;
		this.cache = new HashMap<String, HttpResponse>();
		this.request = httpRequest;
	}

	/**
	 * The entry point for connection handler. It first parses incoming request
	 * and creates a {@link HttpRequest} object, then it creates an appropriate
	 * {@link HttpResponse} object and sends the response back to the client
	 * (web browser).
	 */
	public void run() {
		OutputStream outStream = null;

		try {
			outStream = this.socket.getOutputStream();
		} catch (Exception e) {
			SwsLogger.errorLogger.error("Exception while creating socket connections!\n" + e.toString());
			return;
		}

		HttpResponseBuilder responseBuilder = new HttpResponseBuilder();
		interceptResponseForGzip(responseBuilder);
		HttpResponse response = null;

		if (!request.getVersion().equalsIgnoreCase(Protocol.getProtocol().getStringRep(Keywords.VERSION))) {
			response = responseBuilder.setStatus(400).generateResponse();
		} else {
			HttpResponse cachedResponse = null;

			// Check cache if it is a GET or HEAD request
			if (request.getMethod().equals(Protocol.getProtocol().getStringRep(Keywords.GET))
					|| request.getMethod().equals(Protocol.getProtocol().getStringRep(Keywords.HEAD))) {
				// Retrieve cached response if it is
				cachedResponse = this.cache.get(request.getUri());
			} else if (request.getMethod().equals(Protocol.getProtocol().getStringRep(Keywords.POST))
					|| request.getMethod().equals(Protocol.getProtocol().getStringRep(Keywords.PUT))
					|| request.getMethod().equals(Protocol.getProtocol().getStringRep(Keywords.DELETE))) {
				// Invalidate cache if it is a write operation
				this.cache.remove(request.getUri());
			}

			// Return cached response if it exists
			if (cachedResponse != null) {
				response = cachedResponse;
			} else {
				// response not found in cache, do regular plugin lookup
				String contextRoot = getContextRootFromUri(request.getUri());
				AServletManager manager = this.contextRootToServlet.get(contextRoot);

				// fall back to the default manager if contextRoot doesn't match
				if (manager == null) {
					contextRoot = DEFAULT_ROOT;
					manager = this.contextRootToServlet.get(contextRoot);
				}
				if (manager == null) {
					response = responseBuilder.setStatus(501).generateResponse();
				} else {
					// Check manager heartbeat
					if (!manager.getHeartbeat()) {
						// plugin has entered BORK MODE, return 501
						response = responseBuilder.setStatus(501).generateResponse();
					} else {
						// plugin is alive and well, send it the request
						response = manager.handleRequest(request, responseBuilder);
					}
				}
			}
		}

		// response is valid, write it to cache if it is a GET or HEAD
		if (request.getMethod().equals(Protocol.getProtocol().getStringRep(Keywords.GET))
				|| request.getMethod().equals(Protocol.getProtocol().getStringRep(Keywords.HEAD))) {
			this.cache.put(request.getUri(), response);
		}

		try {
			// Write response and we are all done so close the socket
			response.write(outStream);
			socket.close();
		} catch (Exception e) {
			// We will ignore this exception
			SwsLogger.errorLogger.error("Error while writing to socket! \n" + e.toString());
		}
	}

	/**
	 * Strip out /userapp/users/1 => "userapp"
	 * @param uri of the request
	 * @return contextRoot of the request
	 */
	private String getContextRootFromUri(String uri) {
		String contextRoot = DEFAULT_ROOT;
		int firstSlashIndex = uri.indexOf('/') + 1;
		int secondSlashIndex = uri.indexOf('/', firstSlashIndex);
		if (secondSlashIndex != -1) {
			contextRoot = uri.substring(firstSlashIndex, secondSlashIndex);
		}
		return contextRoot;
	}

	/**
	 * Use the HttpRequest to see if Accept-Encoding gzip is included
	 * If so, attach the Content-Encoding header to the HttpResponse
	 */
	private void interceptResponseForGzip(HttpResponseBuilder responseBuilder) {
		Map<String, String> requestHeaders = this.request.getHeader();
		String acceptEncoding = requestHeaders.get(Protocol.getProtocol().getStringRep(Keywords.ACCEPT_ENCODING));

		if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
			responseBuilder.putHeader(Protocol.getProtocol().getStringRep(Keywords.CONTENT_ENCODING), "gzip");
		}
	}
}
