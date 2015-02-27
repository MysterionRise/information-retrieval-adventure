package org.mystic.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.util.automaton.Automata;

import java.io.IOException;

/**
 * @see http://stackoverflow.com/q/28565090/2663985
 */
public class AutomatonScoringTest {

    public static void main(String[] args) throws IOException {
        Directory dir = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        Document doc = new Document();
        doc.add(new TextField("text", "muffin", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "zmuffin", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "mufffin", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        MultiTermQuery query = new AutomatonQuery(new Term("text"), Automata.makeAnyString());
        query.setRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_QUERY_REWRITE);
        System.out.println("query: " + query);

        TopDocs results = searcher.search(query, null, 100);
        ScoreDoc[] scoreDocs = results.scoreDocs;
        for (int i = 0; i < scoreDocs.length; ++i) {
            System.out.println(searcher.explain(query, scoreDocs[i].doc));
        }
    }
}
