package handlers;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;
import utils.SwsLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by TrottaSN on 1/24/2017.
 */
public class PutRequestHandler implements IRequestHandler {

    private String rootDirectory;

    public PutRequestHandler(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        String fileRequested = request.getUri();
        String fullPath = this.rootDirectory.concat(fileRequested);
        File testFile = new File(fullPath);
        if(testFile.isDirectory()){
        	SwsLogger.errorLogger.error("PUT: Requested file is a directory, responding with 400 Bad Request.");
            return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
        }
        if (!testFile.exists()) {
            try {
                testFile.createNewFile();
            } catch (IOException e) {
            	SwsLogger.errorLogger.error("PUT: IOException encountered while creating file, responding with 500 Internal Server Error.");
                return HttpResponseFactory.create500InternalServerError(Protocol.CLOSE);
            }
        }
        FileWriter fw;
        try {
            fw = new FileWriter(testFile, false);
            int amount = Integer.parseInt(request.getHeader().get(Protocol.CONTENT_LENGTH));
            fw.write(new String(request.getBody()), 0, amount);
            fw.close();
        } catch (IOException e) {
        	SwsLogger.errorLogger.error("PUT: IOException encountered while writing to file, responding with 500 Internal Server Error.");
            return HttpResponseFactory.create500InternalServerError(Protocol.CLOSE);
        }
    	SwsLogger.accessLogger.info("PUT: Executed succesfully, responding with 200 OK.");
        return HttpResponseFactory.create200OK(testFile, Protocol.CLOSE);
    }
}
