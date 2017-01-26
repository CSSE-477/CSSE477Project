package integration;

import java.io.IOException;

/**
 * Created by TrottaSN on 1/19/2017.
 */

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import unit.GMTConversionTest;
import unit.HttpRequestTest;
import unit.HttpResponseFactoryTest;
import unit.HttpResponseTest;
import unit.ServerPropertiesTest;
import unit.ServerTest;
import utils.FileCreationUtility;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	ServerTest.class,
	HttpRequestTest.class,
	HttpResponseTest.class,
	HttpResponseFactoryTest.class,
	GMTConversionTest.class,
	ServerPropertiesTest.class,
	GetRequestTests.class,
	PutRequestTests.class,
	DeleteRequestTests.class
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
