package unit;

import servlet.AServletManager;
import utils.FileCreationUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLClassLoader;

/**
 * Created by TrottaSN on 2/2/2017.
 *
 */
public class ConcreteServletManager extends AServletManager {

    public ConcreteServletManager(String filePath, URLClassLoader classLoader) {
        super(filePath, classLoader);
    }

    @Override
    public void init() {
        /*
        File initialFile =  new File(FileCreationUtility.RESOURCE_DIR, "testFile.csv");
        try {
            this.classLoader = new FileInputStream(initialFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public void destroy() {

    }
}
