package servlet;

import protocol.HttpRequest;
import protocol.HttpResponse;

public interface IHttpServlet {

	public void init();
	
	public void destroy();
	
	public void doGet(HttpRequest request, HttpResponse response);
	
	public void doHead(HttpRequest request, HttpResponse response);

	public void doPost(HttpRequest request, HttpResponse response);
	
	public void doPut(HttpRequest request, HttpResponse response);
	
	public void doDelete(HttpRequest request, HttpResponse response);

}
