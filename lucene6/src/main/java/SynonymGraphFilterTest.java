import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermAutomatonQuery;
import org.apache.lucene.search.TokenStreamToTermAutomatonQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
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
        TokenStream tokenStream = getTokenStream(input);
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
            System.out.println(termAtt.toString());
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

    private static TokenStream getTokenStream(String input) throws IOException {
        Tokenizer inputStream = new WhitespaceTokenizer();
        inputStream.setReader(new StringReader(input));

        return new SynonymGraphFilter(inputStream,
                builder.build(),
                true);
    }


    public static void main(String[] args) throws Exception {
        add("wedding trio", "bridal set", true);
        add("wedding trio sets", "3-stone rings", true);

        System.out.println(getGraph("wedding trio sets"));

        Directory dir = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        Document doc = new Document();
        doc.add(new TextField("text", "calvin klein jeans", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "calvin klein pants", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "ralph loren", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "ralph loren pants", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "ralph loren jeans", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "ralph loren trousers", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        IndexReader reader = DirectoryReader.open(dir);

        IndexSearcher searcher = new IndexSearcher(reader);

        TokenStreamToTermAutomatonQuery q = new TokenStreamToTermAutomatonQuery();
        final TermAutomatonQuery query = q.toQuery("text", getTokenStream("calvin klein jeans"));
        final TopDocs response = searcher.search(query, 10);

        ScoreDoc[] scoreDocs = response.scoreDocs;
        for (int i = 0; i < scoreDocs.length; ++i) {
            System.out.println(searcher.doc(scoreDocs[i].doc));
            System.out.println(scoreDocs[i].doc + " " + scoreDocs[i].score);
        }
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
