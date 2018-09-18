import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/** @see https://stackoverflow.com/q/45997270/2663985 */
public class PermissionFiltering {

  public static void main(String[] args) throws IOException {

    Directory dir = new RAMDirectory();
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    addDocument(writer, "open post", "user");
    addDocument(writer, "secured post", "super-user");
    addDocument(writer, "very secured post", "admin");

    writer.close();

    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);

    Query query =
        new BooleanQuery.Builder()
            .add(new BooleanClause(new TermQuery(new Term("*:*")), Occur.MUST))
            .add(new BooleanClause(new TermQuery(new Term("id", "admin")), Occur.FILTER))
            .build();

    final ScoreDoc[] scoreDocs = searcher.search(query, 10).scoreDocs;
    for (ScoreDoc doc : scoreDocs) {
      System.out.println(doc.doc + " " + doc.score);
    }
  }

  private static void addDocument(IndexWriter writer, String title, String id) throws IOException {
    Document doc = new Document();
    doc.add(new TextField("title", title, Store.YES));
    doc.add(new StringField("id", id, Store.YES));
    writer.addDocument(doc);
  }
}
