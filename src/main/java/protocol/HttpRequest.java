package protocol;

import utils.SwsLogger;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

/**
 * Represents a request object for HTTP.
 *
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class HttpRequest {

    private String method;
    private String uri;
    private String version;
    private Map<String, String> header;
    private char[] body;

    public HttpRequest() {
        this.header = new HashMap<>();
        this.body = new char[0];
    }

    public HttpRequest(String method, String uri, String version, Map<String, String> header, char[] body) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.header = header;
        this.body = body;
    }

    /**
     * The request method.
     *
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * The URI of the request object.
     *
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * The version of the http request.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    public char[] getBody() {
        return body;
    }

    /**
     * The key to value mapping in the request header fields.
     *
     * @return the header
     */
    public Map<String, String> getHeader() {
        // Lets return the unmodifable view of the header map
        return Collections.unmodifiableMap(header);
    }

    /**
     * Reads raw data from the supplied input stream and constructs a
     * <tt>HttpRequest</tt> object out of the raw data.
     *
     * @param inputStream The input stream to read from.
     * @return A <tt>HttpRequest</tt> object.
     * @throws Exception Throws either {@link ProtocolException} for bad request or
     *                   {@link IOException} for socket input stream read errors.
     */
    public static HttpRequest read(InputStream inputStream) throws Exception {
        // We will fill this object with the data from input stream and return it
        HttpRequest request = new HttpRequest();

        InputStreamReader inStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inStreamReader);

        //First Request Line: GET /somedir/page.html HTTP/1.1
        String line = reader.readLine(); // A line ends with either a \r, or a \n, or both


        int BAD_REQUEST_CODE = 400;
        String BAD_REQUEST_TEXT = "Bad Request";

        if (line == null) {
            throw new ProtocolException(BAD_REQUEST_CODE, BAD_REQUEST_TEXT);
        }

        // We will break this line using space as delimeter into three parts
        StringTokenizer tokenizer = new StringTokenizer(line, " ");

        // Error checking the first line must have exactly three elements
        if (tokenizer.countTokens() != 3) {
            throw new ProtocolException(BAD_REQUEST_CODE, BAD_REQUEST_TEXT);
        }

        request.method = tokenizer.nextToken();        // GET
        request.uri = tokenizer.nextToken();        // /somedir/page.html
        request.version = tokenizer.nextToken();    // HTTP/1.1

        // Rest of the request is a header that maps keys to values
        // e.g. Host: www.rose-hulman.edu
        // We will convert both the strings to lower case to be able to search later
        line = reader.readLine();
        // Chandan's readLine().trim() was blowing up
        if (line == null) {
            line = "";
        }

        while (!line.equals("")) {
            // First lets trim the line to remove escape characters
            line = line.trim();

            // Now, get index of the first occurrence of space
            int index = line.indexOf(' ');

            if (index > 0 && index < line.length() - 1) {
                // Now lets break the string in two parts
                String key = line.substring(0, index); // Get first part, e.g. "Host:"
                String value = line.substring(index + 1); // Get the rest, e.g. "www.rose-hulman.edu"

                // Lets strip off the white spaces from key if any and change it to lower case
                key = key.trim();

                // Lets also remove ":" from the key
                key = key.substring(0, key.length() - 1);

                // Lets strip white spaces if any from value as well
                value = value.trim();

                // Now lets put the key=>value mapping to the header map
                request.header.put(key, value);
            }

            // Processed one more line, now lets read another header line and loop
            line = reader.readLine();
            // Chandan's readLine().trim() was blowing up
            if (line == null) {
                line = "";
            }
        }

        int contentLength = 0;
        String contentEncoding = null;
        try {
            contentLength = Integer.parseInt(request.header
                    .get(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH)));
            // check to see if Content-Encoding is gzip
            contentEncoding = request.header.get(Protocol.getProtocol().getStringRep(Keywords.CONTENT_ENCODING));
        } catch (Exception e) { // Expected behavior, dont log }

        if (contentLength > 0) {
            // read in the body, gzip or plain text
            request.body = new char[contentLength];
            reader.read(request.body);

            // should we decompress?
            if (contentEncoding != null && (contentEncoding.equals("gzip") || contentEncoding.equals("application/gzip"))) {
                String theBody = new String(request.body);
                char[] decompressedBody = GZipUtils.decompressString(theBody);

                // update the content length
                request.header.put(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH), decompressedBody.length + "");
                request.body = new char[decompressedBody.length];
                request.body = decompressedBody;
            }
        }

        return request;
    }

    public void write(OutputStream outputStream) throws Exception {
        BufferedOutputStream bos = new BufferedOutputStream(outputStream);

        // Request line
        String requestLine = this.method + Protocol.getProtocol().getStringRep(Keywords.SPACE)
                + this.uri + Protocol.getProtocol().getStringRep(Keywords.SPACE) + this.version +
                Protocol.getProtocol().getStringRep(Keywords.CR) +
                Protocol.getProtocol().getStringRep(Keywords.LF);
        bos.write(requestLine.getBytes());

        // Headers
        if (header != null && !header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // Write each header field line
                String currentHeaderLine = key + Protocol.getProtocol().getStringRep(Keywords.SEPARATOR) +
                        Protocol.getProtocol().getStringRep(Keywords.SPACE) + value +
                        Protocol.getProtocol().getStringRep(Keywords.CR) +
                        Protocol.getProtocol().getStringRep(Keywords.LF);
                bos.write(currentHeaderLine.getBytes());
            }
        }

        // Write a blank line
        bos.write(("" + Protocol.getProtocol().getStringRep(Keywords.CR) +
                Protocol.getProtocol().getStringRep(Keywords.LF)).getBytes());

        // Body
        byte[] bodyByteArray = new String(this.getBody()).getBytes();
        if (bodyByteArray != null) {
            bos.write(bodyByteArray);
        }

        // SEND IT BRUH
        bos.flush();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("----------- Header ----------------\n");
        buffer.append(this.method);
        buffer.append(Protocol.getProtocol().getStringRep(Keywords.SPACE));
        buffer.append(this.uri);
        buffer.append(Protocol.getProtocol().getStringRep(Keywords.SPACE));
        buffer.append(this.version);
        buffer.append(Protocol.getProtocol().getStringRep(Keywords.LF));

        for (Map.Entry<String, String> entry : this.header.entrySet()) {
            buffer.append(entry.getKey());
            buffer.append(Protocol.getProtocol().getStringRep(Keywords.SEPARATOR));
            buffer.append(Protocol.getProtocol().getStringRep(Keywords.SPACE));
            buffer.append(entry.getValue());
            buffer.append(Protocol.getProtocol().getStringRep(Keywords.LF));
        }
        buffer.append("------------- Body ---------------\n");
        buffer.append(this.body);
        buffer.append("----------------------------------\n");
        return buffer.toString();
    }
}
