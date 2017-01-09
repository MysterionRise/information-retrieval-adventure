package org.mystic;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;

public class SuffixShingleFactory extends TokenFilterFactory {

    public SuffixShingleFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new SuffixShingleTokenFilter(input);
    }
}
