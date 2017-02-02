package unit;

import servlet.AServletManager;
import java.io.InputStream;
import java.net.URLClassLoader;

/**
 * Created by TrottaSN on 2/2/2017.
 *
 */
public class ConcreteServletManagerBadFile extends AServletManager {

    public ConcreteServletManagerBadFile(String filePath, URLClassLoader classLoader) {
        super(filePath, classLoader);
    }

    @Override
    public void init() {
        // this.configStream = null;
    }

    @Override
    public void destroy() {

    }
}
