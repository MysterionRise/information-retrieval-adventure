import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.util.CharsRefBuilder;

import java.io.IOException;
import java.io.StringReader;

public class SynonymGraphFilterTest {

    private static SynonymMap.Builder builder = new SynonymMap.Builder(true);

    /**
     * Now the graph is more interesting!
     * For each token (arc), the PositionIncrementAttribute tells us how many positions
     * (nodes) ahead this arc starts from, while the new (as of 3.6.0)
     * PositionLengthAttribute tells us how many positions
     * (nodes) ahead the arc arrives to.
     */

    private static String getGraph(String input) throws IOException {
        Tokenizer inputStream = new WhitespaceTokenizer();
        inputStream.setReader(new StringReader(input));

        TokenStream tokenStream = new SynonymGraphFilter(inputStream,
                builder.build(),
                true);
        PositionIncrementAttribute posIncAtt = tokenStream.addAttribute(PositionIncrementAttribute.class);
        PositionLengthAttribute posLenAtt = tokenStream.addAttribute(PositionLengthAttribute.class);
        CharTermAttribute termAtt = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        int srcNode = -1;
        int destNode;

        StringBuilder b = new StringBuilder();
        b.append("digraph Automaton {\n");
        b.append("  initial [shape=plaintext,label=\"\"]\n");
        b.append("  initial -> 0\n");

        while (tokenStream.incrementToken()) {
            int posInc = posIncAtt.getPositionIncrement();
            if (posInc != 0) {
                srcNode += posInc;
                b.append("  ");
                b.append(srcNode);
                b.append(" [shape=circle,label=\"" + srcNode + "\"]\n");
            }
            destNode = srcNode + posLenAtt.getPositionLength();
            b.append("  ");
            b.append(srcNode);
            b.append(" -> ");
            b.append(destNode);
            b.append(" [label=\"");
            b.append(termAtt);
            b.append("\"");
            b.append("]\n");
        }
        tokenStream.end();
        tokenStream.close();

        b.append('}');
        return b.toString();
    }


    public static void main(String[] args) throws Exception {
        add("teh", "the", true);
        add("calvin klein", "ralph loren", true);
        add("jeans", "pants", true);
        add("jeans", "trousers", true);

        System.out.println(getGraph("teh silver calvin klein jeans"));
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
