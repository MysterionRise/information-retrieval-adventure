package org.mystic;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.*;

public class BoostPrefixScoringRewrite extends ScoringRewrite<BooleanQuery.Builder> {

    private final String text;

    public BoostPrefixScoringRewrite(String text) {
        // todo should be handled more carefully, since wildcard query supports other than * symbols
        this.text = text.replace("*", "");
    }

    @Override
    protected BooleanQuery.Builder getTopLevelBuilder() {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.setDisableCoord(true);
        return builder;
    }

    protected Query build(BooleanQuery.Builder builder) {
        return builder.build();
    }

    @Override
    protected void addClause(BooleanQuery.Builder topLevel, Term term, int docCount,
                             float boost, TermContext states) {
        final TermQuery tq = new TermQuery(term, states);
        if (term.text().startsWith(this.text)) {
            // experiment with the boost value
            topLevel.add(new BoostQuery(tq, 100f), BooleanClause.Occur.SHOULD);
        } else {
            topLevel.add(new BoostQuery(tq, boost), BooleanClause.Occur.SHOULD);
        }

    }

    @Override
    protected void checkMaxClauseCount(int count) {
        if (count > BooleanQuery.getMaxClauseCount())
            throw new BooleanQuery.TooManyClauses();
    }
}