package servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
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

    protected HashMap<String, Method> invokationMap;
	protected HashMap<String, AHttpServlet> servletMap;
	protected String filePath;

	protected InputStream configStream;
	protected static final String CONFIG_DELIMETER =  ",";
	protected static final String URI_DELIMETER = "/";

	public AServletManager(String filePath, InputStream configStream) {
	    this.invokationMap = new HashMap<>();
        Method getMethod;
        Method putMethod;
        Method postMethod;
        Method deleteMethod;
        Method headMethod;
        try {
            getMethod = AHttpServlet.class.getDeclaredMethod("doGet", HttpRequest.class, HttpResponseBuilder.class);
            this.invokationMap.put(Protocol.getProtocol().getStringRep(Keywords.GET), getMethod);
            putMethod = AHttpServlet.class.getDeclaredMethod("doPut", HttpRequest.class, HttpResponseBuilder.class);
            this.invokationMap.put(Protocol.getProtocol().getStringRep(Keywords.PUT), putMethod);
            postMethod = AHttpServlet.class.getDeclaredMethod("doPost", HttpRequest.class, HttpResponseBuilder.class);
            this.invokationMap.put(Protocol.getProtocol().getStringRep(Keywords.POST), postMethod);
            deleteMethod = AHttpServlet.class.getDeclaredMethod("doDelete", HttpRequest.class, HttpResponseBuilder.class);
            this.invokationMap.put(Protocol.getProtocol().getStringRep(Keywords.DELETE), deleteMethod);
            headMethod = AHttpServlet.class.getDeclaredMethod("doHead", HttpRequest.class, HttpResponseBuilder.class);
            this.invokationMap.put(Protocol.getProtocol().getStringRep(Keywords.HEAD), headMethod);
        } catch (NoSuchMethodException e) {
            this.invokationMap.clear();
            e.printStackTrace();
        }
        this.servletMap = new HashMap<>();
		this.configStream = configStream;
		this.filePath = filePath;
		this.init();
		this.parseConfigFile();
	}

	public abstract void init();

	public abstract void destroy();

	public boolean parseConfigFile() {
		/*
		 * Config file:
		 * Request Type,Relative URI,Servlet Class
		 * HEAD,/users/{id}/edu.rosehulman.userapp.UserServlet
		 */
		if (this.configStream == null) {
			SwsLogger.accessLogger.info("Did not initialize configFile at ./config.csv");
			return false;
		}
		Scanner scanner = null;
		try {
			scanner = new Scanner(configStream);

			scanner.useDelimiter(CONFIG_DELIMETER);

			int delimited_values = 0;
			String relativeUri = null;
			String servletClassName = null;
			while(scanner.hasNext()) {
                    if(delimited_values == 0) {
                        relativeUri = scanner.next();
                        // extract "users" from "/users/{id}"
                        String[] relativeSplit = relativeUri.split(URI_DELIMETER);
                        if (relativeSplit.length < 1) {
                            return false;
                        }
                        relativeUri = relativeSplit[1];
                    }
                    else if(delimited_values == 1) {
                        servletClassName = scanner.next();
                        if (servletClassName == null || servletClassName.isEmpty()) {
                            return false;
                        }
                        Class<?> servletClass = Class.forName(servletClassName);
                        Constructor<?> servletConstructor = servletClass.getConstructor(String.class);
                        AHttpServlet servletInstance = (AHttpServlet) servletConstructor.newInstance(this.filePath);
                        if (relativeUri == null || relativeUri.isEmpty()) {
                            return false;
                        }
                        this.servletMap.put(relativeUri, servletInstance);
                        relativeUri = null;
                        servletClassName = null;
                        delimited_values = 0;
                    }
                    delimited_values++;
                }
                if(delimited_values != 0) {
                    SwsLogger.errorLogger.error("CSV file not properly formed");
                    return false;
                }

            }
        catch (Exception e) {
            SwsLogger.errorLogger.error("Exception while parsing config file.", e);
            return false;
        } finally {
        	if (scanner != null) {
        		scanner.close();
        	}
        }
		return true;
	}

	public HttpResponse handleRequest(HttpRequest request) {

        HttpResponseBuilder responseBuilder = new HttpResponseBuilder();

		String uri = request.getUri();
		// should look like "/userapp/users/{id}"
        String[] uriSplit = uri.split(URI_DELIMETER);
        if(uriSplit.length <= 1){
            return responseBuilder.generateResponse();
        }

		String servletKey = uriSplit[2];
		
		AHttpServlet servlet = this.servletMap.get(servletKey);

		/*
		Below is a definite possible source of error
		 */
        try {
            this.invokationMap.get(request.getMethod()).invoke(servlet, request, responseBuilder);
        } catch (IllegalAccessException | InvocationTargetException e) {
            SwsLogger.errorLogger.error("Invokation Failure.", e);
        }

        return responseBuilder.generateResponse();
	}
}
