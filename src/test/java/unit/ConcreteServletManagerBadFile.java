package unit;

import servlet.AServletManager;
import java.io.InputStream;

/**
 * Created by TrottaSN on 2/2/2017.
 *
 */
public class ConcreteServletManagerBadFile extends AServletManager {

    public ConcreteServletManagerBadFile(String filePath, InputStream configStream) {
        super(filePath, configStream);
    }

    @Override
    public void init() {
        this.configStream = null;
    }

    @Override
    public void destroy() {

    }
}
