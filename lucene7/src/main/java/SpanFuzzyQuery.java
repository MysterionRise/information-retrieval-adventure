import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * @see https://stackoverflow.com/q/49553970/2663985
 */
public class SpanFuzzyQuery {

    public static void main(String[] args) throws IOException, ParseException {

        Directory dir = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        Document doc = new Document();
        doc.add(new TextField("text", "cars text text", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "car some text ca", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "carmaggeddon some text", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        IndexReader reader = DirectoryReader.open(dir);

        IndexSearcher searcher = new IndexSearcher(reader);

        SpanQuery[] clauses = new SpanQuery[2];
        clauses[0] = new SpanMultiTermQueryWrapper(new FuzzyQuery(new Term("text", "som"), 2));
        clauses[1] = new SpanMultiTermQueryWrapper(new FuzzyQuery(new Term("text", "tet"), 2));
        SpanNearQuery query = new SpanNearQuery(clauses, 0, true);

        TopDocs results = searcher.search(query, 5);
        System.out.println("Hits: " + results.totalHits);
        System.out.println("Max score:" + results.getMaxScore());
    }
}
