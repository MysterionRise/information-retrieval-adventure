package org.custom.price;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;

public class PriceValueSource extends ValueSource {

  public PriceValueSource() {}

  private static final Random r = new Random();

  private static final Map<Integer, Double> cachemap = new HashMap<>();

  @Override
  public FunctionValues getValues(Map map, LeafReaderContext leafReaderContext) throws IOException {

    return new FloatDocValues(this) {

      @Override
      public float floatVal(int doc) {
        float v = r.nextFloat() * r.nextInt(100);
        cachemap.putIfAbsent(leafReaderContext.docBase + doc, (double) v);
        return cachemap.get(leafReaderContext.docBase + doc).floatValue();
      }

      @Override
      public int intVal(int doc) {
        return (int) floatVal(doc);
      }

      @Override
      public long longVal(int doc) {
        return (long) floatVal(doc);
      }

      @Override
      public double doubleVal(int doc) {
        return floatVal(doc);
      }

      @Override
      public Object objectVal(int doc) {
        return floatVal(doc);
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
