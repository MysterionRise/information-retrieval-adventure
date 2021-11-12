package org.mystic;

import java.nio.file.Paths;
import java.util.Date;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/** @see http://stackoverflow.com/q/28556471/2663985 */
public class PresicionStepIndexing {

  private static final java.text.SimpleDateFormat DATE_PARSER =
      new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
  private static final String INDEX_PATH = "/tmp/lucene";

  private static Date parseDate(String dateSt) {
    synchronized (DATE_PARSER) {
      try {
        return DATE_PARSER.parse(dateSt);
      } catch (java.text.ParseException e) {
        throw new IllegalArgumentException(e);
      }
    }
  }

  public static void main(String[] args) {
    try {
      // Create an index
      Directory dir = FSDirectory.open(Paths.get(INDEX_PATH).toFile());
      Analyzer analyzer = new StandardAnalyzer();
      IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
      iwc.setOpenMode(OpenMode.CREATE);
      IndexWriter writer = new IndexWriter(dir, iwc);

      Document doc = new Document();
      doc.add(
          new LongField("BirthDate", parseDate("1989/11/01 10:11:12").getTime(), Field.Store.YES));
      writer.addDocument(doc);

      doc = new Document();
      doc.add(
          new LongField("BirthDate", parseDate("1973/03/02 13:14:15").getTime(), Field.Store.YES));
      writer.addDocument(doc);

      doc = new Document();
      final FieldType type = new FieldType();
      type.setNumericPrecisionStep(4);
      type.setStored(true);
      type.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
      type.setNumericType(FieldType.NumericType.LONG);
      doc.add(new LongField("BirthDate", parseDate("1969/01/31 16:17:18").getTime(), type));
      writer.addDocument(doc);

      writer.close();

      // Now do searching

      IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_PATH).toFile()));
      IndexSearcher searcher = new IndexSearcher(reader);

      Query query =
          NumericRangeQuery.newLongRange(
              "BirthDate",
              4,
              parseDate("1969/01/20 00:00:00").getTime(),
              parseDate("1973/03/03 00:00:00").getTime(),
              true,
              true);
      System.out.println("query: " + query);

      TopDocs results = searcher.search(query, null, 100);
      ScoreDoc[] scoreDocs = results.scoreDocs;
      int hits = scoreDocs.length;
      int count = results.totalHits;

      for (ScoreDoc scoreDoc : scoreDocs) {
        doc = searcher.doc(scoreDoc.doc);
        String value = doc.get("BirthDate");
        System.out.println(new Date(Long.parseLong(value)));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
