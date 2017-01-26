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
	GetRequestTests.class,
	PutRequestTests.class,
	DeleteRequestTests.class,
	PostRequestTests.class
})
public class IntegrationSuite {

    @BeforeClass
    public static void setUp() throws IOException {
    }

    @AfterClass
    public static void tearDown() throws IOException {
    }

}
