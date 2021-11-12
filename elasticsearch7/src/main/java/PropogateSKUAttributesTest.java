import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONObject;
import org.mystic.model.Product;

public class PropogateSKUAttributesTest {

  private static final String TYPE = "product";
  private static final String INDEX = "products";

  public static void main(String[] args) throws FileNotFoundException {
    indexing();
  }

  public static void indexing() throws FileNotFoundException {

    final Scanner in = new Scanner(new File("/home/kperikov/Downloads/out.txt"));
    final PrintWriter out = new PrintWriter("/home/kperikov/Downloads/products.json");

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

        out.println(
            "{ \"create\" : { \"_index\" : \""
                + INDEX
                + "\", \"_type\" : \""
                + TYPE
                + "\", \"_id\" : \""
                + prdId
                + "\" } }");
        out.println(
            new Product(
                prdId,
                department,
                brand,
                category,
                product,
                desc,
                displayColors,
                color_refines,
                variations,
                title));
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
    out.close();
  }
}
