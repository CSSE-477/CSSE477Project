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
        File file = new File(this.rootDirectory.concat(uri));
        
        if (!file.exists()) {
        	response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
        } else if (file.isDirectory()) {
        	// check for default file before sending 404
            String location = this.rootDirectory.concat(uri).concat(System.getProperty("file.separator")).concat(Protocol.DEFAULT_FILE);
            file = new File(location);
            if(file.exists()) {
                response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
            }
            else {
                response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
            }
        } else {
        	response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
        }

        return response;
    }
}
