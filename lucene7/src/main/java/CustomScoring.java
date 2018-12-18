import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.solr.search.LuceneQParser;

import java.io.IOException;

/**
 * @see https://stackoverflow.com/q/53583782/2663985
 */
public class CustomScoring {

    public static void main(String[] args) throws IOException, ParseException {

        Directory dir = new RAMDirectory();
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        Document doc = new Document();
        doc.add(new TextField("text", "bubble bloom is happening somewhere else", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "here we have just bloom bloom like a spring", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "i would be curious how bubble tea is bubble enough for me", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "i would be curious how bubble tea is bubble enough for me. Should I have more bubble ?", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        IndexReader reader = DirectoryReader.open(dir);

        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = new BooleanQuery.Builder()
                .add(new ConstantScoreQuery(new TermQuery(new Term("text", "bloom"))), BooleanClause.Occur.SHOULD)
                .add(new ConstantScoreQuery(new TermQuery(new Term("text", "bubble"))), BooleanClause.Occur.SHOULD)
                .build();

//        String querystr = "bubble^=1.0 bloom^=1.0c";
//        Query query = new QueryParser("text", new StandardAnalyzer()).parse(querystr);


        TopDocs results = searcher.search(query, 5);
        final ScoreDoc[] scoreDocs = results.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println(scoreDoc.doc + " " + reader.document(scoreDoc.doc) + " " + scoreDoc.score);
        }
        System.out.println("Hits: " + results.totalHits);
        System.out.println("Max score:" + results.getMaxScore());
    }
}
