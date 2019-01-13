import java.io.IOException;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class SetPipelineIndexRequest {

  public static void main(String[] args) throws IOException {
    RestHighLevelClient client =
        new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));

    final GetSettingsRequest data1 = new GetSettingsRequest().indices("data");
    final GetSettingsResponse data = client.indices().getSettings(data1, RequestOptions.DEFAULT);

    System.out.println(data);
    //        final IndexRequest indexRequest = new IndexRequest("index-name", "index-type");
    //        indexRequest.setPipeline("pipeline-name");
    //
    //        Map<String, Object> jsonMap = new HashMap<>();
    //        jsonMap.put("data", "e1xydGYxXGFuc2kNCkxvcmVtIGlwc3VtIGRvbG9yIHNpdCBhbWV0DQpccGFyIH0=");
    //
    //        indexRequest.source(jsonMap);
    //        final IndexResponse indexResponse = client.index(indexRequest);
  }
}
