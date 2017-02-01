package handlers;

import protocol.*;

import java.io.File;

/**
 * Created by TrottaSN on 1/24/2017.
 *
 */
public class GetRequestHandlerFactory implements IRequestHandlerFactory{

    private String rootDirectory;

    public GetRequestHandlerFactory(String rootDirectory){
        this.rootDirectory = rootDirectory;
    }
    @Override
    public IRequestHandler getRequestHandler() {
        return new GetRequestHandler(this.rootDirectory);
    }

    /**
     * Created by TrottaSN on 1/24/2017.
     *
     */
    public class GetRequestHandler implements IRequestHandler {

        private String rootDirectory;

        GetRequestHandler(String rootDirectory) {
            this.rootDirectory = rootDirectory;
        }

        @Override
        public HttpResponse handleRequest(HttpRequest request) {
            HttpResponse response;
            String uri = request.getUri();
            File file = new File(this.rootDirectory.concat(uri));

            if (!file.exists()) {
                response = (new HttpResponseBuilder(404)).generateResponse();
            } else if (file.isDirectory()) {
                // check for default file before sending 404
                String location = this.rootDirectory.concat(uri).concat(System.getProperty("file.separator"))
                        .concat(Protocol.getProtocol().getStringRep(Keywords.DEFAULT_FILE));
                file = new File(location);
                if(file.exists()) {
                    response = (new HttpResponseBuilder(200))
                            .putHeader(Protocol.getProtocol().getStringRep(Keywords.CONTENT_TYPE), "text/html")
                            .setFile(file).generateResponse();
                }
                else {
                    response = (new HttpResponseBuilder(404)).generateResponse();
                }
            } else {
                response = (new HttpResponseBuilder(200))
                        .putHeader(Protocol.getProtocol().getStringRep(Keywords.CONTENT_TYPE), "text/html")
                        .setFile(file).generateResponse();
            }

            return response;
        }
    }
}
