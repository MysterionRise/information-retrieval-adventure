import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class FindTerm {

    public static void main(String[] args) throws IOException {

        Directory dir = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        Document doc = new Document();
        doc.add(new TextField("text", "first second third", Field.Store.YES));
        doc.add(new LegacyIntField("long", 1, Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "second third", Field.Store.YES));
        doc.add(new LegacyIntField("long", 2, Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "third", Field.Store.YES));
        doc.add(new LegacyIntField("long", 1, Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        IndexReader reader = DirectoryReader.open(dir);
        final Fields fields = MultiFields.getFields(reader);
        final Iterator<String> iterator = fields.iterator();

        long maxFreq = Long.MIN_VALUE;
        String freqTerm = "";
        while (iterator.hasNext()) {
            final String field = iterator.next();
            final Terms terms = MultiFields.getTerms(reader, field);
            final TermsEnum it = terms.iterator();
            BytesRef term = it.next();
            while (term != null) {
                System.out.println(term.utf8ToString() + " " + NumericUtils.sortableBytesToInt(term.bytes, term.offset));
                final long freq = it.totalTermFreq();
                if (freq > maxFreq) {
                    maxFreq = freq;
                    freqTerm = term.utf8ToString();
                }
                term = it.next();
            }
        }

        System.out.println(freqTerm + " " + maxFreq);

        IndexSearcher searcher = new IndexSearcher(reader);
        final TopDocs topDocs1 = searcher.search(new MatchAllDocsQuery(), 100);
        final TopDocs topDocs2 = searcher.search(new MatchAllDocsQuery(), 100);
        final TopDocs merge = TopDocs.merge(1000, new TopDocs[]{topDocs1, topDocs2});
        //(ScoreDoc o1, ScoreDoc o2) -> Integer.compare(o1.doc, o2.doc)
        Set<ScoreDoc> scoreDocs = new TreeSet<>(Comparator.comparingInt(o -> o.doc));
        float maxScore = Float.MIN_VALUE;
        for (int i = 0; i < merge.scoreDocs.length; ++i) {
            final ScoreDoc[] scoreDocs1 = merge.scoreDocs;
            scoreDocs.add(scoreDocs1[i]);
            if (scoreDocs1[i].score > maxScore) {
                maxScore = scoreDocs1[i].score;
            }
        }
        final TopDocs filteredTopDocs = new TopDocs(scoreDocs.size(), scoreDocs.toArray(new ScoreDoc[0]), maxScore);
        for (int i = 0; i < filteredTopDocs.scoreDocs.length; ++i) {
            System.out.println(filteredTopDocs.scoreDocs[i]);
        }

    }
}
