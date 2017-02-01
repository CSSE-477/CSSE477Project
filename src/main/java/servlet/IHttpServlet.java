package servlet;

import protocol.HttpRequest;
import protocol.HttpResponseBuilder;

public interface IHttpServlet {

	public void init();
	
	public void destroy();
	
	public void doGet(HttpRequest request, HttpResponseBuilder responseBuilder);
	
	public void doHead(HttpRequest request, HttpResponseBuilder responseBuilder);

	public void doPost(HttpRequest request, HttpResponseBuilder responseBuilder);
	
	public void doPut(HttpRequest request, HttpResponseBuilder responseBuilder);
	
	public void doDelete(HttpRequest request, HttpResponseBuilder responseBuilder);

}
