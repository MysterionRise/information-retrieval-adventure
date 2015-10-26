package org.apache.lucene.search;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class BM25FTest {

    private IndexSearcher indexSearcher;
    private IndexReader reader;
    private Directory dir;

    private static final Version VERSION = Version.LATEST;

    private static final String ID = "famn";
    private static final String TI_EN = "title_en";
    private static final String TI_FR = "title_fr";
    private static final String TI_DE = "title_de";

    @Before
    public void setUp() {
        try {
            dir = new RAMDirectory();
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_4, new WhitespaceAnalyzer());
            config.setMergePolicy(new LogDocMergePolicy());
            IndexWriter writer = new IndexWriter(dir, config);

            writer.addDocument(doc(0, "Things with Thing1 a resistance of 500 ohms. Things"));
            writer.addDocument(doc(1, "melting Thing with a melting point of 500 degrees. Thingb"));
            writer.addDocument(doc(2, "Things with a sadasd point of 6100.0 degrees."));
            writer.addDocument(doc(3, "Things with a melting sadasd sadasd point of 6200 degrees."));
            writer.addDocument(doc(4, "Things with a meltingpoint of 6200 degrees."));
            writer.addDocument(doc(5, "melting Things with a point of 6200 degrees."));
            reader = DirectoryReader.open(writer, false);
            writer.close();
            indexSearcher = new IndexSearcher(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws IOException {
        reader.close();
        dir.close();
    }

    @Test
    public void testBM25Query() throws IOException {
        final List<Query> queries = new ArrayList<>();
        queries.add(new TermBM25FQuery(new Term("title_en", "melting")));
        queries.add(new TermBM25FQuery(new Term("title_de", "melting")));
        final Map<String, Float> weights = new HashMap<>();
        weights.put("title_en", 1.5f);
        weights.put("title_de", 0.5f);
        final BM25FQuery query = new BM25FQuery(queries, weights);

        TopScoreDocCollector collector = TopScoreDocCollector.create(50, true);
        indexSearcher.setSimilarity(new BM25FSimilarity());
        indexSearcher.search(query, collector);
        ScoreDoc[] bScoreDocs = collector.topDocs().scoreDocs;
        double[] scores = new double[bScoreDocs.length];
        for (int i = 0; i < bScoreDocs.length; ++i) {
            scores[i] = bScoreDocs[i].score;
            System.out.println(bScoreDocs[i].doc + " " + scores[i]);
        }
    }

    private Document doc(int id, String title) {
        Document doc = new Document();
        doc.add(new IntField(ID, id, Field.Store.YES));
        doc.add(new TextField(TI_EN, title, Field.Store.YES));
        doc.add(new TextField(TI_DE, title, Field.Store.YES));
        doc.add(new TextField(TI_FR, title, Field.Store.YES));
        return doc;
    }
}
