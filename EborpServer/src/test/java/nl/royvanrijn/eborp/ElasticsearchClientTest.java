package nl.royvanrijn.eborp;

import org.junit.Test;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ElasticsearchClientTest {

//	@Test
	public void go() throws Exception {
		ElasticsearchClient client = new ElasticsearchClient();
		List<String> lines = Files.readAllLines(Paths.get(new URI
				("file:///Users/jeroen/Documents/jpoint/eborp/EborpServer/jsonInputExample")));

		for (String line : lines) {
			String json = line.replaceAll("dfb41b70", "\"dfb41b70").replaceAll("9a19ad49be8c", "9a19ad49be8c\"");
			client.addSample(json);
		}
	}

	@Test
	public void es() throws Exception {
		ElasticsearchClient client = new ElasticsearchClient();

		Map<String, List<EborpSample>> all = client.getAll("1000d");


		System.out.println(all.size());


	}

}