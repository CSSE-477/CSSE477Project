package handlers;

/**
 * Created by Trowbrct on 1/25/2017.
 */
public class HeadRequestHandlerFactory implements IRequestHandlerFactory {

	private String rootDirectory;

	public HeadRequestHandlerFactory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	@Override
	public IRequestHandler getRequestHandler() {
		return new HeadRequestHandler(this.rootDirectory);
	}

}
