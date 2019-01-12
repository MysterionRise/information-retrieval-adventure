import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetPipelineIndexRequest {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));

        final IndexRequest indexRequest = new IndexRequest("index-name", "index-type");
        indexRequest.setPipeline("pipeline-name");

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("data", "e1xydGYxXGFuc2kNCkxvcmVtIGlwc3VtIGRvbG9yIHNpdCBhbWV0DQpccGFyIH0=");

        indexRequest.source(jsonMap);
        final IndexResponse indexResponse = client.index(indexRequest);
    }
}
