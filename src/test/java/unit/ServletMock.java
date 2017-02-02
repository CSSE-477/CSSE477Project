package unit;

import protocol.HttpRequest;
import protocol.HttpResponseBuilder;
import servlet.AHttpServlet;

/**
 * Created by TrottaSN on 2/2/2017.
 *
 */
public class ServletMock extends AHttpServlet {

    public ServletMock(String resourcePath) {
        super(resourcePath);
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doGet(HttpRequest request, HttpResponseBuilder responseBuilder) {

    }

    @Override
    public void doHead(HttpRequest request, HttpResponseBuilder responseBuilder) {

    }

    @Override
    public void doPost(HttpRequest request, HttpResponseBuilder responseBuilder) {

    }

    @Override
    public void doPut(HttpRequest request, HttpResponseBuilder responseBuilder) {

    }

    @Override
    public void doDelete(HttpRequest request, HttpResponseBuilder responseBuilder) {

    }
}
