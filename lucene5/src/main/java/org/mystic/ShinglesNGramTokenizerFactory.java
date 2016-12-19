package org.mystic;


import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

import java.util.Map;

public class ShinglesNGramTokenizerFactory extends TokenizerFactory {

    public ShinglesNGramTokenizerFactory(Map<String, String> args) {
        super(args);
        System.out.println(args);
    }

    @Override
    public Tokenizer create(AttributeFactory factory) {
        return new ShinglesNGramTokenizer();
    }
}
