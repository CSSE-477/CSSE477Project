package handlers;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

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
			response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		} else if (file.isDirectory()) {
			// check for default file before sending 400
			String location = fullPath.concat(System.getProperty("file.separator")).concat(Protocol.DEFAULT_FILE);
			file = new File(location);

			if (file.exists()) {
				response = getHeadResponseFromFile(file);
			} else {
				response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);	
			}
		} else {
			// file exists; return last modified, file size, file type
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
