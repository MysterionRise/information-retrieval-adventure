package org.mystic;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SuffixShingleTokenFilter extends TokenFilter {

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
            final StringBuilder sb = new StringBuilder();
            for (int i = index; i < tokens.length; i++) {
                sb.append(tokens[i]);
                sb.append(" ");
            }
            termAtt.copyBuffer(sb.toString().trim().toCharArray(), 0, sb.toString().trim().length());
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
