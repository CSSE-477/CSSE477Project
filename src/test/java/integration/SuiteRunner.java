package integration;

/**
 * Created by TrottaSN on 1/19/2017.
 */

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import utilities.FileCreationUtility;

import java.io.IOException;

@RunWith(Suite.class)
@Suite.SuiteClasses({GetRequestTests.class})
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
