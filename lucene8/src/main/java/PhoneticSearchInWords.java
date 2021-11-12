import com.ibm.icu.text.Transliterator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.icu.ICUTransformFilter;
import org.apache.lucene.analysis.phonetic.PhoneticFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

/** Phonetic search in Lucene */
public class PhoneticSearchInWords {

  public static void main(String[] args) throws IOException, ParseException {

    Directory dir =
        new MMapDirectory(Files.createTempDirectory(PhoneticSearchInWords.class.getName()));

    final Languages.LanguageSet languages =
        Languages.LanguageSet.from(
            new HashSet<>() {
              {
                add("russian");
              }
            });

    Analyzer analyzer =
        new Analyzer() {
          @Override
          protected TokenStreamComponents createComponents(String fieldName) {
            final StandardTokenizer analyzer = new StandardTokenizer();
            TokenStream tok =
                new ICUTransformFilter(analyzer, Transliterator.getInstance("Cyrillic-Latin"));
            tok = new PhoneticFilter(tok, new DoubleMetaphone(), true);
            return new TokenStreamComponents(analyzer, tok);
          }
        };
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter writer = new IndexWriter(dir, iwc);

    String wordsString =
        "'сек', 'руж', 'бан', 'лящ', 'чив', 'ч(щ)ир', 'фен', 'лём??', 'нут', 'нож', 'лачь', 'кит', 'жых', 'пур', 'реж', 'сом', 'чир', 'рак', 'мяч', 'рич', 'щин', 'лёс', 'пес', 'чиж', 'фож', 'вожь', 'бык', 'рачь', 'пыр', 'бак', 'кож', 'пез', 'чим', 'сен', 'краб', 'лон', 'мол', 'сем', 'сум', 'фыв(?)', 'лев', 'фур', 'пас', 'бым', 'паж', 'сошь', 'пурпур', 'фор', 'луж', 'пяж', 'сош', 'пёс', 'ряж', 'син', 'лещ', 'ож', 'лям', 'грач', 'люм', 'пыж', 'пож', 'чес', 'быч', 'луш', 'теч', 'бур', 'пав', 'гук', 'быв', 'чев', 'тяж', 'сож', 'лячь', 'щур', 'щер', 'суж', 'лив', 'лом', 'люк', 'лач', 'пурр', 'ляч', 'пуль', 'таз', 'чер', 'меч', 'мячь', 'пёз', 'чиф', 'леф', 'бум', 'cом', 'риж', 'щуп', 'рач', 'киж', 'собака', 'фез', 'тев', 'жук', 'тюч', 'вожь(фожь)', 'пяз', 'фоч', 'щит', 'лук', 'щав', 'сев', 'бам', 'зир', 'бин', 'жун', 'сичь', 'чем', 'вож', 'пуш', 'щев', 'нук', 'тяч', 'муб', 'пун', '***', 'жур?', 'щен', 'щив', 'тас', 'бим', 'жум', 'лем', 'щир', 'сощ', 'тячь'";

    String[] words = wordsString.replaceAll("'", "").replaceAll(" ", "").split(",");
    System.out.println(words.length);

    for (String word : words) {
      Document doc = new Document();
      doc.add(new TextField("word", word, Field.Store.YES));
      writer.addDocument(doc);
    }

    writer.close();

    String correctWordsString =
        "'бык',\n"
            + "    'сом',\n"
            + "    'пес', \n"
            + "    'жук', \n"
            + "    'лом', \n"
            + "    'мяч', \n"
            + "    'нож', \n"
            + "    'фен',\n"
            + "    'фез', \n"
            + "    'щир', \n"
            + "    'фож', \n"
            + "    'бым', \n"
            + "    'рач', \n"
            + "    'тяч', \n"
            + "    'щив',\n"
            + "    'чев',\n"
            + "    'рак', \n"
            + "    'кит', \n"
            + "    'чиж', \n"
            + "    'лев', \n"
            + "    'люк', \n"
            + "    'бур', \n"
            + "    'щит', \n"
            + "    'таз', \n"
            + "    'люм', \n"
            + "    'пур', \n"
            + "    'нук', \n"
            + "    'сен', \n"
            + "    'риж', \n"
            + "    'ляч', \n"
            + "    'сож', \n"
            + "    'жун'";

    String[] correctWords =
        correctWordsString.replaceAll("'", "").replaceAll(" ", "").replaceAll("\n", "").split(",");
    System.out.println(correctWords.length);

    for (String correctWord : correctWords) {

      System.out.println(correctWord);

      IndexReader reader = DirectoryReader.open(dir);
      IndexSearcher searcher = new IndexSearcher(reader);
      QueryParser parser = new QueryParser("word", analyzer);
      Query q = parser.parse(correctWord);

      TopDocs results = searcher.search(q, 10);
      final ScoreDoc[] scoreDocs = results.scoreDocs;
      for (ScoreDoc scoreDoc : scoreDocs) {
        System.out.println(
            scoreDoc.doc + " " + reader.document(scoreDoc.doc) + " " + scoreDoc.score);
      }

      System.out.println();
    }
  }
}
