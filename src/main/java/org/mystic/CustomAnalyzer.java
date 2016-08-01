package org.mystic;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;

import java.io.IOException;
import java.io.Reader;

/**
 * @see http://stackoverflow.com/q/38682588/2663985
 */
public class CustomAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String s, Reader reader) {
        // provide your own tokenizer, that will split input string as you want it
        final Tokenizer standardTokenizer = new MyStandardTokenizer(reader);

        TokenStream tok = new StandardFilter(standardTokenizer);
        // make everything lowercase, remove if not needed
        tok = new LowerCaseFilter(tok);
        //provide stopwords if you want them
//        tok = new StopFilter(tok, stopwords);
        return new TokenStreamComponents(standardTokenizer, tok);
    }

    private class MyStandardTokenizer extends Tokenizer {

        protected MyStandardTokenizer(Reader input) {
            super(input);
        }

        public boolean incrementToken() throws IOException {
            //add your logic
            return false;
        }
    }
}
