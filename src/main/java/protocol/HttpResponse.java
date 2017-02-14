/*
 * HttpResponse.java
 * Oct 7, 2012
 *
 * Simple Web Server (SWS) for CSSE 477
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 */
 
package protocol;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import utils.SwsLogger;

/**
 * Represents a response object for HTTP.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class HttpResponse {

    private String version;
	private int status;
	private String phrase;
	private Map<String, String> header;
	private File file;
	private String body;
	
	/**
	 * Constructs a HttpResponse object using supplied parameter
	 * 
	 * @param version The http version.
	 * @param status The response status.
	 * @param phrase The response status phrase.
	 * @param header The header field map.
	 * @param file The file to be sent.
	 */
	public HttpResponse(String version, int status, String phrase, Map<String, String> header, File file, String body) {
		this.version = version;
		this.status = status;
		this.phrase = phrase;
		this.header = header;
		this.file = file;
		this.body = body;
	}

	/**
	 * Gets the version of the HTTP.
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the status code of the response object.
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Gets the status phrase of the response object.
	 * 
	 * @return the phrase
	 */
	public String getPhrase() {
		return phrase;
	}
	
	/**
	 * The file to be sent.
	 * 
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * The body to be sent
	 * 
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Returns the header fields associated with the response object.
	 * @return the header
	 */
	public Map<String, String> getHeader() {
		// Lets return the unmodifable view of the header map
		return Collections.unmodifiableMap(header);
	}

	/**
	 * Writes the data of the http response object to the output stream.
	 * 
	 * @param outStream The output stream
	 * @throws Exception
	 */
	public void write(OutputStream outStream) throws Exception {
        BufferedOutputStream out = new BufferedOutputStream(outStream);

        // Check to see if/what the body is & set the headers
        byte[] bodyBytes = null;
        String bodyToWrite = this.getBodyToWrite();
        if (bodyToWrite != null) {
        	// we have a body, let's check if we should gzip it
    		String contentEncoding = header.get(Protocol.getProtocol().getStringRep(Keywords.CONTENT_ENCODING));
    		// is the content encoding set and gzip?
    		if (contentEncoding != null && (contentEncoding.equals("gzip") || contentEncoding.equals("application/gzip"))) {
    			bodyBytes = compressBody(bodyToWrite);
    		} else {
    			// content encoding is something besides gzip, oh well we only support plain text or gzip for now
    			bodyBytes = bodyToWrite.getBytes();
    		}
        }

		// First status line
		String line = this.version + Protocol.getProtocol().getStringRep(Keywords.SPACE) +
				this.status + Protocol.getProtocol().getStringRep(Keywords.SPACE) + this.phrase +
				Protocol.getProtocol().getStringRep(Keywords.CR) +
				Protocol.getProtocol().getStringRep(Keywords.LF);
		out.write(line.getBytes());
		
		// Write header fields if there is something to write in header field
		if(header != null && !header.isEmpty()) {
			for(Map.Entry<String, String> entry : header.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();

				// Write each header field line
				line = key + Protocol.getProtocol().getStringRep(Keywords.SEPARATOR) +
						Protocol.getProtocol().getStringRep(Keywords.SPACE) + value +
						Protocol.getProtocol().getStringRep(Keywords.CR) +
						Protocol.getProtocol().getStringRep(Keywords.LF);
				out.write(line.getBytes());
			}
		}

		// Write a blank line
		out.write(("" + Protocol.getProtocol().getStringRep(Keywords.CR) +
				Protocol.getProtocol().getStringRep(Keywords.LF)).getBytes());

		// Write body *as gzip* if necessary
		if (bodyBytes != null) {
			out.write(bodyBytes);
		}
		// Flush the data so that outStream sends everything through the socket 
		out.flush();
	}

	/**
	 * Determines whether we are sending file or body text
	 * @return String if file or body, null if no body is to be sent
	 * @throws IOException 
	 */
	private String getBodyToWrite() throws IOException {
		String bodyToWrite = null;
		int OK_CODE = 200;
		if (this.getStatus() == OK_CODE && this.file != null && this.file.exists()) {
			bodyToWrite = new String(Files.readAllBytes(Paths.get(this.file.getAbsolutePath())));
		} else if (this.getStatus() == OK_CODE && this.body != null) {
			bodyToWrite = this.body;
		}

		return bodyToWrite;
	}

	/**
	 * Compress the body to gzip format
	 * @param the body as a string
	 * @return the body as a gzip byte[]
	 */
	private byte[] compressBody(String str) {
		// check to see if header map exists, if not return null since body cannot be processed
		if (header == null) {
			return null;
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(str.getBytes());
			gzip.close();
			bos.close();
		} catch (IOException e) {
			SwsLogger.errorLogger.error("Error writing body as gzip.", e);
		}

		byte[] compressed = bos.toByteArray();
		// update headers that are relevant
//		this.header.put(Protocol.getProtocol().getStringRep(Keywords.CONTENT_ENCODING), "gzip");
		this.header.put(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH), compressed.length + "");
		return compressed;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("----------------------------------\n");
		buffer.append(this.version);
		buffer.append(Protocol.getProtocol().getStringRep(Keywords.SPACE));
		buffer.append(this.status);
		buffer.append(Protocol.getProtocol().getStringRep(Keywords.SPACE));
		buffer.append(this.phrase);
		buffer.append(Protocol.getProtocol().getStringRep(Keywords.LF));
		
		for(Map.Entry<String, String> entry : this.header.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append(Protocol.getProtocol().getStringRep(Keywords.SEPARATOR));
			buffer.append(Protocol.getProtocol().getStringRep(Keywords.SPACE));
			buffer.append(entry.getValue());
			buffer.append(Protocol.getProtocol().getStringRep(Keywords.LF));
		}
		
		buffer.append(Protocol.getProtocol().getStringRep(Keywords.LF));
		if(file != null) {
			buffer.append("Data: ");
			buffer.append(this.file.getAbsolutePath());
		}
		buffer.append("\n----------------------------------\n");
		return buffer.toString();
	}
	
}
