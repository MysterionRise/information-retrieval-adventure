import java.io.IOException;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/** @see https://stackoverflow.com/q/49527123/2663985 */
public class GetFieldType {

  public static void main(String[] args) throws IOException, ParseException {

    Directory dir = new RAMDirectory();
    IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    Document doc = new Document();
    doc.add(new TextField("text", "otherwise i want to do so", Field.Store.YES));
    doc.add(new StoredField("int", 123));
    doc.add(new StoredField("float", 123.456d));
    doc.add(new IntPoint("i", 123));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "[otherwise i would be glad to do so", Field.Store.YES));
    doc.add(new StoredField("int", 234));
    writer.addDocument(doc);
    doc = new Document();
    doc.add(new TextField("text", "(otherwise", Field.Store.YES));
    doc.add(new StoredField("int", 345));
    writer.addDocument(doc);
    writer.close();

    IndexReader reader = DirectoryReader.open(dir);

    final Document document = reader.document(0);
    final IndexableField text = document.getField("float");
    System.out.println(text.stringValue());
    System.out.println(text.numericValue());
    System.out.println(text.binaryValue());
    System.out.println(text.readerValue());

    final IndexableField i = document.getField("int");

    System.out.println(i.stringValue());
    System.out.println(i.numericValue());
    System.out.println(i.binaryValue());
    System.out.println(i.readerValue());
  }
}
