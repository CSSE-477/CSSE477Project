package handlers;

import protocol.ProtocolConfiguration;

/**
 * Created by TrottaSN on 1/24/2017.
 *
 */
public class PutRequestHandlerFactory implements IRequestHandlerFactory {

    private String rootDirectory;
    private ProtocolConfiguration protocol;

    public PutRequestHandlerFactory(String rootDirectory, ProtocolConfiguration protocol){
        this.rootDirectory = rootDirectory;
        this.protocol = protocol;
    }

    @Override
    public IRequestHandler getRequestHandler() {
        return new PutRequestHandler(this.rootDirectory, this.protocol);
    }
}
