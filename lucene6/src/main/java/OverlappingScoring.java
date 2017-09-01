import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermInSetQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

/**
 * @see https://stackoverflow.com/q/45874420/2663985
 */
public class OverlappingScoring {

    public static void main(String[] args) throws IOException, ParseException {

        Directory dir = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        Document doc = new Document();
        doc.add(new TextField("text", "system engineering artificial intelligence", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "architecture logic programming", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "sytem architecture engineering", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        IndexReader reader = DirectoryReader.open(dir);

        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new TermInSetQuery("text", new BytesRef("system"), new BytesRef("architecture"));

        final ScoreDoc[] scoreDocs = searcher.search(query, 10).scoreDocs;
        for (ScoreDoc d: scoreDocs) {
            System.out.println(d.doc + " " + d.score);
        }


    }
}
