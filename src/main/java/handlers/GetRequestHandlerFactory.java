package handlers;

import protocol.ProtocolConfiguration;

/**
 * Created by TrottaSN on 1/24/2017.
 *
 */
public class GetRequestHandlerFactory implements IRequestHandlerFactory{

    private String rootDirectory;
    private ProtocolConfiguration protocol;

    public GetRequestHandlerFactory(String rootDirectory, ProtocolConfiguration protocol){
        this.rootDirectory = rootDirectory;
        this.protocol = protocol;
    }
    @Override
    public IRequestHandler getRequestHandler() {
        return new GetRequestHandler(this.rootDirectory, this.protocol);
    }
}
