package org.mystic;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/** @see http://stackoverflow.com/q/29945985/2663985 */
public class WildcardQueryWithSpace {

  public static void main(String[] args) throws IOException {
    Directory dir = new RAMDirectory();
    Analyzer analyzer = new KeywordAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    Document doc = new Document();
    doc.add(new TextField("text", "san diego", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "sandales", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "san antonio", Field.Store.YES));
    writer.addDocument(doc);
    writer.close();

    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);

    Term term = new Term("text", QueryParser.escape("san ") + "*");
    WildcardQuery wildcardQuery = new WildcardQuery(term);
    System.out.println("query: " + wildcardQuery);

    TopDocs results = searcher.search(wildcardQuery, null, 100);
    ScoreDoc[] scoreDocs = results.scoreDocs;
    for (int i = 0; i < scoreDocs.length; ++i) {
      System.out.println(scoreDocs[i].doc);
      System.out.println(searcher.explain(wildcardQuery, scoreDocs[i].doc));
    }
  }
}
