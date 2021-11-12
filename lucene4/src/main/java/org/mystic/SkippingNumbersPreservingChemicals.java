package org.mystic;

import java.io.IOException;
import java.io.StringReader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.FilteringTokenFilter;

/** @see https://stackoverflow.com/q/46050874/2663985 */
public class SkippingNumbersPreservingChemicals {

  public static void main(String[] args) throws IOException {

    String theSentence =
        "this is the scientific article about chemicals like H20 C2H50H with concentration "
            + "of 3.99 kilograms and 0,123 micrograms also i have some CO2 gas n=3 x=45";
    StringReader reader = new StringReader(theSentence);
    Tokenizer whitespaceTokenizer = new WhitespaceTokenizer(reader);
    TokenStream tokenStream =
        new StopFilter(whitespaceTokenizer, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    tokenStream = new ScientificFiltering(tokenStream);

    final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
    tokenStream.reset();

    while (tokenStream.incrementToken()) {
      System.out.println(charTermAttribute.toString());
    }

    tokenStream.end();
    tokenStream.close();
  }

  static class ScientificFiltering extends FilteringTokenFilter {

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    public ScientificFiltering(TokenStream in) {
      super(in);
    }

    @Override
    protected boolean accept() {
      String token = new String(termAtt.buffer(), 0, termAtt.length());
      return !token.matches("[0-9,.]+");
    }
  }
}
