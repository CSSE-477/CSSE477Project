package integration;

import app.SimpleWebServer;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import protocol.ProtocolConfiguration;
import server.Server;
import utils.FileCreationUtility;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by TrottaSN on 1/25/2017.
 */
public class PostRequestTests {

    private static Server server;
    private static int port;

    private final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private final static JsonFactory JSON_FACTORY = new JacksonFactory();

    private static HttpRequestFactory requestFactory;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String rootDirectory = "web";

        port = 8080;
        ProtocolConfiguration protocol = SimpleWebServer.getProtocolConfiguration();
        server = new Server(port, SimpleWebServer.getPopulatedFactoryHash(rootDirectory, protocol), protocol);
        Thread runner = new Thread(server);
        runner.start();

        int sleepAmount = 1000;
        int retries = 10;
        while(!server.isReady()) {
            if (retries > 0) {
                Thread.sleep(sleepAmount);
            }
            else{
                break;
            }
            retries = retries - 1;
        }
        requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(new JsonObjectParser(JSON_FACTORY)));
    }

    @Test
    public void testPostCreationMatches() throws Exception {
        String fullPath = "./web/notFound.txt";
        File fileBeforeCall = new File(fullPath);
        if(fileBeforeCall.exists()){
            fileBeforeCall.delete();
        }
        GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/notFound.txt");
        String requestBody = "Extra Content.";
        HttpRequest request = requestFactory.buildPostRequest(url, ByteArrayContent.fromString("text/plain", requestBody));
        request.getHeaders().setContentType("application/json");
        HttpResponse response = null;
        try {
            response = request.execute();
        }
        catch (HttpResponseException e) {
            assertTrue(false);
        }
        int expectedCode = 200;
        int actualCode = response.getStatusCode();
        assertEquals(expectedCode, actualCode);

        String actualContent = requestBody;
        byte[] responseContentByteArray = new byte[Math.toIntExact(response.getHeaders().getContentLength())];
        response.getContent().read(responseContentByteArray);
        assertTrue(actualContent.equals(new String(responseContentByteArray)));

        File madeFile = new File(fullPath);
        assertTrue(madeFile.exists());
        assertTrue(madeFile.isFile());

        String resourceFileName = "test";
        FileCreationUtility.createResourceFile(resourceFileName, FileCreationUtility.Extension.txt);
        FileCreationUtility.writeToTestFile(requestBody, false, resourceFileName, FileCreationUtility.Extension.txt);
        File testFile = FileCreationUtility.retrieveTestFile(resourceFileName, FileCreationUtility.Extension.txt);
        assert testFile != null;

        assertTrue(FileUtils.contentEquals(testFile, madeFile));

        madeFile.delete();
    }

    @Test
    public void testPostAdditionMatches() throws Exception {
        String fullPath = "./web/notFound.txt";
        String previousContent = "Previous Content.";
        File fileBeforeCall = new File(fullPath);
        if(fileBeforeCall.exists()){
            fileBeforeCall.delete();
        }
        fileBeforeCall.createNewFile();
        try{
            FileWriter writer = new FileWriter(fullPath, false);
            writer.write(previousContent, 0, previousContent.length());
            writer.close();
        } catch (IOException e) {
            assertTrue(false);
        }
        GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/notFound.txt");
        String requestBody = "Extra Content.";
        HttpRequest request = requestFactory.buildPostRequest(url, ByteArrayContent.fromString("text/plain", requestBody));
        request.getHeaders().setContentType("application/json");
        HttpResponse response = null;
        try {
            response = request.execute();
        }
        catch (HttpResponseException e) {
            assertTrue(false);
        }
        int expectedCode = 200;
        int actualCode = response.getStatusCode();
        assertEquals(expectedCode, actualCode);

        String actualContent = "Previous Content.".concat(requestBody);
        byte[] responseContentByteArray = new byte[Math.toIntExact(response.getHeaders().getContentLength())];
        response.getContent().read(responseContentByteArray);
        assertTrue(actualContent.equals(new String(responseContentByteArray)));

        File madeFile = new File(fullPath);
        assertTrue(madeFile.exists());
        assertTrue(madeFile.isFile());

        String resourceFileName = "test";
        FileCreationUtility.createResourceFile(resourceFileName, FileCreationUtility.Extension.txt);
        FileCreationUtility.writeToTestFile(previousContent, false, resourceFileName, FileCreationUtility.Extension.txt);
        FileCreationUtility.writeToTestFile(requestBody, true, resourceFileName, FileCreationUtility.Extension.txt);
        File testFile = FileCreationUtility.retrieveTestFile(resourceFileName, FileCreationUtility.Extension.txt);
        assert testFile != null;

        assertTrue(FileUtils.contentEquals(testFile, madeFile));

        madeFile.delete();
    }

    @Test
    public void testPost500InternalServerErrorFileLocked() throws Exception {
        String fileName = "test.txt";
        String rootDirectory = "./web";
        File testFile = new File(rootDirectory, fileName);
        if(testFile.exists()){
            testFile.delete();
        }
        testFile.createNewFile();
        GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + rootDirectory + "/" + fileName);
        String requestBody = "This is POST request content!";
        HttpRequest request = requestFactory.buildPostRequest(url, ByteArrayContent.fromString("text/plain", requestBody));
        request.getHeaders().setContentType("application/json");

        testFile = new File(rootDirectory, fileName);
        testFile.setWritable(false);

        try {
            request.execute();
        } catch (HttpResponseException e) {
            int expectedCode = 500;
            int actualCode = e.getStatusCode();
            assertEquals(expectedCode, actualCode);
            testFile.setWritable(true);
            testFile.delete();
            return;
        }
        assertTrue(false);
    }

    @Test
    public void testPost500InternalServerErrorDirectory() throws Exception {
        String fileName = "file.txt";
        String rootDirectory = "./web/test";
        File testFile = new File(rootDirectory);
        if(testFile.exists()){
            testFile.delete();
        }
        testFile.mkdirs();
        File testFile2 = new File(rootDirectory, fileName);
        if(testFile2.exists()) {
            testFile2.delete();
        }
        testFile2.createNewFile();
        GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + rootDirectory + "/" + fileName);
        String requestBody = "This is POST request content!";
        HttpRequest request = requestFactory.buildPostRequest(url, ByteArrayContent.fromString("text/plain", requestBody));
        request.getHeaders().setContentType("application/json");

        testFile = new File(rootDirectory);

        try {
            request.execute();
        } catch (HttpResponseException e) {
            int expectedCode = 500;
            int actualCode = e.getStatusCode();
            assertEquals(expectedCode, actualCode);
            if(testFile2.exists()) {
                testFile2.delete();
            }
            if(testFile.exists()){
                testFile.delete();
            }
            return;
        }
        assertTrue(false);
    }

    @AfterClass
    public static void tearDownAfterClass() {
        server.stop();
    }

}
