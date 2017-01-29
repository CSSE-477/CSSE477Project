package handlers;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.ProtocolConfiguration;
import utils.SwsLogger;

/**
 * Created by Trowbrct on 1/25/2017.
 *
 */
public class HeadRequestHandler implements IRequestHandler {

	private String rootDirectory;
	private ProtocolConfiguration protocol;

	public HeadRequestHandler(String rootDirectory, ProtocolConfiguration protocol) {
		this.rootDirectory = rootDirectory;
		this.protocol = protocol;
	}

	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		HttpResponse response = null;
		String fileRequested = request.getUri();
		String fullPath = this.rootDirectory.concat(fileRequested);

		File file = new File(fullPath);

		if (!file.exists()) {
			response = (new HttpResponseBuilder(404, this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.CLOSE))).generateResponse();
		} else if (file.isDirectory()) {
			// check for default file before sending 400
			String location = fullPath.concat(System.getProperty("file.separator")).concat(this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.DEFAULT_FILE));
			file = new File(location);

			if (file.exists()) {
				SwsLogger.accessLogger
				.info("HEAD to file " + file.getAbsolutePath() + ". Sending 200 OK");
				response = getHeadResponseFromFile(file);
			} else {
				SwsLogger.errorLogger
				.error("HEAD to file " + file.getAbsolutePath() + ". Sending 400 Bad Request");
				response = (new HttpResponseBuilder(400, this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.CLOSE))).generateResponse();
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
		return (new HttpResponseBuilder(200, this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.CLOSE))).setFile(file).putHeader("lastModified", lastModified).putHeader("fileSize", fileSize).putHeader("fileType", fileType).generateResponse();
	}

}
