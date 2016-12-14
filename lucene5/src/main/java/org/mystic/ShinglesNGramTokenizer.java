package org.mystic;

import org.apache.lucene.analysis.util.CharTokenizer;

public class ShinglesNGramTokenizer extends CharTokenizer {

    @Override
    protected boolean isTokenChar(int c) {
        return !Character.isWhitespace(c);
    }
}
