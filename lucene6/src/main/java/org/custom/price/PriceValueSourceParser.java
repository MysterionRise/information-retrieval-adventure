package org.custom.price;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.FloatFieldSource;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

import java.util.HashSet;

public class PriceValueSourceParser extends ValueSourceParser {


    public void init(NamedList namedList) {
    }

    @Override
    public ValueSource parse(FunctionQParser fp) throws SyntaxError {
        return new PriceValueSource();
//        ValueSource source = fp.parseValueSource();
//        HashSet<Integer> topic_ids = new HashSet<>();
//        while(fp.hasMoreArguments()){
//            topic_ids.add(fp.parseInt());
//        }
//        return new FloatFieldSource("test");
    }

}
