package handlers;

/**
 * Created by TrottaSN on 1/24/2017.
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
}
