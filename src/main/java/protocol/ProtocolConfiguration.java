package protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TrottaSN on 1/29/2017.
 *
 */
public class ProtocolConfiguration {

    public enum ProtocolElements {
        VERSION, GET, POST, PUT, HEAD, DELETE
    }

    public enum ResponseHeaders {
        DATE, SERVER, LAST_MODIFIED, CONTENT_LENGTH, CONTENT_TYPE
    }

    public enum RequestHeaders {
        HOST, CONNECTION, USER_AGENT
    }

    public enum ServerInfoFields {
        SERVER_INFO, PROVIDER, AUTHOR, CLOSE, OPEN, DEFAULT_FILE, MIME_TEXT
    }

    public enum CharsetConstants {
        SPACE, SEPARATOR, SLASH, CR, LF
    }

    private Map responseHeaderMap;
    private Map requestHeaderMap;
    private Map serverInfoMap;
    private Map protocolElementMap;
    private Map charsetConstantsMap;
    private Map defaultPhraseMap;

    public ProtocolConfiguration() {

        HashMap<Integer, String> defaultPhraseMap = new HashMap<>();
        defaultPhraseMap.put(200, "OK");
        defaultPhraseMap.put(301, "Moved Permanently");
        defaultPhraseMap.put(304, "Not Modified");
        defaultPhraseMap.put(400, "Bad Request");
        defaultPhraseMap.put(404, "Not Found");
        defaultPhraseMap.put(500, "Internal Server Error");
        defaultPhraseMap.put(501, "Not Implemented");
        defaultPhraseMap.put(505, "HTTP Version Not Supported");

        HashMap<CharsetConstants, Character> charsetConstantsMap = new HashMap<>();
        charsetConstantsMap.put(CharsetConstants.SPACE, ' ');
        charsetConstantsMap.put(CharsetConstants.SEPARATOR, ':');
        charsetConstantsMap.put(CharsetConstants.SLASH, '/');
        charsetConstantsMap.put(CharsetConstants.CR, '\r');
        charsetConstantsMap.put(CharsetConstants.LF, '\n');

        HashMap<ProtocolElements, String> protocolElementMap = new HashMap<>();
        protocolElementMap.put(ProtocolElements.VERSION, "HTTP/1.1");
        protocolElementMap.put(ProtocolElements.GET, "GET");
        protocolElementMap.put(ProtocolElements.HEAD, "HEAD");
        protocolElementMap.put(ProtocolElements.DELETE, "DELETE");
        protocolElementMap.put(ProtocolElements.POST, "POST");
        protocolElementMap.put(ProtocolElements.PUT, "PUT");

        HashMap<ResponseHeaders, String> responseHeaderMap = new HashMap<>();
        responseHeaderMap.put(ResponseHeaders.DATE, "Date");
        responseHeaderMap.put(ResponseHeaders.SERVER, "Server");
        responseHeaderMap.put(ResponseHeaders.LAST_MODIFIED, "Last-Modified");
        responseHeaderMap.put(ResponseHeaders.CONTENT_LENGTH, "Content-Length");
        responseHeaderMap.put(ResponseHeaders.CONTENT_TYPE, "Content-Type");

        HashMap<RequestHeaders, String> requestHeaderMap = new HashMap<>();
        requestHeaderMap.put(RequestHeaders.HOST, "Host");
        requestHeaderMap.put(RequestHeaders.CONNECTION, "Connection");
        requestHeaderMap.put(RequestHeaders.USER_AGENT, "User-Agent");

        HashMap<ServerInfoFields, String> serverInfoMap = new HashMap<>();
        serverInfoMap.put(ServerInfoFields.SERVER_INFO, "SimpleWebServer(SWS)/1.0.0");
        serverInfoMap.put(ServerInfoFields.PROVIDER, "Provider");
        serverInfoMap.put(ServerInfoFields.AUTHOR, "Yolo Swag");
        serverInfoMap.put(ServerInfoFields.CLOSE, "Close");
        serverInfoMap.put(ServerInfoFields.OPEN, "Keep-Alive");
        serverInfoMap.put(ServerInfoFields.DEFAULT_FILE, "index.html");
        serverInfoMap.put(ServerInfoFields.MIME_TEXT, "text");

        this.responseHeaderMap = Collections.unmodifiableMap(responseHeaderMap);
        this.requestHeaderMap = Collections.unmodifiableMap(requestHeaderMap);
        this.serverInfoMap = Collections.unmodifiableMap(serverInfoMap);
        this.protocolElementMap = Collections.unmodifiableMap(protocolElementMap);
        this.charsetConstantsMap = Collections.unmodifiableMap(charsetConstantsMap);
        this.defaultPhraseMap = Collections.unmodifiableMap(defaultPhraseMap);
    }

    public String getServerInfo(ServerInfoFields field){
        return (String)this.serverInfoMap.get(field);
    }

    public String getRequestHeader(RequestHeaders header){
        return (String)this.requestHeaderMap.get(header);
    }

    public String getResponseHeader(ResponseHeaders header){
        return (String)this.responseHeaderMap.get(header);
    }

    public String getProtocolElement(ProtocolElements element) {
        return (String)this.protocolElementMap.get(element);
    }

    public String getPhrase(Integer code) {
        return (String)this.defaultPhraseMap.get(code);
    }

    public Character getCharsetConstant(CharsetConstants constant) {
        return (Character)this.charsetConstantsMap.get(constant);
    }

    /**
     * Returns a formatted String containing server information.<br/>
     * e.g. <tt>SimpleWebServer(SWS)/1.0.0 (Mac OS X/10.5.8/i386)</tt>
     * @return
     */
    public String getServerInfo() {
        String os = System.getProperty("os.name"); // e.g. Mac OSX, Ubuntu, etc.
        String osVersion = System.getProperty("os.version"); // e.g. 10.5, 10.0.4, etc
        String architecture = System.getProperty("os.arch"); // e.g. i386, x86_64, etc
        String serverInfo = this.getServerInfo(ProtocolConfiguration.ServerInfoFields.SERVER_INFO) + this.getCharsetConstant(ProtocolConfiguration.CharsetConstants.SLASH) +
                "(" + os + this.getCharsetConstant(ProtocolConfiguration.CharsetConstants.SLASH) + osVersion + this.getCharsetConstant(ProtocolConfiguration.CharsetConstants.SLASH) + architecture + ")";
        return serverInfo;
    }
}
