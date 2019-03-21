package org.custom.price;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

public class CustomPricePlugin extends QParserPlugin {

  public static final String NAME = "customprice";

  public static final String CUSTOM_PRICE_PARAM_ACC = "acc_param";

  @Override
  public void init(NamedList args) {}

  @Override
  public QParser createParser(
      String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
    return new QParser(qstr, localParams, params, req) {

      String account;

      @Override
      public Query parse() {
        account = localParams.get(CUSTOM_PRICE_PARAM_ACC);
        CustomPriceQuery cpq = new CustomPriceQuery(account);
        return cpq;
      }
    };
  }
}
