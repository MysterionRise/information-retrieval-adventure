package org.custom.price;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.request.SolrRequestInfo;
import org.apache.solr.search.DelegatingCollector;
import org.apache.solr.search.PostFilter;

public class EnricherQuery extends Query implements PostFilter {

  public EnricherQuery() {}

  /**
   * Returns a DelegatingCollector to be run after the main query and all of its filters, but before
   * any sorting or grouping collectors
   */
  @Override
  public DelegatingCollector getFilterCollector(IndexSearcher searcher) {
    SolrRequestInfo info = SolrRequestInfo.getRequestInfo();
    ResponseBuilder rb = null;
    if (info != null) {
      rb = info.getResponseBuilder();
    }
    if (rb == null) throw new IllegalStateException("rb is null in EnrichQuery");

    return new EnricherCollector(rb, SolrRequestInfo.getRequestInfo().getReq().getContext());
  }

  @Override
  public String toString(String field) {
    return "custom price query";
  }

  @Override
  public boolean equals(Object obj) {
    return false;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean getCache() {
    return false;
  }

  @Override
  public void setCache(boolean cache) {}

  /**
   * Returns the cost of this query, used to order checking of filters that are not cached. If
   * getCache()==false &amp;&amp; getCost()&gt;=100 &amp;&amp; this instanceof PostFilter, then the
   * PostFilter interface will be used for filtering.
   *
   * <p>Use cost for order
   */
  @Override
  public int getCost() {
    // high cost to make sure it's the last one
    return 5000000;
  }

  @Override
  public void setCost(int cost) {}

  @Override
  public boolean getCacheSep() {
    return false;
  }

  @Override
  public void setCacheSep(boolean cacheSep) {}
}
