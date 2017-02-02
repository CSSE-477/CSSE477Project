package master;
import java.io.IOException;

/**
 * Created by TrottaSN on 1/19/2017.
 */

import integration.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import unit.*;
import utils.FileCreationUtility;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UnitSuite.class,
//        IntegrationSuite.class
})
public class MasterSuite {

    @BeforeClass
    public static void setUp() throws IOException {
    }

    @AfterClass
    public static void tearDown() throws IOException {
        FileCreationUtility.reclaimResourceDirectory();
    }

}
