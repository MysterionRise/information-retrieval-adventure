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
    private final Map fcontext;

    public CustomPriceCollector(ResponseBuilder rb, Map fcontext) {
        this.rb = rb;
        this.fcontext = SolrRequestInfo.getRequestInfo().getReq().getContext();
        ids.clear();
    }

    @Override
    public void collect(int doc) throws IOException {
        // TODO GET from doc the external ID from the document like doc values
        ids.put(doc, r.nextInt(1000));
        //    super.collect(doc);
    }

    @Override
    public boolean needsScores() {
        return true;
    }

    @Override
    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        this.context = context;
        this.docBase = context.docBase;
        leafDelegate = delegate.getLeafCollector(context);
        // TODO this has been called on the end of the segment, it's time to do a batch to price system with ids
        // we also need to evaluate through collected ids and collect them with delegate

        Map<Object, Object> reqContext = SolrRequestInfo.getRequestInfo().getReq().getContext();

        // TODO or check fcontext?

        for (Map.Entry<Integer, Integer> e : ids.entrySet()) {
            // TODO fix to use correct doc id
            // TODO use reqContext which is thread local to put data about actual prices there
            reqContext.put(e.getKey(), e.getValue());
            leafDelegate.collect(e.getKey());
        }

        rb.rsp.add("bla-bla-bla" + r.nextInt(), 100);
        ids.clear();
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
            delegate.getLeafCollector(context).collect(e.getKey());
        }

        rb.rsp.add("bla-bla-bla" + r.nextInt(), 100);
        rb.rsp.add("ids", ids);
        if (delegate instanceof DelegatingCollector) {
            ((DelegatingCollector) delegate).finish();
        }
    }
}
