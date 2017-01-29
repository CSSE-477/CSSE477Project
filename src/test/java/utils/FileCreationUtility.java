package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by TrottaSN on 1/19/2017.
 */
public class FileCreationUtility {

    private static final String RESOURCE_DIR = "." + File.separator + "src" + File.separator + "test" + File.separator + "resource";
    private static final String EXTENSION_SEP = ".";
    public enum Extension { html, txt }

    public static boolean createResourceFile(String fileName, Extension extension) {
        File dir = new File(RESOURCE_DIR);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File testFile = new File(createFilePath(fileName, extension));
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

    public static boolean removeResourceFile(String fileName, Extension extension) {
        File f = new File(createFilePath(fileName, extension));
        if (f.exists()) {
            f.delete();
        }
        return true;
    }

    public static boolean writeToTestFile(String toWrite, boolean overwrite, String fileName, Extension extension) {
        try{
            FileWriter writer = new FileWriter(createFilePath(fileName, extension), overwrite);
            writer.write(toWrite, 0, toWrite.length());
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

    public static File retrieveTestFile(String fileName, Extension extension) {
        File testFile = new File(createFilePath(fileName, extension));
        if(!testFile.exists() || !testFile.isFile()){
            return null;
        }
        return testFile;
    }

    private static String createFilePath(String resourceName, Extension extension) {
        return RESOURCE_DIR.concat(File.separator).concat(resourceName).concat(EXTENSION_SEP).concat(extension.toString());
    }
}
