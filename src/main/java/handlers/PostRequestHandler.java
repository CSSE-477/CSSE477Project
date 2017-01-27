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
public class PostRequestHandler implements IRequestHandler {

    private String rootDirectory;

    public PostRequestHandler(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        String fileRequested = request.getUri();
        String fullPath = this.rootDirectory.concat(fileRequested);
        File testFile = new File(fullPath);
        if(testFile.isDirectory()){
            String errorMessage = "POST - Bad Request";
            SwsLogger.errorLogger.error(errorMessage);
            return HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
        }
        if (!testFile.exists()) {
            try {
                testFile.createNewFile();
            } catch (IOException e) {
                String errorMessage = "POST - I/O failed creating new file. " + testFile.getAbsolutePath();
                SwsLogger.errorLogger.error(errorMessage);
                return HttpResponseFactory.create500InternalServerError(Protocol.CLOSE);
            }
        }
        FileWriter fw;
        try {
            fw = new FileWriter(testFile, true);
            System.out.println(request.getHeader());
            int amount = Integer.parseInt(request.getHeader().get(Protocol.CONTENT_LENGTH));
            fw.write(new String(request.getBody()), 0, amount);
            fw.close();
        } catch (IOException e) {
            String errorMessage = "POST - I/O failed when writing and closing file.";
            SwsLogger.errorLogger.error(errorMessage);
            return HttpResponseFactory.create500InternalServerError(Protocol.CLOSE);
        }
        String successMessage = "POST - sent successful 200 OK";
        SwsLogger.accessLogger.info(successMessage);
        return HttpResponseFactory.create200OK(testFile, Protocol.CLOSE);
    }
}
