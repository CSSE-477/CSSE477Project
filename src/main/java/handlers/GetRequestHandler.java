package handlers;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.ProtocolConfiguration;

import java.io.File;

/**
 * Created by TrottaSN on 1/24/2017.
 *
 */
public class GetRequestHandler implements IRequestHandler {

    private String rootDirectory;
    private ProtocolConfiguration protocol;

    public GetRequestHandler(String rootDirectory, ProtocolConfiguration protocol) {
        this.rootDirectory = rootDirectory;
        this.protocol = protocol;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        HttpResponse response;
        String uri = request.getUri();
        File file = new File(this.rootDirectory.concat(uri));

        if (!file.exists()) {
        	response = (new HttpResponseBuilder(404, this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.CLOSE))).generateResponse();
        } else if (file.isDirectory()) {
        	// check for default file before sending 404
            String location = this.rootDirectory.concat(uri).concat(System.getProperty("file.separator")).concat(this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.DEFAULT_FILE));
            file = new File(location);
            if(file.exists()) {
                response = (new HttpResponseBuilder(200, this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.CLOSE))).setFile(file).generateResponse();
            }
            else {
                response = (new HttpResponseBuilder(404, this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.CLOSE))).generateResponse();
            }
        } else {
        	response = (new HttpResponseBuilder(400, this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.CLOSE))).setFile(file).generateResponse();
        }

        return response;
    }
}
