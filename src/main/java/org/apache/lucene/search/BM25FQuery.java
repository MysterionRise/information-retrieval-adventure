package org.apache.lucene.search;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.Bits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    class BM25FWeight extends DisjunctionMaxWeight {


        public BM25FWeight(IndexSearcher searcher) throws IOException {
            super(searcher);
        }

        @Override
        public Scorer scorer(LeafReaderContext context, Bits acceptDocs) throws IOException {
            List<Scorer> scorers = new ArrayList<>();
            for (Weight w : weights) {
                // we will advance() subscorers
                Scorer subScorer = w.scorer(context, acceptDocs);
                if (subScorer != null) {
                    scorers.add(subScorer);
                }
            }
            if (scorers.isEmpty()) {
                // no sub-scorers had any documents
                return null;
            } else if (scorers.size() == 1) {
                // only one sub-scorer in this segment
                return scorers.get(0);
            } else {
                return new BM25FScorer(this, scorers.toArray(new Scorer[scorers.size()]));
            }
        }
    }

    class BM25FScorer extends DisjunctionScorer {

        protected BM25FScorer(Weight weight, Scorer[] subScorers) {
            super(weight, subScorers);
        }

        @Override
        protected void reset() {

        }

        @Override
        protected void accum(Scorer subScorer) throws IOException {

        }

        @Override
        protected float getFinal() {
            return 0;
        }
    }
}
