package unit;

import org.junit.Before;
import org.junit.Test;

import protocol.*;
import servlet.AHttpServlet;
import servlet.AServletManager;
import utils.FileCreationUtility;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AServletManagerTest {

    private File testFile;
    private AServletManager concreteManager;

    @Before
    public void setUp() {
        this.testFile = new File(FileCreationUtility.RESOURCE_DIR, "config.csv");
    }

    @Test
    public void testParseConfig() throws Exception {
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
        concreteManager = new ConcreteServletManager("fakeFilePath", new CustomClassLoader(null));
        Field f = concreteManager.getClass().getSuperclass().getDeclaredField("servletMap");
        f.setAccessible(true);
        HashMap<String, AHttpServlet> servletMap = (HashMap<String, AHttpServlet>) f.get(concreteManager);
        assertTrue(concreteManager.isValid());
        assertEquals(ServletMock.class, servletMap.get("User").getClass());
    }

    @Test
    public void testParseEmptyConfig() throws Exception {
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
            //Nothing Written
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
        if(this.testFile != null && this.testFile.exists()){
            this.testFile.delete();
        }
        this.testFile.createNewFile();
        concreteManager = new ConcreteServletManager("fakeFilePath", new CustomClassLoader(null));
        Field f = concreteManager.getClass().getSuperclass().getDeclaredField("servletMap");
        f.setAccessible(true);
        assertTrue(concreteManager.isValid());
        assertTrue(((HashMap<String, AHttpServlet>) f.get(concreteManager)).isEmpty());
    }

    @Test
    public void testParseNotFoundConfigFile() {
        if(this.testFile != null && this.testFile.exists()){
            this.testFile.delete();
        }
        concreteManager = new ConcreteServletManagerBadFile("fakeFilePath", new CustomClassLoader(null));
        Field f = null;
        try {
            f = concreteManager.getClass().getSuperclass().getDeclaredField("servletMap");
            f.setAccessible(true);
            assertFalse(concreteManager.isValid());
            assertTrue(((HashMap<String, AHttpServlet>) f.get(concreteManager)).isEmpty());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            assertTrue(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            assertTrue(false);
        }
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
        concreteManager = new ConcreteServletManager("fakeFilePath", new CustomClassLoader(null));
        Field f = concreteManager.getClass().getSuperclass().getDeclaredField("servletMap");
        f.setAccessible(true);
        assertFalse(concreteManager.isValid());
        assertTrue(((HashMap<String, AHttpServlet>) f.get(concreteManager)).isEmpty());
    }

    @Test
    public void testHandlingRouteLessThanTwo() throws Exception {
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
        concreteManager = new ConcreteServletManager("fakeFilePath", new CustomClassLoader(null));
        HttpRequest requestToSend = new HttpRequest();
        Field f = requestToSend.getClass().getDeclaredField("method");
        f.setAccessible(true);
        f.set(requestToSend, "GET");
        Field f2 = requestToSend.getClass().getDeclaredField("uri");
        f2.setAccessible(true);
        f2.set(requestToSend, "");
        HttpResponse handledResponse = concreteManager.handleRequest(requestToSend);
        assertEquals(500, handledResponse.getStatus());
        assertEquals(Protocol.getProtocol().getStringRep(Keywords.INTERNAL_SERVER_ERROR), handledResponse.getPhrase());
    }

    @Test
    public void testHandlingBadMethod() throws Exception {
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
        concreteManager = new ConcreteServletManager("fakeFilePath", new CustomClassLoader(null));
        HttpRequest requestToSend = new HttpRequest();
        Field f = requestToSend.getClass().getDeclaredField("method");
        f.setAccessible(true);
        f.set(requestToSend, "GET");
        Field f2 = requestToSend.getClass().getDeclaredField("uri");
        f2.setAccessible(true);
        f2.set(requestToSend, "/blah/User");
        Field f3 = concreteManager.getClass().getSuperclass().getDeclaredField("invocationMap");
        f3.setAccessible(true);
        f3.set(concreteManager, new HashMap<String, Method>());
        HttpResponse handledResponse = concreteManager.handleRequest(requestToSend);
        assertEquals(500, handledResponse.getStatus());
        assertEquals(Protocol.getProtocol().getStringRep(Keywords.INTERNAL_SERVER_ERROR), handledResponse.getPhrase());
    }

    @Test
    public void testHandlingSuccess() throws Exception {
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
        concreteManager = new ConcreteServletManager("fakeFilePath", new CustomClassLoader(null));
        HttpRequest requestToSend = new HttpRequest();
        Field f = requestToSend.getClass().getDeclaredField("method");
        f.setAccessible(true);
        f.set(requestToSend, Protocol.getProtocol().getStringRep(Keywords.GET));
        Field f2 = requestToSend.getClass().getDeclaredField("uri");
        f2.setAccessible(true);
        f2.set(requestToSend, "/blah/User");
        HttpResponse handledResponse = concreteManager.handleRequest(requestToSend);
        assertEquals(200, handledResponse.getStatus());
        assertEquals(Protocol.getProtocol().getStringRep(Keywords.OK), handledResponse.getPhrase());
    }


    public class ConcreteServletManager extends AServletManager {

        public ConcreteServletManager(String filePath, ClassLoader classLoader) {
            super(filePath, classLoader);
        }

        @Override
        public void init() {

        }
    }

    public class CustomClassLoader extends ClassLoader {

        private HashMap<String, Class<?>> loadClassMap;

        public CustomClassLoader(HashMap<String, Class<?>> loadClassMap){
            this.loadClassMap = loadClassMap;
            if(this.loadClassMap == null){
                this.loadClassMap = new HashMap<>();
            }
            this.loadClassMap.put(ServletMock.class.getCanonicalName(), ServletMock.class);
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            File initialFile = new File(FileCreationUtility.RESOURCE_DIR.concat("/").concat("config.csv"));
            try {
                return new FileInputStream(initialFile);
            } catch (FileNotFoundException e) {
                return null;
            }
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            return this.loadClassMap.get(name);
        }
    }

    public class ConcreteServletManagerBadFile extends AServletManager {

        public ConcreteServletManagerBadFile(String filePath, ClassLoader classLoader) {
            super(filePath, classLoader);
        }

        @Override
        public void init() {

        }
    }
}