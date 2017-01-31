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
    private ProtocolConfiguration protocol;

    public PostRequestHandlerFactory(String rootDirectory, ProtocolConfiguration protocol){
        this.rootDirectory = rootDirectory;
        this.protocol = protocol;
    }
    @Override
    public IRequestHandler getRequestHandler() {
        return new PostRequestHandler(this.rootDirectory, this.protocol);
    }

    /**
     * Created by TrottaSN on 1/24/2017.
     *
     */
    public class PostRequestHandler implements IRequestHandler {

        private String rootDirectory;
        private ProtocolConfiguration protocol;

        PostRequestHandler(String rootDirectory, ProtocolConfiguration protocol) {
            this.rootDirectory = rootDirectory;
            this.protocol = protocol;
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
                    return (new HttpResponseBuilder(500, this.protocol)).generateResponse();
                }
            }
            else if(testFile.isDirectory()){
                return (new HttpResponseBuilder(400, this.protocol)).generateResponse();
            }
            FileWriter fw;
            try {
                fw = new FileWriter(testFile, true);
                System.out.println(request.getHeader());
                int amount = Integer.parseInt(request.getHeader().get(this.protocol
                        .getResponseHeader(ResponseHeaders.CONTENT_LENGTH)));
                fw.write(new String(request.getBody()), 0, amount);
                fw.close();
            } catch (IOException e) {
                return (new HttpResponseBuilder(500, this.protocol)).generateResponse();
            }
            return (new HttpResponseBuilder(200, this.protocol)).setFile(testFile).generateResponse();
        }
    }
}
