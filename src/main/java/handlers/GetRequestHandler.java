package handlers;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
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
			SwsLogger.errorLogger
			.error("GET to file " + file.getAbsolutePath() + ". Sending 404 Not Found");
        	response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
        } else if (file.isDirectory()) {
        	// check for default file before sending 404
            String location = this.rootDirectory.concat(uri).concat(System.getProperty("file.separator")).concat(Protocol.DEFAULT_FILE);
            file = new File(location);
            if(file.exists()) {
    			SwsLogger.accessLogger
    			.info("GET to file " + file.getAbsolutePath() + ". Sending 200 OK");
                response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
            }
            else {
    			SwsLogger.errorLogger
    			.error("GET to file " + file.getAbsolutePath() + ". Sending 404 Not Found");
                response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
            }
        } else {
			SwsLogger.accessLogger
			.info("GET to file " + file.getAbsolutePath() + ". Sending 200 OK");
        	response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
        }

        return response;
    }
}
