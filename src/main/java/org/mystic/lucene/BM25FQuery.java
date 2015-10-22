package org.mystic.lucene;

import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;

import java.util.Collection;

/**
 * my implementation of BM25F approach for Lucene
 */
public class BM25FQuery extends DisjunctionMaxQuery {
    
    public BM25FQuery(Collection<Query> disjuncts, float tieBreakerMultiplier) {
        super(disjuncts, tieBreakerMultiplier);
    }
}
