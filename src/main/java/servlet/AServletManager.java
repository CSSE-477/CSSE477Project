package servlet;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Scanner;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.Keywords;
import protocol.Protocol;

public abstract class AServletManager {

	protected HashMap<String, IHttpServlet> servletMap;
	protected String filePath;

	public AServletManager(String filePath) {
		this.servletMap = new HashMap<String, IHttpServlet>();
		this.filePath = filePath;
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
				Constructor<?> servletConstructor = servletClass.getConstructor(String.class);
				IHttpServlet servletInstance = (IHttpServlet) servletConstructor.newInstance(this.filePath);
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
		HttpResponseBuilder responseBuilder = new HttpResponseBuilder();

		// TODO: refactor pls
		if (request.getMethod() == Protocol.getProtocol().getStringRep(Keywords.GET)) {
			servlet.doGet(request, responseBuilder);
		} else if (request.getMethod() == Protocol.getProtocol().getStringRep(Keywords.HEAD)) {
			servlet.doHead(request, responseBuilder);
		} else if (request.getMethod() == Protocol.getProtocol().getStringRep(Keywords.POST)) {
			servlet.doPost(request, responseBuilder);
		} else if (request.getMethod() == Protocol.getProtocol().getStringRep(Keywords.PUT)) {
			servlet.doPut(request, responseBuilder);
		} else if (request.getMethod() == Protocol.getProtocol().getStringRep(Keywords.DELETE)) {
			servlet.doDelete(request, responseBuilder);
		}
		
		// TODO: verify that response is okay?
		
		return responseBuilder.generateResponse();
	}
}
