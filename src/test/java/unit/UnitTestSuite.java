package unit;
import java.io.IOException;

/**
 * Created by TrottaSN on 1/19/2017.
 */

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GMTConversionTest.class,
        HttpRequestTest.class,
        HttpResponseFactoryTest.class,
        HttpResponseTest.class,
        ServerPropertiesTest.class,
        ServerTest.class
})
public class UnitTestSuite {

    @BeforeClass
    public static void setUp() throws IOException {
    }

    @AfterClass
    public static void tearDown() throws IOException {
    }

}
