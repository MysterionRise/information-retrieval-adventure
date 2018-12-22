import com.ibm.icu.text.Collator;
import com.ibm.icu.util.ULocale;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.collation.ICUCollationDocValuesField;
import org.apache.lucene.collation.ICUCollationKeyAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
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

    for (int i = 0; i < data.length; ++i) {
      final Document doc = new Document();
      doc.add(new TextField("z", data[i], Field.Store.YES));
      contents.setStringValue(data[i]);
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
    for (int i = 0; i < result.length; ++i) {
      Document doc = searcher.doc(result[i].doc);
      System.out.println(doc);
    }
  }
}
