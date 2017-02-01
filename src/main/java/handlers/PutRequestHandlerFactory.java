package handlers;

import protocol.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by TrottaSN on 1/24/2017.
 *
 */
public class PutRequestHandlerFactory implements IRequestHandlerFactory {

    private String rootDirectory;

    public PutRequestHandlerFactory(String rootDirectory){
        this.rootDirectory = rootDirectory;
    }

    @Override
    public IRequestHandler getRequestHandler() {
        return new PutRequestHandler(this.rootDirectory);
    }

    /**
     * Created by TrottaSN on 1/24/2017.
     *
     */
    public class PutRequestHandler implements IRequestHandler {

        private String rootDirectory;

        PutRequestHandler(String rootDirectory) {
            this.rootDirectory = rootDirectory;
        }

        @Override
        public HttpResponse handleRequest(HttpRequest request) {
            String fileRequested = request.getUri();
            String fullPath = this.rootDirectory.concat(fileRequested);
            File testFile = new File(fullPath);
            if(testFile.isDirectory()){
                return (new HttpResponseBuilder(400)).generateResponse();
            }
            if (!testFile.exists()) {
                try {
                    testFile.createNewFile();
                } catch (IOException e) {
                    return (new HttpResponseBuilder(500)).generateResponse();
                }
            }
            FileWriter fw;
            try {
                fw = new FileWriter(testFile, false);
                int amount = Integer.parseInt(
                        request.getHeader().get(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH)));
                fw.write(new String(request.getBody()), 0, amount);
                fw.close();
            } catch (IOException e) {
                return (new HttpResponseBuilder(500)).generateResponse();
            }
            return (new HttpResponseBuilder(200)).setFile(testFile).generateResponse();
        }
    }
}
