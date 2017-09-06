import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.pattern.PatternTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

public class CommaTokenizer {

  public static void main(String[] args) throws IOException {

    Directory dir = new RAMDirectory();
    Analyzer analyzer =
        new Analyzer() {
          @Override
          protected TokenStreamComponents createComponents(String fieldName) {
            Tokenizer source = new PatternTokenizer(Pattern.compile("\\,"), -1);
            return new TokenStreamComponents(source);
          }
        };
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    Document doc = new Document();
    doc.add(new TextField("text", "Age 6, Age 7, Age 8", Field.Store.YES));
    writer.addDocument(doc);
    writer.close();

    IndexReader reader = DirectoryReader.open(dir);
    final Fields fields = MultiFields.getFields(reader);
    final Iterator<String> iterator = fields.iterator();

    while (iterator.hasNext()) {
      final String field = iterator.next();
      final Terms terms = MultiFields.getTerms(reader, field);
      final TermsEnum it = terms.iterator();
      BytesRef term = it.next();
      while (term != null) {
        System.out.println(term.utf8ToString());
        term = it.next();
      }
    }
  }
}
