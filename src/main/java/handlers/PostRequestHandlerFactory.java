package handlers;

import protocol.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by TrottaSN on 1/24/2017.
 *
 */
public class PostRequestHandlerFactory implements IRequestHandlerFactory {

    private String rootDirectory;

    public PostRequestHandlerFactory(String rootDirectory){
        this.rootDirectory = rootDirectory;
    }
    @Override
    public IRequestHandler getRequestHandler() {
        return new PostRequestHandler(this.rootDirectory);
    }

    /**
     * Created by TrottaSN on 1/24/2017.
     *
     */
    public class PostRequestHandler implements IRequestHandler {

        private String rootDirectory;

        PostRequestHandler(String rootDirectory) {
            this.rootDirectory = rootDirectory;
        }

        @Override
        public HttpResponse handleRequest(HttpRequest request) {
            String fileRequested = request.getUri();
            String fullPath = this.rootDirectory.concat(fileRequested);
            File testFile = new File(fullPath);
            if (!testFile.exists()) {
                try {
                    testFile.createNewFile();
                } catch (IOException e) {
                    return (new HttpResponseBuilder(500)).generateResponse();
                }
            }
            else if(testFile.isDirectory()){
                return (new HttpResponseBuilder(400)).generateResponse();
            }
            FileWriter fw;
            try {
                fw = new FileWriter(testFile, true);
                System.out.println(request.getHeader());
                int amount = Integer.parseInt(request.getHeader()
                        .get(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH)));
                fw.write(new String(request.getBody()), 0, amount);
                fw.close();
            } catch (IOException e) {
                return (new HttpResponseBuilder(500)).generateResponse();
            }
            return (new HttpResponseBuilder(200)).setFile(testFile).generateResponse();
        }
    }
}
