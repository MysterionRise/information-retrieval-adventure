package org.mystic;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SuffixShingleTokenFilter extends TokenFilter {

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private String[] tokens;
    private int index = 0;

    public SuffixShingleTokenFilter(TokenStream input) {
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (true) {
            if (tokens == null) {
                if (!input.incrementToken()) {
                    return false;
                } else {
                    final CharTermAttribute attribute = input.getAttribute(CharTermAttribute.class);
                    tokens = attribute.toString().split(" ");
                }
            }
            if (tokens.length - 1 - index < 0)
                return false;
            final String suffix =
                    IntStream.range(index, tokens.length).mapToObj(i -> tokens[i]).collect(Collectors.joining(" "));
            termAtt.copyBuffer(suffix.toCharArray(), 0, suffix.length());
            index += 1;
            return true;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokens = null;
        index = 0;
    }
}
