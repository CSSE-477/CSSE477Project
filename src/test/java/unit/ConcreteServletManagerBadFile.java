package unit;

import servlet.AServletManager;
import utils.FileCreationUtility;

import java.io.File;

/**
 * Created by TrottaSN on 2/2/2017.
 *
 */
public class ConcreteServletManagerBadFile extends AServletManager {

    public ConcreteServletManagerBadFile(String filePath) {
        super(filePath);
    }

    @Override
    public void init() {
        this.configFile = new File(FileCreationUtility.RESOURCE_DIR, "NOTEVENVALIDWHATAREYOUDOING");
    }

    @Override
    public void destroy() {

    }
}
