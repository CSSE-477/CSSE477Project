package integration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by TrottaSN on 1/19/2017.
 */
public class FileCreationUtility {

    public static final String TEST_PATH = "." + File.separator + "integration" + File.separator + "test_json.json";
    public static final String ENCODING = "UTF-8";

    public static boolean createTestFile() {
        File f = new File(TEST_PATH);
        if(!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    public static boolean removeTestFile() {
        File f = new File(TEST_PATH);
        if(f.exists()){
            f.getParentFile().delete();
        }
        return true;
    }

    public static boolean writeToTestFile(String toWrite) {
        try{
            PrintWriter writer = new PrintWriter(TEST_PATH, ENCODING);
            writer.println(toWrite);
            writer.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
