package org.mystic.model;

import java.io.StringWriter;
import java.util.List;
import org.json.JSONWriter;

public class Product {

  private final String prdId;
  private final String department;
  private final String brand;
  private final String category;
  private final String product;
  private final String desc;
  private final List<String> displayColors;
  private final List<String> color_refines;
  private final String variations;
  private final String title;

  public Product(
      String prdId,
      String department,
      String brand,
      String category,
      String product,
      String desc,
      List<String> displayColor,
      List<String> color_refine,
      String variations,
      String title) {
    this.prdId = prdId;
    this.department = department;
    this.brand = brand;
    this.category = category;
    this.product = product;
    this.desc = desc;
    this.displayColors = displayColor;
    this.color_refines = color_refine;
    this.variations = variations;
    this.title = title;
  }

  public String getPrdId() {
    return prdId;
  }

  public String getDepartment() {
    return department;
  }

  public String getBrand() {
    return brand;
  }

  public String getCategory() {
    return category;
  }

  public String getProduct() {
    return product;
  }

  public String getDesc() {
    return desc;
  }

  public List<String> getDisplayColors() {
    return displayColors;
  }

  public List<String> getColor_refines() {
    return color_refines;
  }

  public String getVariations() {
    return variations;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Product product1 = (Product) o;

    if (prdId != null ? !prdId.equals(product1.prdId) : product1.prdId != null) return false;
    if (department != null ? !department.equals(product1.department) : product1.department != null)
      return false;
    if (brand != null ? !brand.equals(product1.brand) : product1.brand != null) return false;
    if (category != null ? !category.equals(product1.category) : product1.category != null)
      return false;
    if (product != null ? !product.equals(product1.product) : product1.product != null)
      return false;
    if (desc != null ? !desc.equals(product1.desc) : product1.desc != null) return false;
    if (displayColors != null
        ? !displayColors.equals(product1.displayColors)
        : product1.displayColors != null) return false;
    if (color_refines != null
        ? !color_refines.equals(product1.color_refines)
        : product1.color_refines != null) return false;
    if (variations != null ? !variations.equals(product1.variations) : product1.variations != null)
      return false;
    return title != null ? title.equals(product1.title) : product1.title == null;
  }

  @Override
  public int hashCode() {
    int result = prdId != null ? prdId.hashCode() : 0;
    result = 31 * result + (department != null ? department.hashCode() : 0);
    result = 31 * result + (brand != null ? brand.hashCode() : 0);
    result = 31 * result + (category != null ? category.hashCode() : 0);
    result = 31 * result + (product != null ? product.hashCode() : 0);
    result = 31 * result + (desc != null ? desc.hashCode() : 0);
    result = 31 * result + (displayColors != null ? displayColors.hashCode() : 0);
    result = 31 * result + (color_refines != null ? color_refines.hashCode() : 0);
    result = 31 * result + (variations != null ? variations.hashCode() : 0);
    result = 31 * result + (title != null ? title.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    final StringWriter w = new StringWriter();
    final JSONWriter obj =
        new JSONWriter(w)
            .object()
            .key("prdId")
            .value(prdId)
            .key("department")
            .value(department)
            .key("brand")
            .value(brand)
            .key("category")
            .value(category)
            .key("product")
            .value(product)
            .key("title")
            .value(title)
            .key("variations")
            .value(variations)
            .key("desc")
            .value(desc);
    JSONWriter colors1 = obj.key("displayColor").array();
    for (String displayColor : displayColors) {
      colors1 = colors1.value(displayColor);
    }
    colors1 = colors1.endArray().key("color_refine").array();
    for (String color_refine : color_refines) {
      colors1 = colors1.value(color_refine);
    }
    colors1.endArray().endObject();
    return w.toString();
  }
}
