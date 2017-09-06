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

/** @see http://stackoverflow.com/q/41461140/2663985 */
public class BoostBeginningWithTest {

  public static void main(String[] args) throws IOException {
    Directory dir = new RAMDirectory();
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    Document doc = new Document();
    doc.add(new TextField("text", "kartnik", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "aakash", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "kaartnik", Field.Store.YES));
    writer.addDocument(doc);
    writer.close();

    IndexReader reader = DirectoryReader.open(dir);

    IndexSearcher searcher = new IndexSearcher(reader);

    MultiTermQuery query = new WildcardQuery(new Term("text", "*ka*"));
    query.setRewriteMethod(new BoostPrefixScoringRewrite("*ka*"));
    System.out.println("query: " + query);
    System.out.println("------------------------------");

    TopDocs results = searcher.search(query, 100);
    ScoreDoc[] scoreDocs = results.scoreDocs;
    for (int i = 0; i < scoreDocs.length; ++i) {
      final String[] texts = reader.document(scoreDocs[i].doc).getValues("text");
      for (String token : texts) System.out.println(token);
      System.out.println(searcher.explain(query, scoreDocs[i].doc));
    }
  }
}
