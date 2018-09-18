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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/** @see http://stackoverflow.com/q/42856949/2663985 */
public class PrefixQueryParsing {

  public static void main(String[] args) throws IOException, ParseException {

    Directory dir = new RAMDirectory();
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    Document doc = new Document();
    doc.add(new TextField("text", "cars text text", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "car some text ca", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "carmaggeddon some text", Field.Store.YES));
    writer.addDocument(doc);
    writer.close();

    IndexReader reader = DirectoryReader.open(dir);

    IndexSearcher searcher = new IndexSearcher(reader);

    final QueryParser queryParser = new QueryParser("text", new StandardAnalyzer());
    final Query q1 = queryParser.parse("car*");
    System.out.println(q1.rewrite(reader));
    final Query q2 = queryParser.parse("car");
    System.out.println(q2.rewrite(reader));
    System.out.println("car*:" + searcher.search(q1, 100).scoreDocs.length);
    System.out.println("car:" + searcher.search(q2, 100).scoreDocs.length);
  }
}
