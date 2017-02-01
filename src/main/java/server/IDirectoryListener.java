package server;

import servlet.AServletManager;

public interface IDirectoryListener {
	/**
	 * Add a new plugin that was dropped 
	 * @param contextRoot
	 * @param manager
	 */
	public void addPlugin(String contextRoot, AServletManager manager);
	
	/**
	 * Remove plugin after being deleted from the directory
	 * @param contextRoot
	 */
	public void removePlugin(String contextRoot);
}
