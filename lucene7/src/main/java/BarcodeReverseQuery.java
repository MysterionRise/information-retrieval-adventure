import com.google.common.base.Stopwatch;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.reverse.ReverseStringFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NoLockFactory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Random;

/**
 * @see https://stackoverflow.com/q/53603726/2663985
 */
public class BarcodeReverseQuery {

    public static void main(String[] args) throws IOException, ParseException {

        Directory dir = new RAMDirectory();
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        Random r = new Random();

        final Tokenizer token = new KeywordTokenizer();

        for (int i = 0; i < 1000; ++i) {
            Document doc = new Document();
            final String value = String.valueOf(r.nextLong());
            token.setReader(new StringReader(value));
            doc.add(new TextField("barcode", value, Field.Store.YES));
            doc.add(new TextField("reverse-barcode", new ReverseStringFilter(token)));
            writer.addDocument(doc);
        }
        writer.close();

        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        final Stopwatch timer1 = new Stopwatch();
        final Stopwatch timer2 = new Stopwatch();

        for (int i = 0; i < 1000000; ++i) {

            final int digit = r.nextInt(10);

            timer1.start();

            Query q = new WildcardQuery(new Term("barcode", "*" + digit));

            TopDocs results = searcher.search(q, 10);
            final long res1 = results.totalHits;
            ScoreDoc[] scoreDocs = results.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                System.out.println(scoreDoc.doc + " " + reader.document(scoreDoc.doc) + " " + scoreDoc.score);
            }
            System.out.println("Hits: " + results.totalHits);
            System.out.println("Max score:" + results.getMaxScore());

            timer1.stop();

            System.out.println("------------------reverse filter--------------------------------------------------------");

            timer2.start();

            q = new WildcardQuery(new Term("reverse-barcode", digit + "*"));

            results = searcher.search(q, 10);
            final long res2 = results.totalHits;
            scoreDocs = results.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                System.out.println(scoreDoc.doc + " " + reader.document(scoreDoc.doc) + " " + scoreDoc.score);
            }
            System.out.println("Hits: " + results.totalHits);
            System.out.println("Max score:" + results.getMaxScore());

            timer2.stop();

            assert(res1 == res2);
        }

        System.out.println("leading wildcard = " + timer1.elapsedMillis());
        System.out.println("reverse wildcard = " + timer2.elapsedMillis());
    }
}
