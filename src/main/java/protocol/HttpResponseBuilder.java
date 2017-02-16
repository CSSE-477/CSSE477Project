package protocol;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import utils.SwsLogger;

/**
 * Created by TrottaSN on 1/27/2017.
 *
 */
public class HttpResponseBuilder {

	private static final int DEFAULT_STATUS_CODE = 500;

	private String version;
	private int status;
	private String phrase;
	private Map<String, String> header;
	private File file;
	private String body;

	private HttpResponseBuilder(String version, int status, String phrase, Map<String, String> header, File file,
			String body) {

		if (version == null) {
			this.version = Protocol.getProtocol().getStringRep(Keywords.VERSION);
		} else {
			this.version = version;
		}
		if (status == -1) {
			this.status = DEFAULT_STATUS_CODE;
		} else {
			this.status = status;
		}
		if (phrase == null) {
			this.phrase = Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(this.status));
		} else {
			this.phrase = phrase;
		}
		if (header == null) {
			this.header = new HashMap<>();
			this.header.put(Protocol.getProtocol().getStringRep(Keywords.CONNECTION),
					Protocol.getProtocol().getStringRep(Keywords.CLOSE));

			// Lets add current date
			Date date = Calendar.getInstance().getTime();
			this.header.put(Protocol.getProtocol().getStringRep(Keywords.DATE), date.toString());

			// Lets add server info
			this.header.put(Protocol.getProtocol().getStringRep(Keywords.SERVER),
					Protocol.getProtocol().getServerInfo());

			// Lets add extra header with provider info
			this.header.put(Protocol.getProtocol().getStringRep(Keywords.PROVIDER),
					Protocol.getProtocol().getStringRep(Keywords.AUTHOR));
		} else {
			this.header = header;
		}
		this.file = file;
		this.body = body;
	}

	public HttpResponseBuilder() {
		this(null, -1, null, null, null, null);
	}

	public HttpResponseBuilder(int status) {
		this(null, status, null, null, null, null);
	}

	public HttpResponseBuilder setVersion(String version) {
		this.version = version;
		return this;
	}

	public HttpResponseBuilder setStatus(int status) {
		this.status = status;
		return this;
	}

	public HttpResponseBuilder setPhrase(String phrase) {
		this.phrase = phrase;
		return this;
	}

	public HttpResponseBuilder setHeader(Map<String, String> header) {
		this.header = header;
		return this;
	}

	public HttpResponseBuilder putHeader(String key, String value) {
		if (this.header == null) {
			this.header = new HashMap<>();
		}
		this.header.put(key, value);
		return this;
	}

	public HttpResponseBuilder setFile(File file) {
		this.file = file;
		if (this.header == null) {
			this.header = new HashMap<>();
		}
		// Lets add last modified date for the file
		long timeSinceEpoch = file.lastModified();
		Date modifiedTime = new Date(timeSinceEpoch);
		this.header.put(Protocol.getProtocol().getStringRep(Keywords.LAST_MODIFIED), modifiedTime.toString());

		// Lets get content length in bytes
		long length = file.length();
		this.header.put(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH), length + "");

		// Lets get MIME type for the file
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String mime = fileNameMap.getContentTypeFor(file.getName());
		// The fileNameMap cannot find mime type for all of the documents, e.g.
		// doc, odt, etc.
		// So we will not add this field if we cannot figure out what a mime
		// type is for the file.
		// Let browser do this job by itself.
		if (mime != null) {
			this.header.put(Protocol.getProtocol().getStringRep(Keywords.CONTENT_TYPE), mime);
		}

		// Let's add the file's Checksum
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			FileInputStream fis = new FileInputStream(file.getPath());

			byte[] buffer = new byte[(int) file.length()]; //Wont work for files over 4 gigs
			int count;
			BufferedInputStream bis = new BufferedInputStream(fis);
			while ((count = bis.read(buffer)) > 0) {
				md.update(buffer, 0, count);
			}
			byte[] hash = md.digest();
			String hashString = DatatypeConverter.printHexBinary(hash);

			this.putHeader("Checksum", hashString);
			fis.close();
		} catch (NoSuchAlgorithmException e) {
			// Shouldn't ever happen
			SwsLogger.errorLogger.error("Unable to find SHA-256 hashing algorighm.", e);
		} catch (FileNotFoundException e) {
			SwsLogger.errorLogger.error("Unable to find file to hash.", e);
		} catch (IOException e) {
			SwsLogger.errorLogger.error("Unable to close FileInputStream while hasing file.", e);
		}

		return this;
	}

	public HttpResponseBuilder setBody(String body) {
		this.body = body;
		// Lets get content length in bytes
		long length = body.length();
		this.header.put(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH), length + "");
		

		// Generate Checksum and add in header
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(body.getBytes());
			String hashString = DatatypeConverter.printHexBinary(hash);

			this.putHeader("Checksum", hashString);
		} catch (NoSuchAlgorithmException e) {
			SwsLogger.errorLogger.error("Unable to find SHA-256 hashing algorighm.");
		}
		return this;
	}

	public HttpResponse generateResponse() {
		return new HttpResponse(this.version, this.status, this.phrase, this.header, this.file, this.body);
	}
}
