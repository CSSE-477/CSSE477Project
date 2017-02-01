package servlet;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Scanner;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.ProtocolConfiguration;
import protocol.ProtocolElements;

public abstract class AServletManager {

	protected HashMap<String, IHttpServlet> servletMap;
	protected String filePath;
	protected ProtocolConfiguration protocol;

	public AServletManager(String filePath, ProtocolConfiguration protocol) {
		this.servletMap = new HashMap<String, IHttpServlet>();
		this.filePath = filePath;
		this.protocol = protocol;
		this.init();
	}

	public abstract void init();

	public abstract void destroy();

	public boolean parseConfigFile() {
		/*
		 * Config file:
		 * Request Type,Relative URI,Servlet Class
		 * HEAD,/users/{id}/edu.rosehulman.userapp.UserServlet
		 */
		File config = new File("./config.txt");
		try {
			Scanner scanner = new Scanner(config);
			
			scanner.useDelimiter(",");
			while(scanner.hasNext()) {
				String requestType = scanner.next();
				
				String relativeUri = scanner.next();
				// extract "users" from "/users/{id}"
				relativeUri = relativeUri.split("/")[1];
								
				String servletClassName = scanner.next();
				
				Class<?> servletClass = Class.forName(servletClassName);
				Constructor<?> servletConstructor = servletClass.getConstructor(String.class, ProtocolConfiguration.class);
				IHttpServlet servletInstance = (IHttpServlet) servletConstructor.newInstance(this.filePath, this.protocol);
				this.servletMap.put(relativeUri, servletInstance);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public HttpResponse handleRequest(HttpRequest request) {
		String uri = request.getUri(); // should look like "/userapp/users/{id}"
		String servletKey = uri.split("/")[2];
		
		IHttpServlet servlet = this.servletMap.get(servletKey);
		HttpResponseBuilder rb = new HttpResponseBuilder(this.protocol);
		HttpResponse response = rb.generateResponse();

		// TODO: refactor pls
		if (request.getMethod() == this.protocol.getProtocolElement(ProtocolElements.GET)) {
			servlet.doGet(request, response);
		} else if (request.getMethod() == this.protocol.getProtocolElement(ProtocolElements.HEAD)) {
			servlet.doHead(request, response);
		} else if (request.getMethod() == this.protocol.getProtocolElement(ProtocolElements.POST)) {
			servlet.doPost(request, response);
		} else if (request.getMethod() == this.protocol.getProtocolElement(ProtocolElements.PUT)) {
			servlet.doPut(request, response);
		} else if (request.getMethod() == this.protocol.getProtocolElement(ProtocolElements.DELETE)) {
			servlet.doDelete(request, response);
		}
		
		// TODO: verify that response is okay?
		
		return response;
	}
}
