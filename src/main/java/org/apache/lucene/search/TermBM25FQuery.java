package org.apache.lucene.search;

import org.apache.lucene.index.*;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

import java.io.IOException;
import java.util.*;

public class TermBM25FQuery extends Query {
    private final Term term;
    private final int docFreq;
    private final TermContext perReaderTermState;

    final class TermBM25FWeight extends Weight {
        private final Similarity similarity;
        private final Similarity.SimWeight stats;
        private final TermContext termStates;

        public TermBM25FWeight(IndexSearcher searcher, TermContext termStates)
                throws IOException {
            assert termStates != null : "TermContext must not be null";
            this.termStates = termStates;
            this.similarity = searcher.getSimilarity();
            this.stats = similarity.computeWeight(
                    getBoost(),
                    searcher.collectionStatistics(term.field()),
                    searcher.termStatistics(term, termStates));
        }

        @Override
        public String toString() {
            return "bm25f weight(" + TermBM25FQuery.this + ")";
        }

        @Override
        public Query getQuery() {
            return TermBM25FQuery.this;
        }

        @Override
        public float getValueForNormalization() {
            return stats.getValueForNormalization();
        }

        @Override
        public void normalize(float queryNorm, float topLevelBoost) {
            stats.normalize(queryNorm, topLevelBoost);
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, Bits acceptDocs) throws IOException {
            assert termStates.topReaderContext == ReaderUtil.getTopLevelContext(context) : "The top-reader used to create Weight (" + termStates.topReaderContext + ") is not the same as the current reader's top-reader (" + ReaderUtil.getTopLevelContext(context);
            final TermsEnum termsEnum = getTermsEnum(context);
            if (termsEnum == null) {
                return null;
            }
            DocsEnum docs = termsEnum.docs(acceptDocs, null);
            assert docs != null;
            return new TermScorer(this, docs, similarity.simScorer(stats, context));
        }

        /**
         * Returns a {@link TermsEnum} positioned at this weights Term or null if
         * the term does not exist in the given context
         */
        private TermsEnum getTermsEnum(AtomicReaderContext context) throws IOException {
            final TermState state = termStates.get(context.ord);
            if (state == null) { // term is not present in that reader
                assert termNotInReader(context.reader(), term) : "no termstate found but term exists in reader term=" + term;
                return null;
            }
            //System.out.println("LD=" + reader.getLiveDocs() + " set?=" + (reader.getLiveDocs() != null ? reader.getLiveDocs().get(0) : "null"));
            final TermsEnum termsEnum = context.reader().terms(term.field()).iterator(null);
            termsEnum.seekExact(term.bytes(), state);
            return termsEnum;
        }

        private boolean termNotInReader(AtomicReader reader, Term term) throws IOException {
            // only called from assert
            //System.out.println("TQ.termNotInReader reader=" + reader + " term=" + field + ":" + bytes.utf8ToString());
            return reader.docFreq(term) == 0;
        }

        @Override
        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            Scorer scorer = scorer(context, context.reader().getLiveDocs());
            if (scorer != null) {
                int newDoc = scorer.advance(doc);
                if (newDoc == doc) {
                    float freq = scorer.freq();
                    Similarity.SimScorer docScorer = similarity.simScorer(stats, context);
                    ComplexExplanation result = new ComplexExplanation();
                    result.setDescription("weight(" + getQuery() + " in " + doc + ") [" + similarity.getClass().getSimpleName() + "], result of:");
                    Explanation scoreExplanation = docScorer.explain(doc, new Explanation(freq, "termFreq=" + freq));
                    result.addDetail(scoreExplanation);
                    result.setValue(scoreExplanation.getValue());
                    result.setMatch(true);
                    return result;
                }
            }
            return new ComplexExplanation(false, 0.0f, "no matching term");
        }
    }

    /**
     * Constructs a query for the term <code>t</code>.
     */
    public TermBM25FQuery(Term t) {
        this(t, -1);
    }

    /**
     * Expert: constructs a TermQuery that will use the
     * provided docFreq instead of looking up the docFreq
     * against the searcher.
     */
    public TermBM25FQuery(Term t, int docFreq) {
        term = t;
        this.docFreq = docFreq;
        perReaderTermState = null;
    }

    /**
     * Expert: constructs a TermQuery that will use the
     * provided docFreq instead of looking up the docFreq
     * against the searcher.
     */
    public TermBM25FQuery(Term t, TermContext states) {
        assert states != null;
        term = t;
        docFreq = states.docFreq();
        perReaderTermState = states;
    }

    /**
     * Returns the term of this query.
     */
    public Term getTerm() {
        return term;
    }

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
        final IndexReaderContext context = searcher.getTopReaderContext();
        final TermContext termState;
        if (perReaderTermState == null || perReaderTermState.topReaderContext != context) {
            // make TermQuery single-pass if we don't have a PRTS or if the context differs!
            termState = TermContext.build(context, term);
        } else {
            // PRTS was pre-build for this IS
            termState = this.perReaderTermState;
        }

        // we must not ignore the given docFreq - if set use the given value (lie)
        if (docFreq != -1)
            termState.setDocFreq(docFreq);

        return new TermBM25FWeight(searcher, termState);
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        terms.add(getTerm());
    }

    /**
     * Prints a user-readable version of this query.
     */
    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!term.field().equals(field)) {
            buffer.append(term.field());
            buffer.append(":");
        }
        buffer.append(term.text());
        buffer.append(ToStringUtils.boost(getBoost()));
        return buffer.toString();
    }

    /**
     * Returns true iff <code>o</code> is equal to this.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TermBM25FQuery))
            return false;
        TermBM25FQuery other = (TermBM25FQuery) o;
        return (this.getBoost() == other.getBoost())
                && this.term.equals(other.term);
    }

    /**
     * Returns a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Float.floatToIntBits(getBoost()) ^ term.hashCode();
    }

}
