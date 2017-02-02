package unit;

import org.junit.Before;
import org.junit.Test;

import servlet.AHttpServlet;
import servlet.AServletManager;
import utils.FileCreationUtility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AServletManagerTest {

    private File testFile;
    private AServletManager concreteManager;

    @Before
    public void setUp() {
        this.testFile = new File(FileCreationUtility.RESOURCE_DIR, "testFile.csv");
        if(testFile.exists()){
            testFile.delete();
        }
        try {
            testFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fileWriter = null;
        try{
            fileWriter = new FileWriter(testFile);
            fileWriter.append("/User");
            fileWriter.append(",");
            fileWriter.append(ServletMock.class.getCanonicalName());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fileWriter != null;
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testParseConfig() throws Exception {
        concreteManager = new ConcreteServletManager("fakeFilePath");
        Field f = concreteManager.getClass().getSuperclass().getDeclaredField("servletMap");
        f.setAccessible(true);
        HashMap<String, AHttpServlet> servletMap = (HashMap<String, AHttpServlet>) f.get(concreteManager);
        assertTrue(concreteManager.isValid());
        assertEquals(ServletMock.class, servletMap.get("User").getClass());
    }

    @Test
    public void testParseEmptyConfig() throws Exception {
        if(this.testFile != null && this.testFile.exists()){
            this.testFile.delete();
        }
        this.testFile.createNewFile();
        concreteManager = new ConcreteServletManager("fakeFilePath");
        Field f = concreteManager.getClass().getSuperclass().getDeclaredField("servletMap");
        f.setAccessible(true);
        assertTrue(concreteManager.isValid());
        assertTrue(((HashMap<String, AHttpServlet>) f.get(concreteManager)).isEmpty());
    }

    @Test
    public void testParseNullConfigFile() throws Exception {
        if(this.testFile != null && this.testFile.exists()){
            this.testFile.delete();
        }
        this.testFile.createNewFile();
        concreteManager = new ConcreteServletManager("fakeFilePath");
        Field f = concreteManager.getClass().getSuperclass().getDeclaredField("servletMap");
        f.setAccessible(true);
        assertFalse(concreteManager.isValid());
        assertTrue(((HashMap<String, AHttpServlet>) f.get(concreteManager)).isEmpty());
    }

    @Test
    public void testParsePartialConfigWithCommaSeparation() throws Exception {
        if(this.testFile != null && this.testFile.exists()){
            this.testFile.delete();
        }
        this.testFile.createNewFile();
        FileWriter fileWriter = null;
        try{
            fileWriter = new FileWriter(testFile);
            fileWriter.append("/User");
            fileWriter.append(",");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fileWriter != null;
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        concreteManager = new ConcreteServletManager("fakeFilePath");
        Field f = concreteManager.getClass().getSuperclass().getDeclaredField("servletMap");
        f.setAccessible(true);
        assertFalse(concreteManager.isValid());
        assertTrue(((HashMap<String, AHttpServlet>) f.get(concreteManager)).isEmpty());
    }
}