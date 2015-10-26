package org.apache.lucene.search;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.util.Bits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BM25FQuery extends DisjunctionMaxQuery {

    private final Map<String, Float> fieldWeights;

    public BM25FQuery(Collection<Query> perFieldQueries, Map<String, Float> fieldWeights) {
        super(perFieldQueries, 0.0f);
        this.fieldWeights = fieldWeights;
    }

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
        return new BM25FWeight(searcher);
    }

    class BM25FWeight extends DisjunctionMaxQuery.DisjunctionMaxWeight {

        private final IndexSearcher searcher;

        public BM25FWeight(IndexSearcher searcher) throws IOException {
            super(searcher);
            this.searcher = searcher;
        }

        @Override
        // TODO need to add proper explanation
        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            return super.explain(context, doc);
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, Bits acceptDocs) throws IOException {
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
                return new BM25FScorer(this, scorers.toArray(new Scorer[scorers.size()]), searcher);
            }
        }
    }

    /**
     * need to implement how to accumulate the score for different parts of queries
     */
    class BM25FScorer extends DisjunctionScorer {

        private final IndexSearcher searcher;
        private double score;

        protected BM25FScorer(Weight weight, Scorer[] subScorers, IndexSearcher searcher) {
            super(weight, subScorers);
            this.searcher = searcher;
        }

        @Override
        protected void reset() {
            score = 0;
        }

        @Override
        protected void accum(Scorer subScorer) throws IOException {
            float weight = 1.0f;
            final Query subQuery = subScorer.getWeight().getQuery();
            if (subQuery instanceof TermQuery) {
                final String field = ((TermQuery) subQuery).getTerm().field();
                if (fieldWeights.containsKey(field)) {
                    weight = fieldWeights.get(field);
                }
            }
            this.score += subScorer.freq() * weight;
        }

        @Override
        protected float getFinal() {
            return (float) score;
        }
    }


}
