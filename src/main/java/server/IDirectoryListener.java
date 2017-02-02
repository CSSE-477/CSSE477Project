package server;

import servlet.AServletManager;

public interface IDirectoryListener {
	/**
	 * Add a new plugin that was dropped 
	 * @param contextRoot
	 * @param manager
	 */
	void addPlugin(String contextRoot, AServletManager manager);
	
	/**
	 * Remove plugin after being deleted from the directory
	 * @param contextRoot
	 */
	void removePlugin(String contextRoot);
}
