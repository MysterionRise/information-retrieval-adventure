import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.CharsRefBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class EntradaSalidaTest extends BaseTokenStreamTestCase {


    @Test
    public void testSynonyms() throws Exception {
        String entrada = "ALCALDE KOOPER";
        String salida = "FEDERICO KOOPER";

        SynonymMap.Builder builder = new SynonymMap.Builder(true);

        CharsRef input = SynonymMap.Builder.join(entrada.split(" "), new CharsRefBuilder());
        CharsRef output = SynonymMap.Builder.join(salida.split(" "), new CharsRefBuilder());


        builder.add(input, output, true);

        SuggestAnalizer suggestAnalizer = new SuggestAnalizer(builder.build());

        Analyzer.TokenStreamComponents components = suggestAnalizer.createComponents(entrada);
        final TokenStream tokenStream = components.getTokenStream();

        CharTermAttribute termAtt = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();

        while (tokenStream.incrementToken()) {
//            if
            System.out.println(termAtt.toString());

        }
        tokenStream.end();
        tokenStream.close();

//        assertTokenStreamContents(tokenStream, new String[]{
//                "FEDERICO"
//        });
//
//        assertAnalyzesTo(suggestAnalizer, entrada, new String[]{
//                "FEDERICO"
//        });
    }

    private class SuggestAnalizer extends Analyzer {


        private final SynonymMap synonymMap;
        private final List<Object> stopList;

        public SuggestAnalizer(SynonymMap synonymMap) {
            this.synonymMap = synonymMap;
            this.stopList = Collections.emptyList();
        }


        @Override
        protected Analyzer.TokenStreamComponents createComponents(String s) {

            Tokenizer tokenizer = new StandardTokenizer();
            TokenStream tokenStream = new SynonymGraphFilter(tokenizer, synonymMap, true);
            TokenStream tokenStream1 = new FlattenGraphFilter(tokenStream);
            return new Analyzer.TokenStreamComponents(tokenizer, tokenStream1);
        }
    }
}
