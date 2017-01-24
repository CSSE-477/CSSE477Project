package unit;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import utils.ServerProperties;

public class ServerPropertiesTest {
	private ServerProperties serverProperties;

	@Before
	public void setUp() {
		serverProperties = new ServerProperties();
	}

	@Test(expected=FileNotFoundException.class)
	public void testBadResourceFileName() throws IOException {
		String propertiesFileName = "test.properties";
		serverProperties.getProperties(propertiesFileName);
	}
	
	@Test
	public void testGetProperties() throws IOException {
		String propertiesFileName = "config.properties";
		Properties props = serverProperties.getProperties(propertiesFileName);
		assertNotNull(props.getProperty("port"));
		assertNotNull(props.getProperty("rootDirectory"));
	}
}
