package org.mystic;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.test.ESIntegTestCase;
import org.json.JSONObject;
import org.junit.Test;
import org.mystic.model.Product;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

        final Scanner in = new Scanner(new File("/home/kperikov/Downloads/out.txt"));
        final PrintWriter out = new PrintWriter(new File("/home/kperikov/Downloads/products.json"));

        final List<String> displayColors = new ArrayList<>();
        final List<String> color_refines = new ArrayList<>();

        while (in.hasNextLine()) {
            final String obj = in.nextLine();
            final JSONObject curr = new JSONObject(obj);
            if (curr.getString("docType").equalsIgnoreCase("product")) {
                final String prdId = curr.getString("prdId");
                String department = "";
                if (curr.has("department")) {
                    department = curr.getString("department");
                }
                String brand = "";
                if (curr.has("brand")) {
                    brand = curr.getString("brand");
                }
                String category = "";
                if (curr.has("category")) {
                    category = curr.getString("category");
                }
                String product = "";
                if (curr.has("product")) {
                    product = curr.getString("product");
                }
                String desc = "";
                if (curr.has("desc")) {
                    desc = curr.getString("desc");
                }
                String variations = "";
                if (curr.has("variations")) {
                    variations = curr.getString("variations");
                }
                final String title = curr.getString("title");

                out.println(new Product(prdId, department, brand, category, product, desc, displayColors, color_refines, variations, title).toString());
                out.flush();
                displayColors.clear();
                color_refines.clear();
            } else {
                if (curr.has("displayColor")) {
                    displayColors.add(curr.getString("displayColor"));
                }
                if (curr.has("color_refine")) {
                    color_refines.add(curr.getString("color_refine"));
                }
            }
        }

        /*JSONObject sku1 = new JSONObject("{\"hasInv\":\"false\",\"docType\":\"sku\",\"isFeaturedColor\":\"false\",\"vendorName\":\"GERSON ECOMMERCE COMPANY THE\",\"parentId\":\"1644554\",\"isLtl\":\"false\",\"id\":\"948560651644554\",\"displayColor\":\"Multi/None\",\"seller_refine\":\"Hlos\",\"code\":\"94856065\",\"originalPrice\":\"9500000\",\"deptName\":\"TRIM-A-TREE\",\"skuInventory\":\"0\",\"color_refine\":\"Multi/none\",\"price\":\"9500000\",\"upcCode\":\"400948560657\",\"lastUpdate\":\"Fri Sep 09 18:14:12 MSK 2016\"}");
        JSONObject prd1 = new JSONObject("{\"prdId\":\"1644554\",\"desc\":\"<p>Decorate your home in classic Christmas style with this Sterling Santa set.</p>\",\"docType\":\"product\",\"ignrInv\":\"false\",\"createDt\":\"1381290898\",\"department\":\"Home Decor\",\"id\":\"1644554\",\"seoTitle\":\"sterling-2-pc-santa-decor-set\",\"priceCode\":\"ORIG\",\"name\":\"Sterling 2-pc. Santa Decor Set\",\"valueAddIcons\":\"Online_Exclusive.gif\",\"variations\":\"None\",\"title\":\"Sterling 2-pc. Santa Decor Set\",\"inventoryQty\":\"0\",\"brand\":\"Sterling\",\"assortment\":\"cat2940351:cat3070161\",\"category\":\"Decorative Accents\",\"product\":\"Figurines & Accents\",\"lastUpdate\":\"Fri Sep 09 18:14:12 MSK 2016\"}");

        final String displayColor = sku1.getString("displayColor");
        final String color_refine = sku1.getString("color_refine");
        final String prdId = prd1.getString("prdId");
        final String department = prd1.getString("department");
        final String brand = prd1.getString("brand");
        final String category = prd1.getString("category");
        final String product = prd1.getString("product");
        final String desc = prd1.getString("desc");*/

        out.close();


        /*add(new Product(prdId, department, brand, category, product, desc, displayColor, color_refine));

        refresh(); // otherwise we would not find beers yet

        indexExists(INDEX); // verifies that index 'drinks' exists
        ensureGreen(INDEX); // ensures cluster status is green

        IndicesStatsResponse indicesStatsResponse = client().admin().indices().prepareStats(INDEX).get();
        System.out.println(indicesStatsResponse.getIndices().get(INDEX).getTotal().docs.getCount());*/

    }
}
