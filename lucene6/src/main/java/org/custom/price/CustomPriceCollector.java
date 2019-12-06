package org.custom.price;

import java.io.IOException;
import java.util.*;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.request.SolrRequestInfo;
import org.apache.solr.search.DelegatingCollector;

public class CustomPriceCollector extends DelegatingCollector {

  final Map<Integer, Integer> ids = new HashMap<>();
  final Random r = new Random();
  private final ResponseBuilder rb;
  private static Integer cnt = 0;
  private final Map fcontext;

  public CustomPriceCollector(ResponseBuilder rb, Map fcontext) {
    cnt = 0;
    this.rb = rb;
    this.fcontext = SolrRequestInfo.getRequestInfo().getReq().getContext();
    ids.clear();
  }

  @Override
  public void collect(int doc) throws IOException {
    // TODO GET from doc the external ID from the document like doc values
    ids.put(doc, r.nextInt(1000));
  }

  @Override
  protected void doSetNextReader(LeafReaderContext context) throws IOException {
    rb.rsp.add("segment" + cnt, "true");
    this.context = context;
    this.docBase = context.docBase;
    leafDelegate = delegate.getLeafCollector(context);
    rb.rsp.add("id size " + cnt, ids.size());
    if (this.scorer == null) {
      rb.rsp.add("segment#" + cnt, "scorer is null");
    } else {

      leafDelegate.setScorer(this.scorer);
      //        leafDelegate = delegate.getLeafCollector(context);
      // TODO this has been called on the end of the segment, it's time to do a batch to price
      // system with ids
      // we also need to evaluate through collected ids and collect them with delegate

      Map<Object, Object> reqContext = SolrRequestInfo.getRequestInfo().getReq().getContext();

      // TODO or check fcontext?

      for (Map.Entry<Integer, Integer> e : ids.entrySet()) {
        // TODO fix to use correct doc id
        // TODO use reqContext which is thread local to put data about actual prices there
        reqContext.put(e.getKey(), e.getValue());
        rb.rsp.add("delegate to string", delegate.toString());
        if (delegate instanceof DelegatingCollector) {
          ((DelegatingCollector) delegate).collect(e.getKey());
        }
      }
    }

    rb.rsp.add("bla-bla-do-set-next-reader" + r.nextInt(), cnt);
    ids.clear();
    cnt += 1;
  }

  @Override
  public boolean needsScores() {
    return true;
  }

  @Override
  public void finish() throws IOException {
    // we need to do last request to price system
    // since it will be called on the end og the last segment

    // TODO we could add something to the response here
    // TODO hack rb.rsp?

    Map<Object, Object> reqContext = SolrRequestInfo.getRequestInfo().getReq().getContext();

    // TODO or check fcontext?

    for (Map.Entry<Integer, Integer> e : ids.entrySet()) {
      // TODO fix to use correct doc id
      // TODO use reqContext which is thread local to put data about actual prices there
      reqContext.put(e.getKey(), e.getValue());
      if (delegate instanceof DelegatingCollector) {
        ((DelegatingCollector) delegate).collect(e.getKey());
      }
    }

    rb.rsp.add("bla-bla-finish" + r.nextInt(), cnt);
    rb.rsp.add("ids", ids);
    if (delegate instanceof DelegatingCollector) {
      ((DelegatingCollector) delegate).finish();
    }
  }
}
