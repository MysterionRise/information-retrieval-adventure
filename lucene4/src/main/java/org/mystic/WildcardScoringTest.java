package org.mystic;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/** Created by kperikov on 09.01.17. */
public class WildcardScoringTest {

  public static void main(String[] args) throws IOException {
    Directory dir = new RAMDirectory();
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    Document doc = new Document();
    doc.add(new TextField("text", "katrin", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "akatrin", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "cfkatrin", Field.Store.YES));
    writer.addDocument(doc);
    writer.close();

    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);

    MultiTermQuery query = new WildcardQuery(new Term("text", "*ka*"));
    query.setRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_QUERY_REWRITE);
    System.out.println("query: " + query);

    TopDocs results = searcher.search(query, null, 100);
    ScoreDoc[] scoreDocs = results.scoreDocs;
    for (ScoreDoc scoreDoc : scoreDocs) {
      System.out.println(searcher.explain(query, scoreDoc.doc));
    }
  }
}
