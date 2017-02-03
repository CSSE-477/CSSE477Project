package unit;

import org.junit.Before;
import org.junit.Test;

import protocol.HttpRequest;
import protocol.HttpResponseBuilder;
import servlet.AHttpServlet;
import servlet.AServletManager;
import utils.FileCreationUtility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
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
        // URL[] urls = { new URL("jar:file:" + pathToJar + "!/") };
        // URLClassLoader cl = URLClassLoader.newInstance(urls);
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
        concreteManager = new ConcreteServletManager("fakeFilePath", null);
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
        concreteManager = new ConcreteServletManager("fakeFilePath", null);
        Field f = concreteManager.getClass().getSuperclass().getDeclaredField("servletMap");
        f.setAccessible(true);
        assertTrue(concreteManager.isValid());
        assertTrue(((HashMap<String, AHttpServlet>) f.get(concreteManager)).isEmpty());
    }

    @Test
    public void testParseNotFoundConfigFile() throws Exception {
        if(this.testFile != null && this.testFile.exists()){
            this.testFile.delete();
        }
        this.testFile.createNewFile();
        concreteManager = new ConcreteServletManagerBadFile("fakeFilePath", null);
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
        concreteManager = new ConcreteServletManager("fakeFilePath", null);
        Field f = concreteManager.getClass().getSuperclass().getDeclaredField("servletMap");
        f.setAccessible(true);
        assertFalse(concreteManager.isValid());
        assertTrue(((HashMap<String, AHttpServlet>) f.get(concreteManager)).isEmpty());
    }

    public class ConcreteServletManager extends AServletManager {

        public ConcreteServletManager(String filePath, URLClassLoader classLoader) {
            super(filePath, classLoader);
        }

        @Override
        public void init() {

        }

        @Override
        public void destroy() {

        }
    }

    public class ConcreteServletManagerBadFile extends AServletManager {

        public ConcreteServletManagerBadFile(String filePath, URLClassLoader classLoader) {
            super(filePath, classLoader);
        }

        @Override
        public void init() {

        }

        @Override
        public void destroy() {

        }
    }

    public class ServletMock extends AHttpServlet {

        public ServletMock(String resourcePath) {
            super(resourcePath);
        }

        @Override
        public void init() {

        }

        @Override
        public void destroy() {

        }

        @Override
        public void doGet(HttpRequest request, HttpResponseBuilder responseBuilder) {

        }

        @Override
        public void doHead(HttpRequest request, HttpResponseBuilder responseBuilder) {

        }

        @Override
        public void doPost(HttpRequest request, HttpResponseBuilder responseBuilder) {

        }

        @Override
        public void doPut(HttpRequest request, HttpResponseBuilder responseBuilder) {

        }

        @Override
        public void doDelete(HttpRequest request, HttpResponseBuilder responseBuilder) {

        }
    }
}