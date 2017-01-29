package protocol;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TrottaSN on 1/27/2017.
 *
 */
public class HttpResponseBuilder {

    private static final int DEFAULT_STATUS_CODE = 500;

    private HashMap<Integer, String> defaultPhraseMap;
    private String version;
    private int status;
    private String phrase;
    private Map<String, String> header;
    private File file;
    private ProtocolConfiguration protocol;

    private HttpResponseBuilder(String version, int status, String phrase, Map<String, String> header, File file, String connection) {

        this.defaultPhraseMap = new HashMap<>();
        this.defaultPhraseMap.put(200, "OK");
        this.defaultPhraseMap.put(301, "Moved Permanently");
        this.defaultPhraseMap.put(304, "Not Modified");
        this.defaultPhraseMap.put(400, "Bad Request");
        this.defaultPhraseMap.put(404, "Not Found");
        this.defaultPhraseMap.put(500, "Internal Server Error");
        this.defaultPhraseMap.put(501, "Not Implemented");
        this.defaultPhraseMap.put(505, "HTTP Version Not Supported");

        if(version == null){
            this.version = this.protocol.getProtocolElement(ProtocolConfiguration.ProtocolElements.VERSION);
        }
        if(status == -1){
            this.status = DEFAULT_STATUS_CODE;
        }
        if(phrase == null){
            this.phrase = this.defaultPhraseMap.get(this.status);
        }
        if(header == null){
            this.header = new HashMap<>();
            this.header.put(this.protocol.getRequestHeader(ProtocolConfiguration.RequestHeaders.CONNECTION), connection);

            // Lets add current date
            Date date = Calendar.getInstance().getTime();
            this.header.put(this.protocol.getResponseHeader(ProtocolConfiguration.ResponseHeaders.DATE), date.toString());

            // Lets add server info
            this.header.put(this.protocol.getResponseHeader(ProtocolConfiguration.ResponseHeaders.SERVER), this.protocol.getServerInfo());

            // Lets add extra header with provider info
            this.header.put(this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.PROVIDER), this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.AUTHOR));
        }
        if(file == null) {
            this.file = null;
        }
    }

    public HttpResponseBuilder(String connection){
        this(null, -1, null, null, null, connection);
    }

    public HttpResponseBuilder(int status, String connection){
        this(null, status, null, null, null, connection);
    }

    public HttpResponseBuilder setVersion(String version){
        this.version = version;
        return this;
    }

    public HttpResponseBuilder setStatus(int status){
        this.status = status;
        return this;
    }

    public HttpResponseBuilder setPhrase(String phrase){
        this.phrase = phrase;
        return this;
    }

    public HttpResponseBuilder setHeader(Map<String, String> header){
        this.header = header;
        return this;
    }

    public HttpResponseBuilder putHeader(String key, String value) {
        if(this.header == null){
            this.header = new HashMap<>();
        }
        this.header.put(key, value);
        return this;
    }

    public HttpResponseBuilder setFile(File file){
        this.file = file;
        if(this.header == null){
            this.header = new HashMap<>();
        }
        // Lets add last modified date for the file
        long timeSinceEpoch = file.lastModified();
        Date modifiedTime = new Date(timeSinceEpoch);
        this.header.put(this.protocol.getResponseHeader(ProtocolConfiguration.ResponseHeaders.LAST_MODIFIED), modifiedTime.toString());

        // Lets get content length in bytes
        long length = file.length();
        this.header.put(this.protocol.getResponseHeader(ProtocolConfiguration.ResponseHeaders.CONTENT_LENGTH), length + "");

        // Lets get MIME type for the file
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mime = fileNameMap.getContentTypeFor(file.getName());
        // The fileNameMap cannot find mime type for all of the documents, e.g. doc, odt, etc.
        // So we will not add this field if we cannot figure out what a mime type is for the file.
        // Let browser do this job by itself.
        if(mime != null) {
            this.header.put(this.protocol.getResponseHeader(ProtocolConfiguration.ResponseHeaders.CONTENT_TYPE), mime);
        }
        return this;
    }

    public HttpResponse generateResponse() {
        HttpResponse response = new HttpResponse(this.version, this.status, this.phrase, this.header, this.file, this.protocol);
        return response;
    }
}
