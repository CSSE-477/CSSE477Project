package handlers;

import handlers.IRequestHandler;

/**
 * Created by TrottaSN on 1/23/2017.
 */
public interface IRequestHandlerFactory {
    IRequestHandler getRequestHandler();
}
