import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/** @see https://stackoverflow.com/q/45822066/2663985 */
public class IndexTimeScoringFactors {

  /**
   * Since Lucene 6.6.0 the index time boosting has been deprecated. How we suppose to solve it now?
   */
  public static void main(String[] args) throws IOException {

    Directory dir = new RAMDirectory();
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    Document doc1 = new Document();
    doc1.add(new TextField("title", "The biggest title in the world", Store.YES));
    doc1.add(new TextField("description", "short descr", Store.YES));
    doc1.add(new FloatDocValuesField("doc_boost", 1.30f));
    writer.addDocument(doc1);

    Document doc2 = new Document();
    doc2.add(new TextField("title", "Not so important title", Store.YES));
    doc1.add(new TextField("description", "very valuable descr", Store.YES));
    doc1.add(new FloatDocValuesField("doc_boost", 3.30f));
    writer.addDocument(doc2);

    writer.close();

    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);

    Query query = new MatchAllDocsQuery();
    Query q = new FunctionScoreQuery(query, DoubleValuesSource.fromFloatField("doc_boost"));

    final ScoreDoc[] scoreDocs = searcher.search(q, 10).scoreDocs;
    for (ScoreDoc doc : scoreDocs) {
      System.out.println(doc.doc + " " + doc.score);
    }
  }
}
