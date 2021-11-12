package org.mystic;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.ConstValueSource;
import org.apache.lucene.queries.function.valuesource.DualFloatFunction;
import org.apache.lucene.queries.function.valuesource.IntFieldSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/** @see https://stackoverflow.com/q/45773559/2663985 */
public class ClosestNumberScore {

  public static void main(String[] args) throws IOException {

    Directory dir = new RAMDirectory();
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    addDocument(writer, "doc1", 100);
    addDocument(writer, "doc2", 200);
    addDocument(writer, "doc3", 300);
    addDocument(writer, "doc4", 400);
    addDocument(writer, "doc5", 500);
    addDocument(writer, "doc6", 600);

    writer.close();

    IndexReader reader = IndexReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);

    Query q =
        new FunctionQuery(
            new DistanceDualFloatFunction(new IntFieldSource("weight"), new ConstValueSource(245)));

    final ScoreDoc[] scoreDocs = searcher.search(q, 10).scoreDocs;
    for (ScoreDoc doc : scoreDocs) {
      System.out.println(reader.document(doc.doc).getField("title") + " " + doc.score);
    }
  }

  private static void addDocument(IndexWriter writer, String id, int value) throws IOException {
    Document doc = new Document();
    doc.add(new TextField("title", id, Store.YES));
    doc.add(new IntField("weight", value, Store.YES));
    writer.addDocument(doc);
  }

  static class DistanceDualFloatFunction extends DualFloatFunction {

    public DistanceDualFloatFunction(ValueSource a, ValueSource b) {
      super(a, b);
    }

    @Override
    protected String name() {
      return "distance function";
    }

    @Override
    protected float func(int doc, FunctionValues aVals, FunctionValues bVals) {
      return 1000 - Math.abs(aVals.intVal(doc) - bVals.intVal(doc));
    }
  }
}
