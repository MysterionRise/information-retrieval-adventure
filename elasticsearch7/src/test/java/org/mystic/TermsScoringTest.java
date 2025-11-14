package org.mystic;

import static org.hamcrest.Matchers.equalTo;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.test.ESIntegTestCase;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mystic.model.User;

/** DEPRECATED AND NOT RECOMMENDED TO USE */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE)
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
public class TermsScoringTest extends ESIntegTestCase {

  private static final String TYPE = "user";
  private static final String INDEX = "users";

  private void add(User user) {
    System.out.printf("adding user to search index: %s\n", user);
    IndexRequest indexRequest = new IndexRequest(INDEX);
    indexRequest.source(user.toJson(), XContentType.JSON);
    index(INDEX, TYPE, user.getId(), user.toJson());
  }

  @Test
  public void scoring() {
    add(new User("1", "Alice", "boxing"));
    add(new User("2", "Bob", "music", "boxing", "karate"));
    add(new User("3", "Clare", "boxing"));

    refresh();

    indexExists(INDEX);
    ensureGreen(INDEX);

    SearchResponse response =
        client()
            .prepareSearch(INDEX)
            .setQuery(QueryBuilders.termsQuery("interests", "music", "boxing", "karate"))
            .execute()
            .actionGet();
    SearchHit[] hits = response.getHits().getHits();
    for (SearchHit searchHit : hits) {
      System.out.println(searchHit.getId() + " " + searchHit.getScore());
    }

    MatcherAssert.assertThat("hits should be equal to 3", hits.length, equalTo(3));
    MatcherAssert.assertThat(
        "all scores are the same, no matter number of matched terms",
        hits[0].getScore(),
        equalTo(hits[1].getScore()));
  }
}
