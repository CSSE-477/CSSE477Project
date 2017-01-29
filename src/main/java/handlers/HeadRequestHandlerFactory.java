package handlers;

import protocol.ProtocolConfiguration;

/**
 * Created by Trowbrct on 1/25/2017.
 *
 */
public class HeadRequestHandlerFactory implements IRequestHandlerFactory {

	private String rootDirectory;
	private ProtocolConfiguration protocol;

	public HeadRequestHandlerFactory(String rootDirectory, ProtocolConfiguration protocol){
		this.rootDirectory = rootDirectory;
		this.protocol = protocol;
	}
	@Override
	public IRequestHandler getRequestHandler() {
		return new HeadRequestHandler(this.rootDirectory, this.protocol);
	}

}
