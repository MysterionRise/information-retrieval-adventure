package org.mystic.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

import java.io.IOException;
import java.io.StringReader;

/**
 * @see http://stackoverflow.com/q/31957986/2663985
 */
public class StopWordsStemmingAnalyzer {

    public static void main(String[] args) throws IOException {

        String theSentence = "this is a mouse more mouses and thing things thoughts think thinks thinked";
        StringReader reader = new StringReader(theSentence);
        Tokenizer whitespaceTokenizer = new WhitespaceTokenizer();
        whitespaceTokenizer.setReader(reader);
        TokenStream tokenStream = new StopFilter(whitespaceTokenizer, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
        tokenStream = new PorterStemFilter(tokenStream);

        final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();

        while (tokenStream.incrementToken()) {
            System.out.println(charTermAttribute.toString());
        }

        tokenStream.end();
        tokenStream.close();
    }
}
