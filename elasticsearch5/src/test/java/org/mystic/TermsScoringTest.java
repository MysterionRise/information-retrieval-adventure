package org.mystic;

import static org.hamcrest.Matchers.equalTo;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;
import org.mystic.model.User;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE)
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
public class TermsScoringTest extends ESIntegTestCase {

  private static final String TYPE = "user";
  private static final String INDEX = "users";

  public void add(User user) {
    System.out.printf("adding user to search index: %s\n", user);
    IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, user.getId());
    indexRequest.source(user.toJson());
    index(INDEX, TYPE, user.getId(), user.toJson());
  }

  @Test
  public void scoring() throws Exception {
    add(new User("1", "Alice", "boxing"));
    add(new User("2", "Bob", "music", "boxing"));
    add(new User("3", "Clare", "boxing"));

    refresh(); // otherwise we would not find beers yet

    indexExists(INDEX); // verifies that index 'drinks' exists
    ensureGreen(INDEX); // ensures cluster status is green

    float avg1 = 0.0f;
    float avg2 = 0.0f;

    Client client = client();
    for (int i = 0; i < 100000; ++i) {

      long startTime = System.nanoTime();
      IndicesStatsResponse indicesStatsResponse =
          client.admin().indices().prepareStats(INDEX).get();
      System.out.println(indicesStatsResponse.getIndices().get(INDEX).getTotal().docs.getCount());
      avg1 += System.nanoTime() - startTime;
      //System.out.println(System.nanoTime() - startTime);

      startTime = System.nanoTime();
      SearchResponse searchResponse =
          client.prepareSearch(INDEX).setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
      System.out.println(searchResponse.getHits().getHits().length);
      avg2 += System.nanoTime() - startTime;
      //System.out.println(System.nanoTime() - startTime);

    }

    System.out.println("----------------stupid testing-------------");

    System.out.println(avg1 / 100000f);

    System.out.println(avg2 / 100000f);

    System.out.println("----------------stupid testing-------------");

    SearchResponse response =
        client()
            .prepareSearch(INDEX)
            .setTypes(TYPE)
            .setQuery(QueryBuilders.termsQuery("interests", "music", "boxing", "karate"))
            .execute()
            .actionGet();
    SearchHit[] hits = response.getHits().getHits();
    for (SearchHit searchHit : hits) {
      System.out.println(searchHit.id() + " " + searchHit.score());
    }

    assertThat("hits should be equal to 3", hits.length, equalTo(3));
    assertThat("2 matched terms over 1", hits[0].id(), equalTo("2"));
  }
}
