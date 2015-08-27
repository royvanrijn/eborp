package nl.royvanrijn.eborp;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.UUID;

/**
 * Reads the ID from disk (if set) otherwise, create one.
 */
public class PropertiesReader {

	public static final String SOURCE = "source";
	public static final String SERVER_ADDRESS = "address";

	private static final String FILENAME = "eborp.properties";

	private final Properties properties;

	public PropertiesReader() {
		properties = readOrCreate();
	}

	public Properties getProperties() {
		return properties;
	}

	private void write(Properties properties) {
		Path path = FileSystems.getDefault().getPath(FILENAME);

		try {
			properties.store(Files.newOutputStream(path), null);
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	private Properties readOrCreate() {
		Path path = FileSystems.getDefault().getPath(FILENAME);

		if(Files.exists(path)) {

			try {
				Properties properties = new Properties();
				properties.load(Files.newInputStream(path));
				return properties;
			} catch (IOException io) {
				io.printStackTrace();
			}
		}

		// Didn't exist or IO error:
		return initialFill();
	}

	private Properties initialFill() {
		Properties properties = new Properties();
		properties.put(SOURCE, UUID.randomUUID().toString());
		properties.put(SERVER_ADDRESS, "http://192.168.0.199:7777/detection");
		write(properties);
		return properties;
	}
}
