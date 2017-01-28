package handlers;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.Protocol;
import utils.SwsLogger;

/**
 * Created by Trowbrct on 1/25/2017.
 */
public class HeadRequestHandler implements IRequestHandler {

	private String rootDirectory;

	public HeadRequestHandler(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		HttpResponse response = null;
		String fileRequested = request.getUri();
		String fullPath = this.rootDirectory.concat(fileRequested);

		File file = new File(fullPath);

		if (!file.exists()) {
			response = (new HttpResponseBuilder(404, Protocol.CLOSE)).generateResponse();
		} else if (file.isDirectory()) {
			// check for default file before sending 400
			String location = fullPath.concat(System.getProperty("file.separator")).concat(Protocol.DEFAULT_FILE);
			file = new File(location);

			if (file.exists()) {
				SwsLogger.accessLogger
				.info("HEAD to file " + file.getAbsolutePath() + ". Sending 200 OK");
				response = getHeadResponseFromFile(file);
			} else {
				SwsLogger.errorLogger
				.error("HEAD to file " + file.getAbsolutePath() + ". Sending 400 Bad Request");
				response = (new HttpResponseBuilder(400, Protocol.CLOSE)).generateResponse();
			}
		} else {
			// file exists; return last modified, file size, file type
			SwsLogger.accessLogger
			.info("HEAD to file " + file.getAbsolutePath() + ". Sending 200 OK");
			response = getHeadResponseFromFile(file);
		}

		return response;
	}
	
	private HttpResponse getHeadResponseFromFile(File file) {
		String lastModified = new Date(file.lastModified()).toString();
		String fileSize = String.valueOf(file.length());
		String fileType = FilenameUtils.getExtension(file.getAbsolutePath());
		return (new HttpResponseBuilder(200, Protocol.CLOSE)).setFile(file).putHeader("lastModified", lastModified).putHeader("fileSize", fileSize).putHeader("fileType", fileType).generateResponse();
	}

}
