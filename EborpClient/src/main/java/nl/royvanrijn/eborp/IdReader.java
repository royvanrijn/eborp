package nl.royvanrijn.eborp;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

/**
 * Reads the ID from disk (if set) otehrwise, create one.
 *
 */
public class IdReader {

	private static final String FILENAME = "eborp.id";
	private String id = null;
	
	public IdReader() {
		this.id = readOrCreateIdFromDisk();
	}

	public String getId() {
		return id;
	}
	
	private String readOrCreateIdFromDisk() {
		try {
		    Path path = FileSystems.getDefault().getPath(FILENAME);
			if(Files.exists(path)) {
				return Files.readAllLines(path).get(0);
			} else {
				String newId = UUID.randomUUID().toString();
				Files.write(path, Arrays.asList(newId));
				return newId;
			}
		} catch(IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}
}
