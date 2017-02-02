package unit;

import servlet.AServletManager;
import utils.FileCreationUtility;

import java.io.File;

/**
 * Created by TrottaSN on 2/2/2017.
 *
 */
public class ConcreteServletManager extends AServletManager {

    public ConcreteServletManager(String filePath) {
        super(filePath);
    }

    @Override
    public void init() {
        this.configFile = new File(FileCreationUtility.RESOURCE_DIR, "testFile.csv");
    }

    @Override
    public void destroy() {

    }
}
