import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.CharsRefBuilder;

import java.io.IOException;
import java.io.StringReader;

/**
 * @see https://issues.apache.org/jira/browse/LUCENE-6664
 */
public class ShingleFilterTest {

  public static void main(String[] args) throws IOException {

    String theSentence = "teh big sofa";
    StringReader reader = new StringReader(theSentence);
    Tokenizer whitespaceTokenizer = new WhitespaceTokenizer();
    whitespaceTokenizer.setReader(reader);
    Tokenizer tokenizer = new StandardTokenizer();

    String sofa = "big sofa";
    String divan = "small divan";

    SynonymMap.Builder builder = new SynonymMap.Builder(true);

    CharsRef input = SynonymMap.Builder.join(sofa.split(" "), new CharsRefBuilder());
    CharsRef output = SynonymMap.Builder.join(divan.split(" "), new CharsRefBuilder());

    builder.add(output, input, true);

    TokenStream tokenStream = new SynonymGraphFilter(tokenizer, builder.build(), true);

    final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
//    tokenStream.reset();

    while (tokenStream.incrementToken()) {
      System.out.println(charTermAttribute.toString());
    }

    tokenStream.end();
    tokenStream.close();
  }
}
