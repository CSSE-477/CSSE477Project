package handlers;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
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
            return (new HttpResponseBuilder(400, Protocol.CLOSE)).generateResponse();
        }
        if (!testFile.exists()) {
            try {
                testFile.createNewFile();
            } catch (IOException e) {
                return (new HttpResponseBuilder(500, Protocol.CLOSE)).generateResponse();
            }
        }
        FileWriter fw;
        try {
            fw = new FileWriter(testFile, false);
            int amount = Integer.parseInt(request.getHeader().get(Protocol.CONTENT_LENGTH));
            fw.write(new String(request.getBody()), 0, amount);
            fw.close();
        } catch (IOException e) {
            return (new HttpResponseBuilder(500, Protocol.CLOSE)).generateResponse();
        }
    	SwsLogger.accessLogger.info("PUT: Executed succesfully, responding with 200 OK.");
        return (new HttpResponseBuilder(200, Protocol.CLOSE)).setFile(testFile).generateResponse();
    }
}
