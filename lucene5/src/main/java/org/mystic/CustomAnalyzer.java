package org.mystic;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardFilter;

/** @see http://stackoverflow.com/q/38682588/2663985 */
public class CustomAnalyzer extends Analyzer {

  @Override
  protected TokenStreamComponents createComponents(String s) {
    // provide your own tokenizer, that will split input string as you want it
    final Tokenizer standardTokenizer = new MyStandardTokenizer();

    TokenStream tok = new StandardFilter(standardTokenizer);
    // make everything lowercase, remove if not needed
    tok = new LowerCaseFilter(tok);
    // provide stopwords if you want them
    //        tok = new StopFilter(tok, stopwords);
    return new TokenStreamComponents(standardTokenizer, tok);
  }

  private class MyStandardTokenizer extends Tokenizer {

    protected MyStandardTokenizer() {
      super();
    }

    public boolean incrementToken() {
      // add your logic
      return false;
    }
  }
}
