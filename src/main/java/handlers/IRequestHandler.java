package handlers;

import protocol.HttpRequest;
import protocol.HttpResponse;

/**
 * Created by TrottaSN on 1/23/2017.
 */
public interface IRequestHandler {
    HttpResponse handleRequest(HttpRequest request);
}
