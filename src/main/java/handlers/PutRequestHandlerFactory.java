package handlers;

/**
 * Created by TrottaSN on 1/24/2017.
 */
public class PutRequestHandlerFactory implements IRequestHandlerFactory {

    private String rootDirectory;

    public PutRequestHandlerFactory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public IRequestHandler getRequestHandler() {
        return new PutRequestHandler(this.rootDirectory);
    }
}
