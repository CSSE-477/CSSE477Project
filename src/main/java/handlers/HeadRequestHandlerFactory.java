package handlers;

import org.apache.commons.io.FilenameUtils;
import protocol.*;
import utils.SwsLogger;

import java.io.File;
import java.util.Date;

/**
 * Created by Trowbrct on 1/25/2017.
 *
 */
public class HeadRequestHandlerFactory implements IRequestHandlerFactory {

	private String rootDirectory;

	public HeadRequestHandlerFactory(String rootDirectory){
		this.rootDirectory = rootDirectory;
	}
	@Override
	public IRequestHandler getRequestHandler() {
		return new HeadRequestHandler(this.rootDirectory);
	}

	/**
	 * Created by Trowbrct on 1/25/2017.
	 *
	 */
	public class HeadRequestHandler implements IRequestHandler {

		private String rootDirectory;

		HeadRequestHandler(String rootDirectory) {
			this.rootDirectory = rootDirectory;
		}

		@Override
		public HttpResponse handleRequest(HttpRequest request) {
			HttpResponse response = null;
			String fileRequested = request.getUri();
			String fullPath = this.rootDirectory.concat(fileRequested);

			File file = new File(fullPath);

			if (!file.exists()) {
				response = (new HttpResponseBuilder(404)).generateResponse();
			} else if (file.isDirectory()) {
				// check for default file before sending 400
				String location = fullPath.concat(System.getProperty("file.separator"))
						.concat(Protocol.getProtocol().getStringRep(Keywords.DEFAULT_FILE));
				file = new File(location);

				if (file.exists()) {
					SwsLogger.accessLogger
							.info("HEAD to file " + file.getAbsolutePath() + ". Sending 200 OK");
					response = getHeadResponseFromFile(file);
				} else {
					SwsLogger.errorLogger
							.error("HEAD to file " + file.getAbsolutePath() + ". Sending 400 Bad Request");
					response = (new HttpResponseBuilder(400)).generateResponse();
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
			return (new HttpResponseBuilder(200)).setFile(file)
					.putHeader("lastModified", lastModified).putHeader("fileSize", fileSize)
					.putHeader("fileType", fileType).generateResponse();
		}

	}
}
