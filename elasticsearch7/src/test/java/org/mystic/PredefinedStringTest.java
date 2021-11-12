package org.mystic;

import static org.hamcrest.Matchers.equalTo;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.test.ESIntegTestCase;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

/** @see http://stackoverflow.com/q/42255674/2663985 */

/** DEPRECATED AND NOT RECOMMENDED TO USE */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE)
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
public class PredefinedStringTest extends ESIntegTestCase {

  private static final String TYPE = "string";
  private static final String INDEX = "strings";

  private void add(String string, String id) {
    IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, id);
    indexRequest.source(string, XContentType.JSON);
    index(INDEX, TYPE, id, string);
  }

  @Test
  public void scoring() {
    add("{\"str\":\"string1\"}", "1");
    add("{\"str\":\"alice\"}", "2");
    add("{\"str\":\"bob\"}", "3");
    add("{\"str\":\"string2\"}", "4");
    add("{\"str\":\"melanie\"}", "5");
    add("{\"str\":\"moana\"}", "6");

    refresh();

    indexExists(INDEX);
    ensureGreen(INDEX);

    SearchResponse searchResponse =
        client()
            .prepareSearch(INDEX)
            .setQuery(QueryBuilders.termsQuery("str", "string1", "string3", "melani"))
            .execute()
            .actionGet();
    SearchHit[] hits = searchResponse.getHits().getHits();

    MatcherAssert.assertThat(hits.length, equalTo(1));

    for (SearchHit hit : hits) {
      System.out.println(hit);
    }
  }
}
