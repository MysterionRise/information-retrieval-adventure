import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queries.mlt.MoreLikeThisQuery;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter.*;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter.PRESERVE_ORIGINAL;

/**
 * @see https://stackoverflow.com/q/49527123/2663985
 */
public class SpanFuzzyQuery {

    public static void main(String[] args) throws IOException, ParseException {
        int flags = 0;
        flags |= GENERATE_WORD_PARTS;
        flags |= GENERATE_NUMBER_PARTS;
        flags |= CATENATE_WORDS;
        flags |= CATENATE_NUMBERS;
        flags |= CATENATE_ALL;
        flags |= SPLIT_ON_CASE_CHANGE;
        flags |= SPLIT_ON_NUMERICS;
        flags |= PRESERVE_ORIGINAL;
        flags |= STEM_ENGLISH_POSSESSIVE;
        Directory dir = new RAMDirectory();
        int finalFlags = flags;
        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                final StandardTokenizer src = new StandardTokenizer();
                TokenStream tok = new StandardFilter(src);
                tok = new WordDelimiterGraphFilter(tok, finalFlags, CharArraySet.EMPTY_SET);
                tok = new LowerCaseFilter(tok);
                return new TokenStreamComponents(src, tok);
            }
        };

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        Document doc = new Document();
        doc.add(new TextField("text", "BlueCross BlueShield", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "Company Name Ltd", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "Google Inc.", Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("text", "Another BlueCompany", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        IndexReader reader = DirectoryReader.open(dir);

        IndexSearcher searcher = new IndexSearcher(reader);
        MoreLikeThis mlt = new MoreLikeThis(reader);
        mlt.setAnalyzer(analyzer);
        mlt.setMinDocFreq(0);
        mlt.setMinTermFreq(0);
        mlt.setMinWordLen(0);
        final Query query = mlt.like("text", new StringReader("BlueCros BlueShield              Customer Service \n" +
                "   1-800-521-2227           \n" +
                "                        of Texas                          Preauth-Medical              1-800-441-9188           \n" +
                "                                                          Preauth-MH/CD                1-800-528-7264           \n" +
                "                                                          Blue Card Access             1-800-810-2583           "));
        System.out.println(query);

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        if (query instanceof BooleanQuery) {
            final List<BooleanClause> clauses = ((BooleanQuery) query).clauses();
            for (BooleanClause bc : clauses) {
                Query q = bc.getQuery();
                if (q instanceof TermQuery) {
                    builder.add(new FuzzyQuery(((TermQuery) q).getTerm(), 2), bc.getOccur());
                } else {
                    builder.add(bc);
                }
            }
        }
        Query updatedQuery = builder.build();
        System.out.println(updatedQuery);

        TopDocs results = searcher.search(query, 5);
        final ScoreDoc[] scoreDocs = results.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println(scoreDoc.doc + " " + scoreDoc.score);
        }
        System.out.println("Hits: " + results.totalHits);
        System.out.println("Max score:" + results.getMaxScore());
    }
}
