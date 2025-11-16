package org.mystic;

import static org.hamcrest.Matchers.equalTo;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.test.ESIntegTestCase;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;

/** @see https://stackoverflow.com/q/53707084/2663985 */

/** DEPRECATED AND NOT RECOMMENDED TO USE */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE)
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
@Ignore
public class SortingInvestigationTest extends ESIntegTestCase {

  private static final String TYPE = "product";
  private static final String INDEX = "strings";

  private void add(String jsonString, String id) {
    IndexRequest indexRequest = new IndexRequest(INDEX);
    indexRequest.source(jsonString, XContentType.JSON);
    index(INDEX, TYPE, id, jsonString);
  }

  @Test
  public void scoring() {
    CreateIndexRequest createIndexRequest =
        new CreateIndexRequest(INDEX)
            .settings(
                "{\"analysis\":{\"analyzer\":{\"english_text\":{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\"]},\"english_lowercase\":{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\"]}}}}",
                XContentType.JSON)
            .mapping(
                TYPE,
                "{\"properties\":{\"prdId\":{\"type\":\"keyword\"},\"title\":{\"type\":\"text\",\"analyzer\":\"english_text\"},\"department\":{\"type\":\"text\",\"analyzer\":\"english_lowercase\"},\"brand\":{\"type\":\"text\",\"analyzer\":\"english_lowercase\"},\"variations\":{\"type\":\"keyword\"},\"category\":{\"type\":\"text\",\"analyzer\":\"english_text\"},\"product\":{\"type\":\"text\",\"analyzer\":\"english_text\"},\"desc\":{\"type\":\"text\",\"analyzer\":\"english_text\"},\"displayColor\":{\"type\":\"text\",\"analyzer\":\"english_lowercase\"},\"color_refine\":{\"type\":\"text\",\"analyzer\":\"english_lowercase\"}}}",
                XContentType.JSON);

    ActionFuture<CreateIndexResponse> createIndexResponseActionFuture =
        admin().indices().create(createIndexRequest);
    CreateIndexResponse createIndexResponse = createIndexResponseActionFuture.actionGet();

    add("{\"department\":\"String1\"}", "1");
    add("{\"department\":\"alice\"}", "2");
    add("{\"department\":\"bob\"}", "3");
    add("{\"department\":\"string2\"}", "4");
    add("{\"department\":\"melanie\"}", "5");
    add("{\"department\":\"moana\"}", "6");
    add("{\"department\":\"12321\"}", "7");

    refresh();

    indexExists(INDEX);
    ensureGreen(INDEX);

    SearchResponse searchResponse =
        client()
            .prepareSearch(INDEX)
            .setQuery(QueryBuilders.matchAllQuery())
            .addSort("department", SortOrder.ASC)
            .execute()
            .actionGet();
    SearchHit[] hits = searchResponse.getHits().getHits();

    MatcherAssert.assertThat(hits.length, equalTo(1));

    for (SearchHit hit : hits) {
      System.out.println(hit);
    }
  }
}
