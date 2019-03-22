package org.custom.price;

import java.io.IOException;
import java.util.Map;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.request.SolrRequestInfo;
import org.apache.solr.search.DelegatingCollector;

public class EnricherCollector extends DelegatingCollector {

  private final ResponseBuilder rb;
  final Map fcontext;
  /*
  if (postFilters != null) {
    Collections.sort(postFilters, sortByCost);
    for (int i = postFilters.size() - 1; i >= 0; i--) {
      DelegatingCollector prev = pf.postFilter;
      pf.postFilter = ((PostFilter) postFilters.get(i)).getFilterCollector(this);
      if (prev != null) pf.postFilter.setDelegate(prev);
    }
  }
   */

  public EnricherCollector(ResponseBuilder rb, Map fcontext) {
    this.rb = rb;
    this.fcontext = fcontext;
  }

  @Override
  public void collect(int doc) throws IOException {
    Map<Object, Object> context = SolrRequestInfo.getRequestInfo().getReq().getContext();
    if (context == null) {
      throw new RuntimeException("context is null from request info");
    }
    //    int o = (int) context.get(this.docBase + doc);
    //    if (o > 0) {
      leafDelegate.collect(doc);
    //    }
    // TODO if know price we could filter document?
  }

  @Override
  public void finish() throws IOException {
    rb.rsp.add("bla", 20);

    if (delegate instanceof DelegatingCollector) {
      ((DelegatingCollector) delegate).finish();
    }
  }
}
