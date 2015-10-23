package org.mystic.lucene;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Bits;

import java.io.IOException;
import java.util.Collection;

/**
 * my implementation of BM25F approach for Lucene
 */
public class BM25FQuery extends DisjunctionMaxQuery {

    public BM25FQuery(Collection<Query> disjuncts, float tieBreakerMultiplier) {
        super(disjuncts, tieBreakerMultiplier);
    }

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
        return new BM25FWeight(searcher);
    }

    private class BM25FWeight extends Weight {
        
        public BM25FWeight(IndexSearcher searcher) {
        }

        @Override
        public Explanation explain(LeafReaderContext leafReaderContext, int i) throws IOException {
            return null;
        }

        @Override
        public Query getQuery() {
            return null;
        }

        @Override
        public float getValueForNormalization() throws IOException {
            return 0;
        }

        @Override
        public void normalize(float v, float v1) {

        }

        @Override
        public Scorer scorer(LeafReaderContext leafReaderContext, Bits bits) throws IOException {
            return null;
        }
    }
}
