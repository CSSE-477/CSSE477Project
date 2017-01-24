package utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by TrottaSN on 1/19/2017.
 */
public class FileCreationUtility {

    private static final String RESOURCE_DIR = "." + File.separator + "resource.files";
    private static final String DEFAULT_RESOURCE_FILE_NAME = "resource";
    private static final String EXTENSION_SEP = ".";
    private static final String ENCODING = "UTF-8";
    public enum Extension { html }

    public static boolean createResourceFile() {
        Extension extension = Extension.html;
        File dir = new File(RESOURCE_DIR);
        if(!dir.exists()){
            dir.mkdir();
        }
        File testFile = new File(createFilePath(DEFAULT_RESOURCE_FILE_NAME, extension));
        if(testFile.exists()) {
            testFile.delete();
        }
        try {
            testFile.createNewFile();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /*
    public static boolean removeResourceFile() {
        String resourceName = DEFAULT_RESOURCE_FILE_NAME;
        Extension extension = Extension.html;
        File f = new File(createURI(resourceName, extension));
        if(f.exists()) {
            f.delete();
        }
        return true;
    }
    */

    public static boolean writeToTestFile(String toWrite) {
        Extension extension = Extension.html;
        try{
            PrintWriter writer = new PrintWriter(createFilePath(DEFAULT_RESOURCE_FILE_NAME, extension), ENCODING);
            writer.println(toWrite);
            writer.close();
        } catch (IOException e) {
            return false;
        }
        return true;
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

    private static String createFilePath(String resourceName, Extension extension) {
        return RESOURCE_DIR.concat(File.separator).concat(resourceName).concat(EXTENSION_SEP).concat(extension.toString());
    }
}
