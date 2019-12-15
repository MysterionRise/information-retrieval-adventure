import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

/** @see https://stackoverflow.com/q/53374492/2663985 */
public class FacetMissingLucene {

  public static void main(String[] args) throws IOException, ParseException {

    Directory dir =
        new MMapDirectory(Files.createTempDirectory(FacetMissingLucene.class.getName()));
    IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    Document doc = new Document();
    doc.add(new TextField("text", "bubble bloom is happening somewhere else", Field.Store.YES));
    doc.add(new TextField("category", "A", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "bubble bloom is happening", Field.Store.YES));
    doc.add(new TextField("category", "B", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "something else", Field.Store.YES));
    doc.add(new TextField("category", "A", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "almost empty", Field.Store.YES));
    writer.addDocument(doc);
    writer.close();

    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);

    Query q = new MatchAllDocsQuery();
    Set<Integer> queryRes = new HashSet<>();
    searcher.search(q, new CheckHits.SetCollector(queryRes));

    Set<Integer> missingRes = new HashSet<>();
    searcher.search(
        new TermRangeQuery("category", null, null, false, false),
        new CheckHits.SetCollector(missingRes));
    missingRes.retainAll(queryRes);

    System.out.println(missingRes.size());
  }
}
