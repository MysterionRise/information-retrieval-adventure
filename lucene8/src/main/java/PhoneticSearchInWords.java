import com.ibm.icu.text.Transliterator;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.commons.codec.language.bm.NameType;
import org.apache.commons.codec.language.bm.PhoneticEngine;
import org.apache.commons.codec.language.bm.RuleType;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.icu.ICUTransformFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.phonetic.BeiderMorseFilter;
import org.apache.lucene.analysis.phonetic.PhoneticFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;

/**
 * Phonetic search in Lucene
 */
public class PhoneticSearchInWords {

    public static void main(String[] args) throws IOException, ParseException {

        Directory dir =
                new MMapDirectory(Files.createTempDirectory(PhoneticSearchInWords.class.getName()));

        final Languages.LanguageSet languages = Languages.LanguageSet.from(new HashSet<String>() {{
            add("russian");
        }});

        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                final StandardTokenizer analyzer = new StandardTokenizer();
                TokenStream tok = new ICUTransformFilter(
                        analyzer,
                        Transliterator.getInstance("Cyrillic-Latin")
                );
                tok = new PhoneticFilter(tok, new DoubleMetaphone(), true);
                return new TokenStreamComponents(analyzer, tok);
            }
        };
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        String wordsString = "'чев', 'бык', 'мяч', 'фен', 'вожь', 'пес', 'риж', 'лачь', 'пуль', 'краб', 'сен', 'тячь', 'люм', 'пурр', 'лём??', 'нук', 'щив', 'лом', 'жур?', 'кит', 'бур', 'собака', 'лячь', 'пёс', 'грач', 'фез\\\\пьез', 'жук', 'таз', 'сошь', 'лев', 'пурпур', 'щит', 'рак', 'рачь', 'нож', 'люк', 'жун', 'рач', 'бым', 'ч(щ)ир', 'сом', 'ляч', 'мячь', 'сичь', 'пур', 'фож', 'тяч', 'вожь(фожь)', 'чиж', 'фыв(?)', 'щир', 'фез'";

        String[] words = wordsString.replaceAll("'", "").replaceAll(" ", "").split(",");
        System.out.println(words.length);

        for (String word : words) {
            Document doc = new Document();
            doc.add(new TextField("word", word, Field.Store.YES));
            writer.addDocument(doc);
        }

        writer.close();

        String correctWordsString = "'бык',\n" +
                "    'сом',\n" +
                "    'пес', \n" +
                "    'жук', \n" +
                "    'лом', \n" +
                "    'мяч', \n" +
                "    'нож', \n" +
                "    'фен',\n" +
                "    'фез', \n" +
                "    'щир', \n" +
                "    'фож', \n" +
                "    'бым', \n" +
                "    'рач', \n" +
                "    'тяч', \n" +
                "    'щив',\n" +
                "    'чев',\n" +
                "    'рак', \n" +
                "    'кит', \n" +
                "    'чиж', \n" +
                "    'лев', \n" +
                "    'люк', \n" +
                "    'бур', \n" +
                "    'щит', \n" +
                "    'таз', \n" +
                "    'люм', \n" +
                "    'пур', \n" +
                "    'нук', \n" +
                "    'сен', \n" +
                "    'риж', \n" +
                "    'ляч', \n" +
                "    'сож', \n" +
                "    'жун'";

        String[] correctWords = correctWordsString
                .replaceAll("'", "")
                .replaceAll(" ", "")
                .replaceAll("\n", "")
                .split(",");
        System.out.println(correctWords.length);

        for (String correctWord: correctWords) {

            System.out.println(correctWord);

            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("word", analyzer);
            Query q = parser.parse(correctWord);

            TopDocs results = searcher.search(q, 10);
            final ScoreDoc[] scoreDocs = results.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                System.out.println(scoreDoc.doc + " " + reader.document(scoreDoc.doc) + " " + scoreDoc.score);
            }

            System.out.println();

        }
    }
}
