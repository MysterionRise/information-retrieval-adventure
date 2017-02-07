import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;

public class RandomScoreTest {

    public static void main(String[] args) {
        final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        final FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders
                .functionScoreQuery(boolQuery, ScoreFunctionBuilders.randomFunction(1))
                .boostMode(CombineFunction.REPLACE);
    }
}
