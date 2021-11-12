package org.mystic;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class IndexOutput {

  public static void main(String[] args) throws IOException {

    Directory dir = new RAMDirectory();
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    Document doc = new Document();
    doc.add(new TextField("text", "first second third", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "second third", Field.Store.YES));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "third", Field.Store.YES));
    writer.addDocument(doc);
    writer.close();

    IndexReader reader = DirectoryReader.open(dir);
    final Fields fields = MultiFields.getFields(reader);

    for (String field : fields) {
      final Terms terms = MultiFields.getTerms(reader, field);
      final TermsEnum it = terms.iterator(null);
      BytesRef term = it.next();
      while (term != null) {
        System.out.println(term.utf8ToString());
        term = it.next();
      }
    }
  }
}
