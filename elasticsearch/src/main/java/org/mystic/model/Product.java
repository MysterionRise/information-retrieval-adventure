package org.mystic.model;

import org.json.JSONWriter;

import java.io.StringWriter;

public class Product {

    private final String prdId;
    private final String department;
    private final String brand;
    private final String category;
    private final String product;
    private final String desc;
    private final String displayColor;
    private final String color_refine;

    public Product(String prdId, String department, String brand, String category, String product, String desc, String displayColor, String color_refine) {
        this.prdId = prdId;
        this.department = department;
        this.brand = brand;
        this.category = category;
        this.product = product;
        this.desc = desc;
        this.displayColor = displayColor;
        this.color_refine = color_refine;
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

    public String getDisplayColor() {
        return displayColor;
    }

    public String getColor_refine() {
        return color_refine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product1 = (Product) o;

        if (prdId != null ? !prdId.equals(product1.prdId) : product1.prdId != null) return false;
        if (department != null ? !department.equals(product1.department) : product1.department != null) return false;
        if (brand != null ? !brand.equals(product1.brand) : product1.brand != null) return false;
        if (category != null ? !category.equals(product1.category) : product1.category != null) return false;
        if (product != null ? !product.equals(product1.product) : product1.product != null) return false;
        if (desc != null ? !desc.equals(product1.desc) : product1.desc != null) return false;
        if (displayColor != null ? !displayColor.equals(product1.displayColor) : product1.displayColor != null)
            return false;
        return color_refine != null ? color_refine.equals(product1.color_refine) : product1.color_refine == null;
    }

    @Override
    public int hashCode() {
        int result = prdId != null ? prdId.hashCode() : 0;
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (brand != null ? brand.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (displayColor != null ? displayColor.hashCode() : 0);
        result = 31 * result + (color_refine != null ? color_refine.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringWriter w = new StringWriter();
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
                .key("desc")
                .value(desc)
                .key("displayColor")
                .value(displayColor)
                .key("color_refine")
                .value(color_refine)
                .endObject();
        return w.toString();
    }
}
