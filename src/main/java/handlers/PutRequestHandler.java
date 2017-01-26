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
        	System.out.println("Here");
            return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
        }
        if (!testFile.exists()) {
            try {
                testFile.createNewFile();
            } catch (IOException e) {
                SwsLogger.errorLogger.error(e.getStackTrace().toString());
                return HttpResponseFactory.create500InternalServerError(Protocol.CLOSE);
            }
        }
        FileWriter fw;
        try {
            fw = new FileWriter(testFile, false);
            System.out.println(request.getHeader());
            int amount = Integer.parseInt(request.getHeader().get(Protocol.CONTENT_LENGTH));
            fw.write(new String(request.getBody()), 0, amount);
            fw.close();
        } catch (IOException e) {
            SwsLogger.errorLogger.error(e.getMessage());
            return HttpResponseFactory.create500InternalServerError(Protocol.CLOSE);
        }
        return HttpResponseFactory.create200OK(testFile, Protocol.CLOSE);
    }
}
