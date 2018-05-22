import static org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter.*;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.pattern.PatternReplaceCharFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/** @see https://stackoverflow.com/q/49393645/2663985 */
public class SpecialCharactersSearch {

  static class CustomAnalyzer extends Analyzer {

    @Override
    protected Reader initReader(String fieldName, Reader reader) {
      CharFilter cf = new PatternReplaceCharFilter(Pattern.compile("\\["), "", reader);
      cf = new PatternReplaceCharFilter(Pattern.compile("\\]"), "", cf);
      cf = new PatternReplaceCharFilter(Pattern.compile("\\)"), "", cf);
      cf = new PatternReplaceCharFilter(Pattern.compile("\\("), "", cf);
      return cf;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
      final StandardTokenizer analyzer = new StandardTokenizer();
      TokenStream tok = new StandardFilter(analyzer);
      tok = new LowerCaseFilter(tok);
      return new TokenStreamComponents(analyzer, tok);
    }
  }

  public static void main(String[] args) throws IOException, ParseException {
    Directory dir = new RAMDirectory();
    IndexWriterConfig iwc = new IndexWriterConfig(new CustomAnalyzer());
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    Document doc = new Document();
    doc.add(new TextField("text", "otherwise i want to do so", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "[otherwise i would be glad to do so", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "(otherwise", Field.Store.YES));
    writer.addDocument(doc);
    writer.close();

    IndexReader reader = DirectoryReader.open(dir);

    IndexSearcher searcher = new IndexSearcher(reader);

    Query query = new TermQuery(new Term("text", "otherwise"));

    TopDocs results = searcher.search(query, 5);
    final ScoreDoc[] scoreDocs = results.scoreDocs;
    for (ScoreDoc scoreDoc : scoreDocs) {
      System.out.println(scoreDoc.doc + " " + scoreDoc.score);
    }
    System.out.println("Hits: " + results.totalHits);
    System.out.println("Max score:" + results.getMaxScore());
  }
}
