import com.ibm.icu.text.Collator;
import com.ibm.icu.util.ULocale;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.collation.ICUCollationDocValuesField;
import org.apache.lucene.collation.ICUCollationKeyAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;

/** @see https://stackoverflow.com/q/53438426/2663985 */
public class GermanUmlautsSorting {

  public static void main(String[] args) throws IOException, ParseException {

    final Collator instance = Collator.getInstance(ULocale.GERMAN);
    Analyzer analyzer = new ICUCollationKeyAnalyzer(instance);
    RAMDirectory indexStore = new RAMDirectory();
    IndexWriter writer = new IndexWriter(indexStore, new IndexWriterConfig(analyzer));

    String[] data = new String[] {"Ü", "Z", "ä", "ö", "o", "Ö", "O", "ß", "s", "a", "U"};

    final ICUCollationDocValuesField contents =
        new ICUCollationDocValuesField("contents", instance);

    for (String datum : data) {
      final Document doc = new Document();
      doc.add(new TextField("z", datum, Field.Store.YES));
      contents.setStringValue(datum);
      doc.add(contents);
      writer.addDocument(doc);
    }

    writer.close();
    IndexReader reader = DirectoryReader.open(indexStore);
    IndexSearcher searcher = new IndexSearcher(reader);
    Sort sort = new Sort();
    sort.setSort(new SortField("contents", SortField.Type.STRING));
    Query query = new MatchAllDocsQuery();
    ScoreDoc[] result = searcher.search(query, 1000, sort).scoreDocs;
    for (ScoreDoc scoreDoc : result) {
      Document doc = searcher.doc(scoreDoc.doc);
      System.out.println(doc);
    }
  }
}
