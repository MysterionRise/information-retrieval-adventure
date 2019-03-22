package org.custom.price;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

public class PriceValueSourceParser extends ValueSourceParser {

  public void init(NamedList namedList) {}

  @Override
  public ValueSource parse(FunctionQParser fp) throws SyntaxError {
    return new PriceValueSource();
  }
}
