package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by TrottaSN on 1/19/2017.
 *
 */
public class FileCreationUtility {

    public static final String RESOURCE_DIR = "." + File.separator + "src" + File.separator + "test" + File.separator + "resource";
    public static final String EXTENSION_SEP = ".";

    public static boolean createResourceDir(){
        File dir = new File(RESOURCE_DIR);
        if(dir.exists()){
            reclaimResourceDirectory();
        }
        return dir.mkdirs();
    }

    public static boolean reclaimResourceDirectory()
    {
        File dir = new File(RESOURCE_DIR);
        return reclaimHelper(dir);
    }

    private static boolean reclaimHelper(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = reclaimHelper(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
