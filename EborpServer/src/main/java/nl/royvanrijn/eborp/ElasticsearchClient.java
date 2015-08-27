package nl.royvanrijn.eborp;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElasticsearchClient {

	private final Client client;
	private static final String INDEX_NAME = "eborp2";
	private static final String TYPE_NAME = "sample";

	public ElasticsearchClient() {
		ImmutableSettings.Builder builder = ImmutableSettings.builder();
		builder.put("cluster.name", "es_wifi");
		builder.put("node.data", "false");
		builder.put("node.master", "false");
		Node node = NodeBuilder.nodeBuilder().settings(builder.build()).node();
		client = node.client();
	}

	public void addSample(String sampleAsJson) {
		IndexRequestBuilder indexRequest = client.prepareIndex(INDEX_NAME, TYPE_NAME);
		indexRequest.setSource(sampleAsJson);
		indexRequest.setRefresh(false);
		IndexResponse response = indexRequest.execute().actionGet();
		System.out.println(response);
	}

	public Map<String, List<EborpSample>> getAll(String timeRange) {
		//TODO datumrange toevoegen
		SearchRequestBuilder search = client.prepareSearch(INDEX_NAME);

		RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("epoch");
		rangeQuery.from("now-" + timeRange);

		SearchRequestBuilder srb = search.setQuery(rangeQuery);
		srb.setSize(1000000);

        final SearchResponse response = srb.execute().actionGet();

//        final Map<String, List<Triplet<Integer, Instant, String>>> results = new HashMap<>();
//		System.out.println("response.getHits().hits() = " + response.getHits().hits().length);

		return Arrays.asList(response.getHits().hits()).stream()
				.map(SearchHit::getSource)
				.map(this::mapToEborpSample)
				.collect(Collectors.groupingBy(EborpSample::getMac));
    }

	private EborpSample mapToEborpSample(Map<String, Object> map) {
		final String mac = (String) map.get("mac");
		final Integer dbm = new Integer(map.get("dbm").toString());
		final Long epoch = (Long) map.get("epoch");
		final String source = (String) map.get("source");

		return new EborpSample(epoch, mac, dbm, source);
	}
}
