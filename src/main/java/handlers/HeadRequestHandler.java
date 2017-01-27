package handlers;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
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
			SwsLogger.errorLogger
			.error("HEAD to file " + file.getAbsolutePath() + ". Sending 404 Not Found");
			response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
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
				response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);	
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
		HttpResponse response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);

		String lastModified = new Date(file.lastModified()).toString();
		response.put("lastModified", lastModified);
		
		String fileSize = String.valueOf(file.length());
		response.put("fileSize", fileSize);
		
		String fileType = FilenameUtils.getExtension(file.getAbsolutePath());
		response.put("fileType", fileType);
		
		return response;
	}

}
