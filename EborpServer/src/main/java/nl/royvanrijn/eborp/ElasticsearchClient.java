package nl.royvanrijn.eborp;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.javatuples.Triplet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticsearchClient {

	private final Client client;
	private static final String INDEX_NAME = "eborp";
	private static final String TYPE_NAME = "probe";

	public ElasticsearchClient() {
		Node node = NodeBuilder.nodeBuilder().clusterName("es_wifi").node();
		client = node.client();
	}

	public void addProbe(String probeAsJson) {
		IndexRequestBuilder indexRequest = client.prepareIndex(INDEX_NAME, TYPE_NAME);
		indexRequest.setSource(probeAsJson);
		indexRequest.execute().actionGet();
	}

	public Map<String, List<Triplet<Integer, Instant, String>>> getAll(String timeRange) {
		//TODO datumrange toevoegen
		SearchRequestBuilder search = client.prepareSearch(INDEX_NAME);

		RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("epoch");
		rangeQuery.from("now-" + timeRange);

		SearchRequestBuilder srb = search.setQuery(rangeQuery);
		srb.setSize(1000000);

		SearchResponse response = srb.execute().actionGet();

		Map<String, List<Triplet<Integer, Instant, String>>> results = new HashMap<>();

		for (SearchHit hit : response.getHits().hits()) {

			Map<String, Object> map = hit.getSource();

			String mac = (String) map.get("mac");
			Integer dbm = new Integer(map.get("dbm").toString());
			Long epoch = (Long) map.get("epoch");
			String source = (String) map.get("source");

			//TODO goede tijdzone
			Triplet<Integer, Instant, String> r = new Triplet<>(dbm, Instant.ofEpochMilli(epoch), source);

			if (!results.containsKey(mac)) {
				results.put(mac, new ArrayList<>());
			}
			results.get(mac).add(r);
		}

		return results;
	}
}
