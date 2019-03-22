package org.custom.price;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class PriceValueSource extends ValueSource {

  private static final Random r = new Random();

  @Override
  public FunctionValues getValues(Map map, LeafReaderContext leafReaderContext) throws IOException {

    return new FunctionValues() {
      @Override
      public float floatVal(int doc) {
        // TODO get by doc price that collector put into request context
        if (!map.containsKey(doc)) return r.nextFloat() * r.nextInt(100);
        return (float) map.get(doc);
      }

      @Override
      public String toString(int doc) {
        return "price funct" + doc;
      }
    };
  }

  @Override
  public boolean equals(Object o) {
    return false;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public String description() {
    return "price value";
  }
}
