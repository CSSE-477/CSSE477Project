package integration;

/**
 * Created by TrottaSN on 1/19/2017.
 */

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import unit.HttpRequestTest;
import unit.HttpResponseTest;
import unit.ServerPropertiesTest;
import unit.ServerTest;
import utils.FileCreationUtility;

import java.io.IOException;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	ServerTest.class,
	HttpRequestTest.class,
	HttpResponseTest.class,
	ServerPropertiesTest.class,
	GetRequestTests.class
})
public class SuiteRunner {

    @BeforeClass
    public static void setUp() throws IOException {
        FileCreationUtility.createResourceFile();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        FileCreationUtility.reclaimResourceDirectory();
    }

}
