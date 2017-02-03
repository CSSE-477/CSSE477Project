package servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Scanner;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.Keywords;
import protocol.Protocol;
import utils.SwsLogger;

public abstract class AServletManager {

    protected HashMap<String, Method> invocationMap;
	protected HashMap<String, AHttpServlet> servletMap;
	protected String filePath;

	private boolean validStatus;

	protected static final String CONFIG_DELIMETER =  ",";
	protected static final String URI_DELIMETER = "/";
	protected static final String PATH_REPLACEMENT_DELIMETER = ".";
    protected URLClassLoader classLoader;

	public AServletManager(String filePath, URLClassLoader classLoader) {
	    this.invocationMap = new HashMap<>();

        Method getMethod;
        Method putMethod;
        Method postMethod;
        Method deleteMethod;
        Method headMethod;
        try {
            getMethod = AHttpServlet.class.getDeclaredMethod("doGet", HttpRequest.class, HttpResponseBuilder.class);
            this.invocationMap.put(Protocol.getProtocol().getStringRep(Keywords.GET), getMethod);
            putMethod = AHttpServlet.class.getDeclaredMethod("doPut", HttpRequest.class, HttpResponseBuilder.class);
            this.invocationMap.put(Protocol.getProtocol().getStringRep(Keywords.PUT), putMethod);
            postMethod = AHttpServlet.class.getDeclaredMethod("doPost", HttpRequest.class, HttpResponseBuilder.class);
            this.invocationMap.put(Protocol.getProtocol().getStringRep(Keywords.POST), postMethod);
            deleteMethod = AHttpServlet.class.getDeclaredMethod("doDelete", HttpRequest.class, HttpResponseBuilder.class);
            this.invocationMap.put(Protocol.getProtocol().getStringRep(Keywords.DELETE), deleteMethod);
            headMethod = AHttpServlet.class.getDeclaredMethod("doHead", HttpRequest.class, HttpResponseBuilder.class);
            this.invocationMap.put(Protocol.getProtocol().getStringRep(Keywords.HEAD), headMethod);
        } catch (NoSuchMethodException e) {
            this.invocationMap.clear();
            e.printStackTrace();
        }
        this.servletMap = new HashMap<>();
		this.classLoader = classLoader;
		this.filePath = filePath;
		this.init();
		this.validStatus = this.parseConfigFile();
	}

	public final boolean isValid() {
	    return this.validStatus;
    }

	public abstract void init();

	public abstract void destroy();

	public final boolean parseConfigFile() {
		/*
		 * Config file:
		 * Request Type,Relative URI,Servlet Class
		 * HEAD,/users/{id}/edu.rosehulman.userapp.UserServlet
		 */
        InputStream configStream = this.classLoader.getResourceAsStream("./config.csv");

		if (configStream == null) {
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
                        servletClassName = servletClassName.replace(URI_DELIMETER, PATH_REPLACEMENT_DELIMETER).trim();
                        Class<?> servletClass = this.classLoader.loadClass(servletClassName);
                        Constructor<?> servletConstructor = servletClass.getConstructor(String.class);
                        AHttpServlet servletInstance = (AHttpServlet) servletConstructor.newInstance(this.filePath);
                        if (relativeUri == null || relativeUri.isEmpty()) {
                            return false;
                        }
                        this.servletMap.put(relativeUri, servletInstance);
                        relativeUri = null;
                        servletClassName = null;
                        delimited_values = -1;
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
            this.invocationMap.get(request.getMethod()).invoke(servlet, request, responseBuilder);
        } catch (IllegalAccessException | InvocationTargetException e) {
            SwsLogger.errorLogger.error("Invocation Failure.", e);
        }

        return responseBuilder.generateResponse();
	}
}
