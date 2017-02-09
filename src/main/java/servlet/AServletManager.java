package servlet;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

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
	protected static final String ENCODING = "UTF-8";

    protected ClassLoader classLoader;
    
    private boolean borkMode;

	public AServletManager(String filePath, ClassLoader classLoader) {
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
		this.validStatus = this.parseConfigFile();
		this.borkMode = false;
        this.init();
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
        String  thisLine;
        String relativeUri;
        String servletClassName;
        BufferedReader br = null;
		try {
            // open input stream test.txt for reading purpose.
            br = new BufferedReader(new InputStreamReader(configStream, ENCODING));
            while ((thisLine = br.readLine()) != null) {

                thisLine = thisLine.trim();
                String[] lineSplit = thisLine.split(CONFIG_DELIMETER);

                if(lineSplit.length != 2){
                    return false;
                }

                relativeUri = lineSplit[0];

                if (relativeUri == null) {
                    return false;
                }

                // extract "users" from "/users/{id}"
                String[] relativeSplit = relativeUri.split(URI_DELIMETER);

                if (relativeSplit.length < 1) {
                    return false;
                }

                relativeUri = relativeSplit[1];

                servletClassName = lineSplit[1];

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
            }
        }
        catch (Exception e) {
            SwsLogger.errorLogger.error("Exception while parsing config file.", e);
            return false;
        } finally {
        	if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    SwsLogger.errorLogger.error("Exception closing BufferedReader", e);
                    return false;
                }
            }
        }
		return true;
	}

	public HttpResponse handleRequest(HttpRequest request) {
		
		if (request.getUri().contains("bork")) {
        	// plugin-borking easter egg
			enableBorkMode();
        } else if (request.getUri().contains("plsfix")) {
        	// plugin-unborking easter egg
			disableBorkMode();
        }
		
		if (this.borkMode) {
			SwsLogger.errorLogger.error("BORK MODE enabled, borking request!");
			String bork = null;
			bork.indexOf("bork");
		}

        HttpResponseBuilder responseBuilder = new HttpResponseBuilder();

		String uri = request.getUri();
		// should look like "/userapp/users/{id}"
        String[] uriSplit = uri.split(URI_DELIMETER);
        
        if(uriSplit.length <= 1){
            return responseBuilder.generateResponse();
        }

		String servletKey = uriSplit[2];

		AHttpServlet servlet = this.servletMap.get(servletKey);

		if(servlet == null){
            SwsLogger.errorLogger.error("Could not find associated servlet. Caught Null.");
            return responseBuilder.generateResponse();
        }

        try {
            Method methodToInvoke = this.invocationMap.get(request.getMethod());
            if(methodToInvoke == null){
                SwsLogger.errorLogger.error("Invocation Failure Due to Lack of Method Support - AKA no Get or Post or etc. Caught Null.");
                return responseBuilder.generateResponse();
            }
            methodToInvoke.invoke(servlet, request, responseBuilder);
        } catch (IllegalAccessException | InvocationTargetException e) {
            SwsLogger.errorLogger.error("Invocation Failure.", e);
        }

        return responseBuilder.generateResponse();
	}
	
	
	private void enableBorkMode() {
		SwsLogger.accessLogger.info("***Easter egg detected, enabling BORK MODE!***");
		this.borkMode = true;
	}
	
	private void disableBorkMode() {
		SwsLogger.accessLogger.info("***Polite fix request detected, disabling BORK MODE!***");
		this.borkMode = false;
	}
	
	public boolean getHeartbeat() {
		return !this.borkMode;
	}
}
