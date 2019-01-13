package org.mystic;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

/** @see https://stackoverflow.com/q/53707084/2663985 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE)
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
public class SortingInvestigationTest extends ESIntegTestCase {

  private static final String TYPE = "product";
  private static final String INDEX = "strings";

  public void add(String string, String id) {
    IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, id);
    indexRequest.source(string);
    index(INDEX, TYPE, id, string);
  }

  @Test
  public void scoring() throws Exception {
    CreateIndexRequest createIndexRequest =
        new CreateIndexRequest(INDEX)
            .settings(
                "{\n"
                    + "    \"analysis\": {\n"
                    + "      \"analyzer\": {\n"
                    + "        \"english_text\": {\n"
                    + "          \"type\": \"custom\",\n"
                    + "          \"char_filter\": [\n"
                    + "            \"html_strip\"\n"
                    + "          ],\n"
                    + "          \"tokenizer\": \"standard\",\n"
                    + "          \"filter\": [\n"
                    + "            \"lowercase\"\n"
                    + "          ]\n"
                    + "        },\n"
                    + "        \"english_lowercase\": {\n"
                    + "          \"type\": \"custom\",\n"
                    + "          \"tokenizer\": \"keyword\",\n"
                    + "          \"filter\": [\n"
                    + "            \"lowercase\"\n"
                    + "          ]\n"
                    + "        }\n"
                    + "      }\n"
                    + "    }\n"
                    + "  }",
                XContentType.JSON)
            .mapping(
                TYPE,
                "{\n"
                    + "    \"product\": {\n"
                    + "      \"properties\": {\n"
                    + "        \"prdId\": {\n"
                    + "          \"type\": \"keyword\"\n"
                    + "        },\n"
                    + "        \"title\": {\n"
                    + "          \"type\": \"text\",\n"
                    + "          \"analyzer\": \"english_text\"\n"
                    + "        },\n"
                    + "        \"department\": {\n"
                    + "          \"type\": \"text\",\n"
                    + "          \"fielddata\": true,\n"
                    + "          \"analyzer\": \"english_lowercase\",\n"
                    + "          \"fields\": {\n"
                    + "            \"keyword\": {\n"
                    + "              \"type\": \"keyword\"\n"
                    + "            }\n"
                    + "          }\n"
                    + "        },\n"
                    + "        \"brand\": {\n"
                    + "          \"type\": \"text\",\n"
                    + "          \"analyzer\": \"english_lowercase\",\n"
                    + "          \"fields\": {\n"
                    + "            \"keyword\": {\n"
                    + "              \"type\": \"keyword\"\n"
                    + "            }\n"
                    + "          }\n"
                    + "        },\n"
                    + "        \"variations\": {\n"
                    + "          \"type\": \"keyword\"\n"
                    + "        },\n"
                    + "        \"category\": {\n"
                    + "          \"type\": \"text\",\n"
                    + "          \"analyzer\": \"english_text\"\n"
                    + "        },\n"
                    + "        \"product\": {\n"
                    + "          \"type\": \"text\",\n"
                    + "          \"analyzer\": \"english_text\",\n"
                    + "          \"fields\": {\n"
                    + "            \"keyword\": {\n"
                    + "              \"type\": \"keyword\"\n"
                    + "            }\n"
                    + "          }\n"
                    + "        },\n"
                    + "        \"desc\": {\n"
                    + "          \"type\": \"text\",\n"
                    + "          \"analyzer\": \"english_text\"\n"
                    + "        },\n"
                    + "        \"displayColor\": {\n"
                    + "          \"type\": \"text\",\n"
                    + "          \"analyzer\": \"english_lowercase\",\n"
                    + "          \"fields\": {\n"
                    + "            \"keyword\": {\n"
                    + "              \"type\": \"keyword\"\n"
                    + "            }\n"
                    + "          }\n"
                    + "        },\n"
                    + "        \"color_refine\": {\n"
                    + "          \"type\": \"text\",\n"
                    + "          \"analyzer\": \"english_lowercase\",\n"
                    + "          \"fields\": {\n"
                    + "            \"keyword\": {\n"
                    + "              \"type\": \"keyword\"\n"
                    + "            }\n"
                    + "          }\n"
                    + "        }\n"
                    + "      }\n"
                    + "    }\n"
                    + "  }",
                XContentType.JSON);

    ActionFuture<CreateIndexResponse> createIndexResponseActionFuture =
        admin().indices().create(createIndexRequest);
    CreateIndexResponse createIndexResponse = createIndexResponseActionFuture.actionGet();
    System.out.println(createIndexResponse);

    add("{\"department\":\"String1\"}", "1");
    add("{\"department\":\"alice\"}", "2");
    add("{\"department\":\"bob\"}", "3");
    add("{\"department\":\"string2\"}", "4");
    add("{\"department\":\"melanie\"}", "5");
    add("{\"department\":\"moana\"}", "6");
    add("{\"department\":\"12321\"}", "7");

    refresh(); // otherwise we would not find beers yet

    indexExists(INDEX); // verifies that index 'drinks' exists
    ensureGreen(INDEX); // ensures cluster status is green

    SearchResponse searchResponse =
        client()
            .prepareSearch(INDEX)
            .setQuery(QueryBuilders.matchAllQuery())
            .addSort("department", SortOrder.ASC)
            .execute()
            .actionGet();
    SearchHit[] hits = searchResponse.getHits().getHits();

    //    assertThat(hits.length, equalTo(1));

    for (SearchHit hit : hits) {
      System.out.println(hit);
    }
  }
}
