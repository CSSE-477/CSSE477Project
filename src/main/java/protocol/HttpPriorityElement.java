package protocol;

import handlers.ConnectionHandler;

import java.time.LocalDateTime;

/**
 * Created by TrottaSN on 2/7/2017.
 *
 */
public class HttpPriorityElement {

    private HttpRequest request;
    private LocalDateTime localDateTime;
    private ConnectionHandler connectionHandler;

    public HttpPriorityElement(HttpRequest request, ConnectionHandler connectionHandler) {
        this.request = request;
        this.localDateTime = LocalDateTime.now();
        this.connectionHandler = connectionHandler;
    }

    public HttpRequest getRequest(){
        return this.request;
    }

    public LocalDateTime getTime() {
        return this.localDateTime;
    }

    public ConnectionHandler getHandler() {
        return this.connectionHandler;
    }
}
