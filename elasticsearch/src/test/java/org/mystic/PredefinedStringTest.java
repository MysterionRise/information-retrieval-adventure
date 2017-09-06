package org.mystic;

import static org.hamcrest.Matchers.equalTo;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

/** @see http://stackoverflow.com/q/42255674/2663985 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE)
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
public class PredefinedStringTest extends ESIntegTestCase {

  private static final String TYPE = "string";
  private static final String INDEX = "strings";

  public void add(String string, String id) {
    IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, id);
    indexRequest.source(string);
    index(INDEX, TYPE, id, string);
  }

  @Test
  public void scoring() throws Exception {
    add("{\"str\":\"string1\"}", "1");
    add("{\"str\":\"alice\"}", "2");
    add("{\"str\":\"bob\"}", "3");
    add("{\"str\":\"string2\"}", "4");
    add("{\"str\":\"melanie\"}", "5");
    add("{\"str\":\"moana\"}", "6");

    refresh(); // otherwise we would not find beers yet

    indexExists(INDEX); // verifies that index 'drinks' exists
    ensureGreen(INDEX); // ensures cluster status is green

    SearchResponse searchResponse =
        client()
            .prepareSearch(INDEX)
            .setQuery(QueryBuilders.termsQuery("str", "string1", "string3", "melani"))
            .execute()
            .actionGet();
    SearchHit[] hits = searchResponse.getHits().getHits();

    assertThat(hits.length, equalTo(1));

    for (SearchHit hit : hits) {
      System.out.println(hit.getSource());
    }
  }
}
