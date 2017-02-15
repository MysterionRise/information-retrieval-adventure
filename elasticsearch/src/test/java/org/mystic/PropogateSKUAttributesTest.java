package org.mystic;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.test.ESIntegTestCase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.mystic.model.Product;
import org.mystic.model.User;

import static org.hamcrest.Matchers.equalTo;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE)
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
public class PropogateSKUAttributesTest extends ESIntegTestCase {

    private static final String TYPE = "product";
    private static final String INDEX = "products";

    public void add(Product product) {
        System.out.println("adding " + product.toString());
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, product.getPrdId());
        indexRequest.source(product.toString());
        index(INDEX, TYPE, product.getPrdId(), product.toString());
    }

    @Test
    public void indexing() throws Exception {

        JSONObject sku1 = new JSONObject("{\"hasInv\":\"false\",\"docType\":\"sku\",\"isFeaturedColor\":\"false\",\"vendorName\":\"GERSON ECOMMERCE COMPANY THE\",\"parentId\":\"1644554\",\"isLtl\":\"false\",\"id\":\"948560651644554\",\"displayColor\":\"Multi/None\",\"seller_refine\":\"Hlos\",\"code\":\"94856065\",\"originalPrice\":\"9500000\",\"deptName\":\"TRIM-A-TREE\",\"skuInventory\":\"0\",\"color_refine\":\"Multi/none\",\"price\":\"9500000\",\"upcCode\":\"400948560657\",\"lastUpdate\":\"Fri Sep 09 18:14:12 MSK 2016\"}");
        JSONObject prd1 = new JSONObject("{\"prdId\":\"1644554\",\"desc\":\"<p>Decorate your home in classic Christmas style with this Sterling Santa set.</p>\",\"docType\":\"product\",\"ignrInv\":\"false\",\"createDt\":\"1381290898\",\"department\":\"Home Decor\",\"id\":\"1644554\",\"seoTitle\":\"sterling-2-pc-santa-decor-set\",\"priceCode\":\"ORIG\",\"name\":\"Sterling 2-pc. Santa Decor Set\",\"valueAddIcons\":\"Online_Exclusive.gif\",\"variations\":\"None\",\"title\":\"Sterling 2-pc. Santa Decor Set\",\"inventoryQty\":\"0\",\"brand\":\"Sterling\",\"assortment\":\"cat2940351:cat3070161\",\"category\":\"Decorative Accents\",\"product\":\"Figurines & Accents\",\"lastUpdate\":\"Fri Sep 09 18:14:12 MSK 2016\"}");

        final String displayColor = sku1.getString("displayColor");
        final String color_refine = sku1.getString("color_refine");
        final String prdId = prd1.getString("prdId");
        final String department = prd1.getString("department");
        final String brand = prd1.getString("brand");
        final String category = prd1.getString("category");
        final String product = prd1.getString("product");
        final String desc = prd1.getString("desc");


        add(new Product(prdId, department, brand, category, product, desc, displayColor, color_refine));

        refresh(); // otherwise we would not find beers yet

        indexExists(INDEX); // verifies that index 'drinks' exists
        ensureGreen(INDEX); // ensures cluster status is green

        IndicesStatsResponse indicesStatsResponse = client().admin().indices().prepareStats(INDEX).get();
        System.out.println(indicesStatsResponse.getIndices().get(INDEX).getTotal().docs.getCount());

    }
}
