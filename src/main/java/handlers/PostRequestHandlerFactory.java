package handlers;

/**
 * Created by TrottaSN on 1/24/2017.
 */
public class PostRequestHandlerFactory implements IRequestHandlerFactory {

    private String rootDirectory;

    public PostRequestHandlerFactory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public IRequestHandler getRequestHandler() {
        return new PostRequestHandler(this.rootDirectory);
    }
}
