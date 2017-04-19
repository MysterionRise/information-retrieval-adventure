import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.CharsRefBuilder;

import java.io.IOException;
import java.io.StringReader;

/**
 * @see http://stackoverflow.com/q/42378027/2663985
 */
public class SynonymGraphFilterTest {

  private static SynonymMap.Builder builder = new SynonymMap.Builder(true);
  private static Tokenizer tokensIn;
  private static SynonymGraphFilter tokensOut;
  private static CharTermAttribute termAtt;
  private static PositionIncrementAttribute posIncrAtt;
  private static PositionLengthAttribute posLenAtt;
  private static OffsetAttribute offsetAtt;

  private static void verify(String input, String output) throws Exception {

    System.out.println("TEST: verify input=" + input + " expectedOutput=" + output);

    tokensIn.setReader(new StringReader(input));
    tokensOut.reset();
    final String[] expected = output.split(" ");
    int expectedUpto = 0;
    while (tokensOut.incrementToken()) {

      System.out.println(
          "  incr token=" + termAtt.toString() + " posIncr=" + posIncrAtt.getPositionIncrement() + " startOff="
              + offsetAtt.startOffset() + " endOff=" + offsetAtt.endOffset());

      final int startOffset = offsetAtt.startOffset();
      final int endOffset = offsetAtt.endOffset();

      final String[] expectedAtPos = expected[expectedUpto++].split("/");
      for (int atPos = 0; atPos < expectedAtPos.length; atPos++) {
        if (atPos > 0) {
          tokensOut.incrementToken();

          System.out.println(
              "  incr token=" + termAtt.toString() + " posIncr=" + posIncrAtt.getPositionIncrement() + " startOff="
                  + offsetAtt.startOffset() + " endOff=" + offsetAtt.endOffset());

        }
        final int colonIndex = expectedAtPos[atPos].indexOf(':');
        final int underbarIndex = expectedAtPos[atPos].indexOf('_');
      }
    }
    tokensOut.end();
    tokensOut.close();
    System.out.println("  incr: END");
  }


  public static void main(String[] args) throws Exception {
    add("a", "foo", true);
    add("a b", "bar fee", true);
    add("b c", "dog collar", true);
    add("c d", "dog harness holder extras", true);
    add("m c e", "dog barks loudly", false);
    add("i j k", "feep", true);

    add("e f", "foo bar", false);
    add("e f", "baz bee", false);

    add("z", "boo", false);
    add("y", "bee", true);

    tokensIn = new WhitespaceTokenizer();
    tokensIn.setReader(new StringReader("a"));
    tokensIn.reset();
    tokensIn.incrementToken();
    tokensIn.end();
    tokensIn.close();

    tokensOut = new SynonymGraphFilter(tokensIn,
        builder.build(),
        true);
    termAtt = tokensOut.addAttribute(CharTermAttribute.class);
    posIncrAtt = tokensOut.addAttribute(PositionIncrementAttribute.class);
    posLenAtt = tokensOut.addAttribute(PositionLengthAttribute.class);
    offsetAtt = tokensOut.addAttribute(OffsetAttribute.class);

    verify("a b c", "a/bar b/fee c");

    // syn output extends beyond input tokens
    verify("x a b c d", "x a/bar b/fee c/dog d/harness holder extras");

    verify("a b a", "a/bar b/fee a/foo");

    // outputs that add to one another:
    verify("c d c d", "c/dog d/harness c/holder/dog d/extras/harness holder extras");

    // two outputs for same input
    verify("e f", "foo/baz bar/bee");

    // verify multi-word / single-output offsets:
    verify("g i j k g", "g i/feep:7_3 j k g");

    // mixed keepOrig true/false:
    verify("a m c e x", "a/foo dog barks loudly x");
    verify("c d m c e x", "c/dog d/harness holder/dog extras/barks loudly x");
  }

  private static void add(String input, String output, boolean keepOrig) {
    System.out.println("  add input=" + input + " output=" + output + " keepOrig=" + keepOrig);

    CharsRefBuilder inputCharsRef = new CharsRefBuilder();
    SynonymMap.Builder.join(input.split(" +"), inputCharsRef);

    CharsRefBuilder outputCharsRef = new CharsRefBuilder();
    SynonymMap.Builder.join(output.split(" +"), outputCharsRef);

    builder.add(inputCharsRef.get(), outputCharsRef.get(), keepOrig);
  }
}
