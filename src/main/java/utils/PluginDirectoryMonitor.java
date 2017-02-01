package utils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.logging.log4j.Level;

import protocol.ProtocolConfiguration;
import server.IDirectoryListener;
import server.Server;
import servlet.AServletManager;

public class PluginDirectoryMonitor implements Runnable {
	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private Map<String, String> jarPathToContextRoot;
	private IDirectoryListener listener;

	public static void main(String[] args) throws IOException {
		new PluginDirectoryMonitor(Paths.get("./web"), new Server(100, new ProtocolConfiguration())).processEvents();
	}

	public PluginDirectoryMonitor(Path dir, IDirectoryListener listener) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.jarPathToContextRoot = new HashMap<String, String>();
		this.listener = listener;
		
		register(dir);
	}

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>)event;
	}
	
    /**
     * Register the given directory with the WatchService
     * Taken from tutorial...
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        keys.put(key, dir);
    }

    /**
     * Read the jar and load its entry class
     * Custom code
     */
    private void handleJarUpserted(String pathToJar) {
    	String entryPointClassName = "";
    	JarFile jar = null;
    	try {
    		jar = new JarFile(pathToJar);
    		Manifest manifest = null;
    		Enumeration<JarEntry> e = jar.entries();
    		URL[] urls = { new URL("jar:file:" + pathToJar + "!/") };
    		URLClassLoader cl = URLClassLoader.newInstance(urls);

    		// just load the manifest file
    		while (e.hasMoreElements()) {
    			JarEntry je = e.nextElement();
				if (je.getName().endsWith(".MF")) {
					manifest = new Manifest(jar.getInputStream(je));
					entryPointClassName = initializeManifestValues(manifest, pathToJar);
					entryPointClassName = entryPointClassName.replace('/', '.');
					break;
				}    			
    		}
  
    		// if the manifest file succeeded
    		if (!entryPointClassName.equals("")) {
    			Class<?> c = cl.loadClass(entryPointClassName);
    			Constructor<?> constructor = c.getConstructor(String.class);
    			String pluginPathDirectory = "/home/csse/" + this.jarPathToContextRoot.get(pathToJar);
    			Object result = constructor.newInstance(pluginPathDirectory);

    			// TODO: create this directory on the VM
    			if (result instanceof AServletManager) {
    				SwsLogger.accessLogger.log(Level.INFO, "Successfully loaded plugin jar: " + pathToJar);
    				AServletManager manager = (AServletManager) result;
    				this.listener.addPlugin(this.jarPathToContextRoot.get(pathToJar), manager);
    			} else {
    				SwsLogger.errorLogger.log(Level.INFO, "Error loading jar file's entry point class. Not AServletManager instance " + pathToJar);
    			}
    		} else {
    			SwsLogger.errorLogger.log(Level.INFO, "Error loading jar file's entry point class name " + pathToJar);
    		}

    	} catch (Exception e) {
    		SwsLogger.errorLogger.log(Level.INFO, "Error loading jar file " + pathToJar);
    	} finally {
    		try {
    			jar.close();
    		} catch (Exception er) {
    			SwsLogger.errorLogger.log(Level.INFO, "Error closing jar file " + pathToJar);
    		}
    	}
    }

    /**
     * Custom code.
     * Read the manifest file and set the context root.
     * Return the entry point class to the plugin.
     * @param manifest
     * @param jeName
     * @return entryPointClassName
     */
	private String initializeManifestValues(Manifest manifest, String jarPath) {
		final String MANIFEST_CONTEXT_ROOT = "Context-Root";
		final String MANIFEST_ENTRY_POINT = "Entry-Point";
		String contextRoot = "";
		String entryPoint = "";
		if (manifest == null) {
			SwsLogger.errorLogger.log(Level.INFO, "manifest file is invalid: " + jarPath);
			return "";
		}

		Attributes attributes = manifest.getMainAttributes();
		for (Object key : attributes.keySet()) {
			if (key.toString().equals(MANIFEST_CONTEXT_ROOT)) {
				contextRoot = attributes.getValue(key.toString());
			} else if (key.toString().equals(MANIFEST_ENTRY_POINT)) {
				entryPoint = attributes.getValue(key.toString());
			}
		}
		
		if (contextRoot.equals("") || entryPoint.equals("")) {
			SwsLogger.errorLogger.log(Level.INFO, "manifest file does not contain contextRoot or entryPoint: " + jarPath);
			return "";
		} else {
			this.jarPathToContextRoot.put(jarPath, contextRoot);
			return entryPoint;
		}
	}

    /**
     * Process all events for keys queued to the watcher
     * This was from the tutorial...
     */
    private void processEvents() {
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // handle event
            	if (!child.endsWith(".jar")) {
            		SwsLogger.accessLogger.log(Level.INFO, "Plugin directory didn't process: " + child + " file");
            	} else if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE || event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                	try {
                		handleJarUpserted(child.toString());
                	} catch (Exception e) {
                		
                	}
                } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
//                	handleJarDeleted(child.toString());
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

	@Override
	public void run() {
		this.processEvents();
	}
}
