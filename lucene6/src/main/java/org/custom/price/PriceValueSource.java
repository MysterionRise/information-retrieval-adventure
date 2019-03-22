package org.custom.price;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.valuesource.ConstValueSource;

import java.io.IOException;
import java.util.Map;

public class PriceValueSource extends ValueSource {
    @Override
    public FunctionValues getValues(Map map, LeafReaderContext leafReaderContext) throws IOException {
        return new FloatDocValues(new ConstValueSource(1.0f)) {
            @Override
            public float floatVal(int doc) {
                // TODO get by doc price that collector put into request context
                return (float) map.get(doc);

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
