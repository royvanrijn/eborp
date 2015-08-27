package nl.royvanrijn.eborp;

import org.javatuples.Triplet;
import org.junit.Test;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ElasticsearchClientTest {

//	@Test
	public void go() throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(new URI
				("file:///Users/jeroen/Documents/jpoint/eborp/EborpServer/jsonInputExample")));

		int i = 1;
		for (String line : lines) {
			System.out.println("{ \"index\" : { \"_index\" : \"eborp\", \"_type\" : \"probe\", \"_id\" : \"" + i + "\" } }");
			System.out.println(line);
			i++;
		}
	}

	@Test
	public void es() throws Exception {
		ElasticsearchClient client = new ElasticsearchClient();

		Map<String, List<Triplet<Integer, Instant, String>>> all = client.getAll("1d");


		System.out.println(all.size());


	}

}