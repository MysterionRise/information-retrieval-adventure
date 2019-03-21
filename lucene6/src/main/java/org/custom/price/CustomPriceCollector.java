package org.custom.price;

import java.io.IOException;
import java.util.*;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.search.DelegatingCollector;

public class CustomPriceCollector extends DelegatingCollector {

  final Map<Integer, Integer> ids = new HashMap<>();
  final Random r = new Random();
  private final ResponseBuilder rb;
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

  public CustomPriceCollector(ResponseBuilder rb) {
    this.rb = rb;
    ids.clear();
    //    setDelegate(new FetchPriceCollector(rb));
  }

  @Override
  public void collect(int doc) throws IOException {
    // TODO GET from doc the external ID from the document like doc values
    ids.put(context.docBase + doc, r.nextInt(1000));
    super.collect(doc);
  }

  @Override
  public boolean needsScores() {
    return true;
  }

  @Override
  protected void doSetNextReader(LeafReaderContext context) throws IOException {
    super.doSetNextReader(context);
  }

  @Override
  public void finish() throws IOException {
    // TODO we could add something to the response here
    // TODO hack rb.rsp?

    rb.rsp.add("bla-bla-bla" + r.nextInt(), 100);
    rb.rsp.add("ids", ids);
    if (delegate instanceof DelegatingCollector) {
      ((DelegatingCollector) delegate).finish();
    }
  }
}
