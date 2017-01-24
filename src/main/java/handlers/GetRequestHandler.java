package handlers;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

import java.io.File;

/**
 * Created by TrottaSN on 1/24/2017.
 */
public class GetRequestHandler implements IRequestHandler {

    private String rootDirectory;

    public GetRequestHandler(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        HttpResponse response;
        String uri = request.getUri();
        File file = new File(this.rootDirectory + uri);
        if(file.exists()) {
            if(file.isDirectory()) {
                String location = this.rootDirectory + uri + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
                file = new File(location);
                if(file.exists()) {
                    response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
                }
                else {
                    response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
                }
            }
            else {
                response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
            }
        }
        else {
            response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
        }
        return response;
    }
}
