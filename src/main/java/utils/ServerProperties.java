package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerProperties {

	public Properties getProperties(String propertiesFileName) throws IOException {
		Properties props = new Properties();
		InputStream inputStream = null;

		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(propertiesFileName);
			
			// file not found
			if (inputStream == null) {
				throw new Exception();
			}

			props.load(inputStream);
		} catch (Exception e) {
			throw new FileNotFoundException("property file '" + propertiesFileName + "' not found");
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		return props;
	}
}
