import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NoLockFactory;

/** @see https://stackoverflow.com/q/53327304/2663985 */
public class ReadOnlyIndex {

  public static void main(String[] args) throws IOException, ParseException {

    //        Directory dir = FSDirectory.open(Paths.get("/tmp/test"));
    //        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
    //        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    //        IndexWriter writer = new IndexWriter(dir, iwc);
    //
    //        Document doc = new Document();
    //        doc.add(new TextField("text", "bubble bloom is happening somewhere else",
    // Field.Store.YES));
    //        writer.addDocument(doc);
    //        doc = new Document();
    //        doc.add(new TextField("text", "here we have just bloom bloom like a spring",
    // Field.Store.YES));
    //        writer.addDocument(doc);
    //        doc = new Document();
    //        doc.add(new TextField("text", "i would be curious how bubble tea is bubble enough for
    // me", Field.Store.YES));
    //        writer.addDocument(doc);
    //        doc = new Document();
    //        doc.add(new TextField("text", "i would be curious how bubble tea is bubble enough for
    // me. Should I have more bubble ?", Field.Store.YES));
    //        writer.addDocument(doc);
    //        writer.close();

    Directory index = FSDirectory.open(Paths.get("/tmp/test"), NoLockFactory.INSTANCE);
    IndexReader reader = DirectoryReader.open(index);

    IndexSearcher searcher = new IndexSearcher(reader);
    Query query = new MatchAllDocsQuery();

    TopDocs results = searcher.search(query, 5);
    final ScoreDoc[] scoreDocs = results.scoreDocs;
    for (ScoreDoc scoreDoc : scoreDocs) {
      System.out.println(scoreDoc.doc + " " + reader.document(scoreDoc.doc) + " " + scoreDoc.score);
    }
    System.out.println("Hits: " + results.totalHits);
    System.out.println("Max score:" + results.getMaxScore());
  }
}
