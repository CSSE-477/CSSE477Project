package handlers;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.Protocol;
import utils.SwsLogger;

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
        	response = (new HttpResponseBuilder(404, Protocol.CLOSE)).generateResponse();
        } else if (file.isDirectory()) {
        	// check for default file before sending 404
            String location = this.rootDirectory.concat(uri).concat(System.getProperty("file.separator")).concat(Protocol.DEFAULT_FILE);
            file = new File(location);
            if(file.exists()) {
                response = (new HttpResponseBuilder(200, Protocol.CLOSE)).setFile(file).generateResponse();
            }
            else {
                response = (new HttpResponseBuilder(404, Protocol.CLOSE)).generateResponse();
            }
        } else {
        	response = (new HttpResponseBuilder(400, Protocol.CLOSE)).setFile(file).generateResponse();
        }

        return response;
    }
}
