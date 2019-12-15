package org.mystic;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;

/** @see http://stackoverflow.com/q/38682588/2663985 */
public class CustomAnalyzer extends Analyzer {

  @Override
  protected TokenStreamComponents createComponents(String s) {
    ArrayList stops = new ArrayList(EnglishAnalyzer.getDefaultStopSet());
    ArrayList itaStops = new ArrayList(ItalianAnalyzer.getDefaultStopSet());
    stops.addAll(itaStops);
    CharArraySet sw = StopFilter.makeStopSet(stops);
    Tokenizer source = new StandardTokenizer();
    TokenStream filter = new LowerCaseFilter(source);
    filter = new StopFilter(filter, sw);
    filter = new PorterStemFilter(filter);
    return new TokenStreamComponents(source, filter);
//    // provide your own tokenizer, that will split input string as you want it
//    final Tokenizer standardTokenizer = new MyStandardTokenizer();
//
//    TokenStream tok = new StandardFilter(standardTokenizer);
//    // make everything lowercase, remove if not needed
//    tok = new LowerCaseFilter(tok);
//    //provide stopwords if you want them
//    //        tok = new StopFilter(tok, stopwords);
//    return new TokenStreamComponents(standardTokenizer, tok);
  }

  private class MyStandardTokenizer extends Tokenizer {

    protected MyStandardTokenizer() {
      super();
    }

    public boolean incrementToken() throws IOException {
      //add your logic
      return false;
    }
  }
}
