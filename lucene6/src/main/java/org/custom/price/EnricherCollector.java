package org.custom.price;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.queries.function.valuesource.ConstValueSource;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.search.DelegatingCollector;

public class EnricherCollector extends DelegatingCollector {

  private final ResponseBuilder rb;
  final Map fcontext;
  ValueSourceScorer scorer;
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

  @Override
  public boolean needsScores() {
    return true;
  }

  public EnricherCollector(ResponseBuilder rb, Map fcontext) {
    this.rb = rb;
    this.fcontext = fcontext;
  }

  @Override
  public void collect(int doc) throws IOException {
    super.collect(doc);
    //    final Map<Integer, Integer> ids = (Map<Integer, Integer>) rb.rsp.getValues().get("ids");
    //    if (ids.get(context.docBase + doc) > 500) {
    //      leafDelegate.collect(doc);
    //    }
  }

  @Override
  protected void doSetNextReader(LeafReaderContext context) throws IOException {
    super.doSetNextReader(context);
    FunctionValues dv = new ConstValueSource(10.0f).getValues(fcontext, context);
    scorer = dv.getScorer(context);
  }

  @Override
  public void finish() throws IOException {
    rb.rsp.add("bla", 20);
    if (delegate instanceof DelegatingCollector) {
      ((DelegatingCollector) delegate).finish();
    }
  }
}
