import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;

/**
 * @see http://stackoverflow.com/q/42162533/2663985
 */

public class BoolFuzzyQueryTest {

    public static void main(String[] args) {
        QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("name", "Rahul").fuzziness(Fuzziness.AUTO))
                .must(QueryBuilders.matchQuery("collegeAccountCode", "DIT"));
    }
}
