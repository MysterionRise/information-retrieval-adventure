package org.custom.price;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

public class EnricherPlugin extends QParserPlugin {

  public static final String NAME = "enrich";

  @Override
  public void init(NamedList args) {}

  @Override
  public QParser createParser(
      String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {

    return new QParser(qstr, localParams, params, req) {

      @Override
      public Query parse() {
        EnricherQuery eq = new EnricherQuery();
        return eq;
      }
    };
  }
}
