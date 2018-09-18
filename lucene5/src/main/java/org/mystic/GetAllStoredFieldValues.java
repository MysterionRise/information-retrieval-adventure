package org.mystic;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/** @see http://stackoverflow.com/q/38619549/2663985 */
public class GetAllStoredFieldValues {

  public static void main(String[] args) throws IOException {
    Directory dir = new RAMDirectory();
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    Document doc = new Document();
    doc.add(new TextField("text", "muffin cat", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "zmuffin cat", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "mufffin black cat", Field.Store.YES));
    writer.addDocument(doc);
    writer.close();

    IndexReader reader = DirectoryReader.open(dir);

    final int len = reader.maxDoc();
    for (int i = 0; i < len; ++i) {
      Document document = reader.document(i);
      List<IndexableField> fields = document.getFields();
      for (IndexableField field : fields) {
        if (field.fieldType().stored()) {
          System.out.println(field.stringValue());
        }
      }
    }
    IndexSearcher searcher = new IndexSearcher(reader);
    TermQuery query = new TermQuery(new Term("text", "cat"));
    System.out.println("query: " + query);

    TopDocs results = searcher.search(query, null, 100);
    ScoreDoc[] scoreDocs = results.scoreDocs;
    for (int i = 0; i < scoreDocs.length; ++i) {
      System.out.println(searcher.explain(query, scoreDocs[i].doc));
    }
  }
}
