package org.mystic;

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
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/** @see http://stackoverflow.com/q/38833256/2663985 */
public class URLAnalyzerTest {

  public static void main(String[] args) throws IOException, ParseException {
    Directory dir = new RAMDirectory();
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
    iwc.setOpenMode(OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    Document doc = new Document();
    doc.add(new TextField("url", "http://google.com", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(
        new TextField(
            "url",
            "http://stackoverflow.com/questions/38833256/how-to-index-and-query-urls-with-solr",
            Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("url", "https://google.com/auth/bla/bla?key=value", Field.Store.YES));
    writer.addDocument(doc);
    writer.close();

    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);

    QueryParser queryParser = new QueryParser("url", new StandardAnalyzer());
    final Query query = queryParser.parse("http://google.com");
    System.out.printf(query.toString());

    TopDocs results = searcher.search(query, null, 100);
    System.out.println("Found: " + results.totalHits);
    ScoreDoc[] scoreDocs = results.scoreDocs;
    for (int i = 0; i < scoreDocs.length; ++i) {
      System.out.println(reader.document(scoreDocs[i].doc).getField("url"));
    }
  }
}
