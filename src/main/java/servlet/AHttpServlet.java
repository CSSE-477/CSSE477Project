package servlet;

import protocol.HttpRequest;
import protocol.HttpResponseBuilder;

public abstract class AHttpServlet {

	private String resourcePath;

	public AHttpServlet(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public abstract void init();
	
	public abstract void destroy();
	
	public abstract void doGet(HttpRequest request, HttpResponseBuilder responseBuilder);
	
	public abstract void doHead(HttpRequest request, HttpResponseBuilder responseBuilder);

	public abstract void doPost(HttpRequest request, HttpResponseBuilder responseBuilder);
	
	public abstract void doPut(HttpRequest request, HttpResponseBuilder responseBuilder);
	
	public abstract void doDelete(HttpRequest request, HttpResponseBuilder responseBuilder);

}
