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

	private HashMap<String, AHttpServlet> servletMap;
	private HashMap<String, String> typeMap;
	private String filePath;

	private static final String CONFIG_RELATIVE_PATH = "./config.csv";
	private static final String CONFIG_DELIMETER =  ",";
	private static final String URI_DELIMETER = "/";

	public AServletManager(String filePath) {
		this.servletMap = new HashMap<>();
		this.typeMap = new HashMap<>();
		this.filePath = filePath;
		this.init();
	}

	abstract void init();

	public abstract void destroy();

	public boolean parseConfigFile() {
		/*
		 * Config file:
		 * Request Type,Relative URI,Servlet Class
		 * HEAD,/users/{id}/edu.rosehulman.userapp.UserServlet
		 */
		File config = new File(CONFIG_RELATIVE_PATH);
		try {
			Scanner scanner = new Scanner(config);

			scanner.useDelimiter(CONFIG_DELIMETER);

			int delimited_values = 0;
			String requestType = null;
			String relativeUri = null;
			String servletClassName = null;
			while(scanner.hasNext()) {
			    switch(delimited_values){
                    case(0):
                        requestType = scanner.next();
                        break;
                    case(1):
                        relativeUri = scanner.next();
                        // extract "users" from "/users/{id}"
                        String[] relativeSplit = relativeUri.split(URI_DELIMETER);
                        if(relativeSplit.length <= 1){
                            return false;
                        }
                        relativeUri = relativeUri.split(URI_DELIMETER)[1];
                        break;
                    case(2):
                        servletClassName = scanner.next();
                        if(servletClassName == null || servletClassName.isEmpty()){
                            return false;
                        }
                        Class<?> servletClass = Class.forName(servletClassName);
                        Constructor<?> servletConstructor = servletClass.getConstructor(String.class);
                        AHttpServlet servletInstance = (AHttpServlet) servletConstructor.newInstance(this.filePath);
                        if(relativeUri == null || relativeUri.isEmpty() || requestType == null || requestType.isEmpty()){
                            return false;
                        }
                        this.typeMap.put(relativeUri, requestType);
                        this.servletMap.put(relativeUri, servletInstance);
                        requestType = null;
                        relativeUri = null;
                        servletClassName = null;
                        delimited_values = 0;
                        break;
                    default:
                        return false;
                }
                delimited_values++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	// With the changes below it will be 1.0.4 - more changes to be made
	public HttpResponse handleRequest(HttpRequest request) {
		String uri = request.getUri();
		// should look like "/userapp/users/{id}"
		String servletKey = uri.split(URI_DELIMETER)[2];
		
		AHttpServlet servlet = this.servletMap.get(servletKey);
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
