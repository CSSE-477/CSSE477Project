package protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TrottaSN on 1/29/2017.
 *
 */
public class Protocol {

    private static Protocol protocolInstance;

    public static synchronized Protocol getProtocol() {
        if (null == protocolInstance) {
            synchronized (Protocol.class) {
                protocolInstance = new Protocol();
            }
        }
        return protocolInstance;
    }

    private Map<Integer, Keywords> codeMap;
    private Map<Keywords, String> keyMap;

    private Protocol() {

        HashMap<Integer, Keywords> codeMap = new HashMap<>();
        codeMap.put(200, Keywords.OK);
        codeMap.put(301, Keywords.MOVED_PERMANENTLY);
        codeMap.put(304, Keywords.NOT_MODIFIED);
        codeMap.put(400, Keywords.BAD_REQUEST);
        codeMap.put(404, Keywords.NOT_FOUND);
        codeMap.put(500, Keywords.INTERNAL_SERVER_ERROR);
        codeMap.put(501, Keywords.NOT_IMPLEMENTED);
        codeMap.put(505, Keywords.HTTP_VERSION_NOT_SUPPORTED);
        codeMap.put(204, Keywords.NO_CONTENT);

        HashMap<Keywords, String> keyMap = new HashMap<>();
        keyMap.put(Keywords.SPACE, " ");
        keyMap.put(Keywords.SEPARATOR, ":");
        keyMap.put(Keywords.SLASH, "/");
        keyMap.put(Keywords.CR, "\r");
        keyMap.put(Keywords.LF, "\n");
        keyMap.put(Keywords.VERSION, "HTTP/1.1");
        keyMap.put(Keywords.GET, "GET");
        keyMap.put(Keywords.HEAD, "HEAD");
        keyMap.put(Keywords.DELETE, "DELETE");
        keyMap.put(Keywords.POST, "POST");
        keyMap.put(Keywords.PUT, "PUT");
        keyMap.put(Keywords.DATE, "Date");
        keyMap.put(Keywords.SERVER, "Server");
        keyMap.put(Keywords.LAST_MODIFIED, "Last-Modified");
        keyMap.put(Keywords.CONTENT_LENGTH, "Content-Length");
        keyMap.put(Keywords.CONTENT_TYPE, "Content-Type");
        keyMap.put(Keywords.HOST, "Host");
        keyMap.put(Keywords.CONNECTION, "Connection");
        keyMap.put(Keywords.USER_AGENT, "User-Agent");
        keyMap.put(Keywords.SERVER_INFO, "SimpleWebServer(SWS)/1.0.0");
        keyMap.put(Keywords.PROVIDER, "Provider");
        keyMap.put(Keywords.AUTHOR, "Yolo Swag");
        keyMap.put(Keywords.CLOSE, "Close");
        keyMap.put(Keywords.OPEN, "Keep-Alive");
        keyMap.put(Keywords.DEFAULT_FILE, "index.html");
        keyMap.put(Keywords.MIME_TEXT, "text");
        keyMap.put(Keywords.NO_CONTENT, "No Content");

        this.keyMap = Collections.unmodifiableMap(keyMap);
        this.codeMap = Collections.unmodifiableMap(codeMap);
    }

    public String getStringRep(Keywords field){
        return this.keyMap.get(field);
    }

    public Keywords getCodeKeyword(Integer code){
        return this.codeMap.get(code);
    }

    /**
     * Returns a formatted String containing server information.<br/>
     * e.g. <tt>SimpleWebServer(SWS)/1.0.0 (Mac OS X/10.5.8/i386)</tt>
     * @return
     */
    String getServerInfo() {
        String os = System.getProperty("os.name"); // e.g. Mac OSX, Ubuntu, etc.
        String osVersion = System.getProperty("os.version"); // e.g. 10.5, 10.0.4, etc
        String architecture = System.getProperty("os.arch"); // e.g. i386, x86_64, etc
        String serverInfo = this.getStringRep(Keywords.SERVER_INFO) +
                this.getStringRep(Keywords.SLASH) + "(" + os +
                this.getStringRep(Keywords.SLASH) + osVersion +
                this.getStringRep(Keywords.SLASH) + architecture + ")";
        return serverInfo;
    }
}
