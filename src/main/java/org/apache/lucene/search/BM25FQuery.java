package org.apache.lucene.search;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.similarities.Similarity;
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
            if (subScorer instanceof TermBM25FQuery.TermBM25FScorer) {
                final Similarity.SimScorer simScorer = ((TermBM25FQuery.TermBM25FScorer) subScorer).getDocScorer();
                final TermBM25FQuery.TermBM25FWeight termWeight = (TermBM25FQuery.TermBM25FWeight) subScorer.getWeight();
                final BM25FSimilarity.BM25FStats stats = (BM25FSimilarity.BM25FStats) termWeight.getStats();
                final TermContext termStates = termWeight.getTermStates();
                final float avgdl = stats.getAvgdl();
                final String field = stats.getField();
                final float idf = stats.getIdf().getValue();
                final BM25FSimilarity.BM25DocScorer docScorer = (BM25FSimilarity.BM25DocScorer) ((TermBM25FQuery.TermBM25FScorer) subScorer).getDocScorer();
                final float doclen = docScorer.calculateDocLen(subScorer.docID());
            }

            final Query subQuery = subScorer.getWeight().getQuery();
            if (subQuery instanceof TermBM25FQuery) {
                final String field = ((TermBM25FQuery) subQuery).getTerm().field();
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
