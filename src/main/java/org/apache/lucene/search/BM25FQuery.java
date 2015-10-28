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

        private final float b;
        private final float k1;
        private float idf;
        private float avgdl;
        private float doclen;
        private float weight;
        private float sumFreq;

        protected BM25FScorer(Weight weight, Scorer[] subScorers, IndexSearcher searcher) {
            super(weight, subScorers);
            // we don't work with similarity other than BM25F
            if (!(searcher.getSimilarity() instanceof BM25FSimilarity)) {
                throw new RuntimeException("Wrong similarity is supplied! Only working with BM25FSimilarity");
            }
            this.b = ((BM25FSimilarity) searcher.getSimilarity()).getB();
            this.k1 = ((BM25FSimilarity) searcher.getSimilarity()).getK1();
        }

        @Override
        protected void reset() {
            idf = 0.0f;
            avgdl = 0.0f;
            doclen = 0.0f;
            weight = 1.0f;
            sumFreq = 0.0f;
        }

        @Override
        protected void accum(Scorer subScorer) throws IOException {
            if (subScorer instanceof TermBM25FQuery.TermBM25FScorer) {
                final Similarity.SimScorer simScorer = ((TermBM25FQuery.TermBM25FScorer) subScorer).getDocScorer();
                final TermBM25FQuery.TermBM25FWeight termWeight = (TermBM25FQuery.TermBM25FWeight) subScorer.getWeight();
                final BM25FSimilarity.BM25FStats stats = (BM25FSimilarity.BM25FStats) termWeight.getStats();
                final TermContext termStates = termWeight.getTermStates();
                avgdl = stats.getAvgdl();
                final String field = stats.getField();
                if (fieldWeights.containsKey(field)) {
                    weight = fieldWeights.get(field);
                }
                idf = stats.getIdf().getValue();
                final BM25FSimilarity.BM25DocScorer docScorer = (BM25FSimilarity.BM25DocScorer) ((TermBM25FQuery.TermBM25FScorer) subScorer).getDocScorer();
                doclen = docScorer.calculateDocLen(subScorer.docID());
                sumFreq += subScorer.freq() * weight;
            }
        }

        @Override
        protected float getFinal() {
            return idf * ((sumFreq * (k1 + 1.0f)) / (sumFreq + k1 * (1.0f - b + b * (1.0f * doclen / avgdl))));
        }
    }


}
