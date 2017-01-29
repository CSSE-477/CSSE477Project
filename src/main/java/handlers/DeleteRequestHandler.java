/*
 * DeleteRequestHandler.java
 * Jan 25, 2017
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
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
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */

package handlers;

import java.io.File;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.ProtocolConfiguration;

/**
 * 
 * @author Jesse Shellabarger
 */
public class DeleteRequestHandler implements IRequestHandler {

	private String rootDirectory;
	private ProtocolConfiguration protocol;

	public DeleteRequestHandler(String rootDirectory, ProtocolConfiguration protocol) {
		this.rootDirectory = rootDirectory;
		this.protocol = protocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see handlers.IRequestHandler#handleRequest(protocol.HttpRequest)
	 */
	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		String uri = request.getUri();
		File file = new File(this.rootDirectory + uri);
		if (file.exists()) {
			file.delete();
			return (new HttpResponseBuilder(200, this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.CLOSE))).setFile(file).generateResponse();
		}
		return (new HttpResponseBuilder(404, this.protocol.getServerInfo(ProtocolConfiguration.ServerInfoFields.CLOSE))).generateResponse();
	}

}
