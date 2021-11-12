import java.util.HashSet;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.FloatFieldSource;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

public class ValueSourceParserTest {

  public static void main(String[] args) throws SyntaxError {
    final SortOnXSourceParser sortOnXSourceParser = new SortOnXSourceParser();
    sortOnXSourceParser.parse(new FunctionQParser("sortOnX(x, 309043) desc", null, null, null));
  }

  static class SortOnXSourceParser extends ValueSourceParser {

    public void init(NamedList namedList) {}

    @Override
    public ValueSource parse(FunctionQParser fp) throws SyntaxError {
      ValueSource source = fp.parseValueSource();
      HashSet<Integer> topic_ids = new HashSet<>();
      while (fp.hasMoreArguments()) {
        topic_ids.add(fp.parseInt());
      }
      return new FloatFieldSource("test");
    }
  }
}
