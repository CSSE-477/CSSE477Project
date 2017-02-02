package servlet;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Scanner;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.Keywords;
import protocol.Protocol;
import utils.SwsLogger;

public abstract class AServletManager {
    
    private HashMap<String, Method> invokationMap;
	protected HashMap<String, AHttpServlet> servletMap;
	protected String filePath;

	protected static final String CONFIG_RELATIVE_PATH = "./config.csv";
	protected static final String CONFIG_DELIMETER =  ",";
	protected static final String URI_DELIMETER = "/";

	public AServletManager(String filePath) {
	    this.invokationMap = new HashMap<>();
        Method getMethod;
        Method putMethod;
        Method postMethod;
        Method deleteMethod;
        Method headMethod;
        try {
            getMethod = AHttpServlet.class.getDeclaredMethod("doGet");
            this.invokationMap.put(Protocol.getProtocol().getStringRep(Keywords.GET), getMethod);
            putMethod = AHttpServlet.class.getDeclaredMethod("doPut");
            this.invokationMap.put(Protocol.getProtocol().getStringRep(Keywords.PUT), putMethod);
            postMethod = AHttpServlet.class.getDeclaredMethod("doPost");
            this.invokationMap.put(Protocol.getProtocol().getStringRep(Keywords.POST), postMethod);
            deleteMethod = AHttpServlet.class.getDeclaredMethod("doDelete");
            this.invokationMap.put(Protocol.getProtocol().getStringRep(Keywords.DELETE), deleteMethod);
            headMethod = AHttpServlet.class.getDeclaredMethod("doHead");
            this.invokationMap.put(Protocol.getProtocol().getStringRep(Keywords.HEAD), headMethod);
        } catch (NoSuchMethodException e) {
            this.invokationMap.clear();
            e.printStackTrace();
        }
        this.servletMap = new HashMap<>();
		this.filePath = filePath;
		this.parseConfigFile();
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
			String relativeUri = null;
			String servletClassName = null;
			while(scanner.hasNext()) {
			    switch(delimited_values){
                    case(0):
                        relativeUri = scanner.next();
                        // extract "users" from "/users/{id}"
                        String[] relativeSplit = relativeUri.split(URI_DELIMETER);
                        if(relativeSplit.length < 1){
                            return false;
                        }
                        relativeUri = relativeSplit[1];
                        break;
                    case(1):
                        servletClassName = scanner.next();
                        if(servletClassName == null || servletClassName.isEmpty()){
                            return false;
                        }
                        Class<?> servletClass = Class.forName(servletClassName);
                        Constructor<?> servletConstructor = servletClass.getConstructor(String.class);
                        AHttpServlet servletInstance = (AHttpServlet) servletConstructor.newInstance(this.filePath);
                        if(relativeUri == null || relativeUri.isEmpty()){
                            return false;
                        }
                        this.servletMap.put(relativeUri, servletInstance);
                        relativeUri = null;
                        servletClassName = null;
                        delimited_values = 0;
                        break;
                    default:
                        return false;
                }
                delimited_values++;
			}
			if(delimited_values != 0) {
			    return false;
            }
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	// With the changes below it will be 1.0.4 - more changes to be made
	public HttpResponse handleRequest(HttpRequest request) {

        HttpResponseBuilder responseBuilder = new HttpResponseBuilder();

		String uri = request.getUri();
		// should look like "/userapp/users/{id}"
        String[] uriSplit = uri.split(URI_DELIMETER);
        if(uriSplit.length <= 1){
            return responseBuilder.generateResponse();
        }

		String servletKey = uriSplit[1];
		
		AHttpServlet servlet = this.servletMap.get(servletKey);

        try {
            this.invokationMap.get(request.getMethod()).invoke(servlet, request, responseBuilder);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            SwsLogger.errorLogger.error("Invokation Failure.");
        }

        return responseBuilder.generateResponse();
	}
}
