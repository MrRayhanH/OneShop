package com.example.oneshop.Products;

public class ProductS {
    private String product_id, product_name, description, category_name, image_url, image_public_id, seller_user_id;
    private double price;
    private int stock_quantity;

    // Empty constructor for Firebase
    public ProductS() {}

    // Getters and Setters
    public String getProduct_id() { return product_id; }
    public void setProduct_id(String product_id) { this.product_id = product_id; }

    public String getProduct_name() { return product_name; }
    public void setProduct_name(String product_name) { this.product_name = product_name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory_name() { return category_name; }
    public void setCategory_name(String category_name) { this.category_name = category_name; }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    public String getImage_public_id() { return image_public_id; }
    public void setImage_public_id(String image_public_id) { this.image_public_id = image_public_id; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock_quantity() { return stock_quantity; }
    public String getSeller_user_id() {return seller_user_id;}
    public void setStock_quantity(int stock_quantity) { this.stock_quantity = stock_quantity; }
}

//package com.example.oneshop.Products;
//
//public class ProductS {
//    private String product_id, product_name, description, category_name, image_url, image_public_id, seller_user_id;
//    private double price;
//    private int stock_quantity;
//
//    // Empty constructor for Firebase
//    public ProductS() {
//    }
//
//    // Getters and Setters
//    public String getProduct_id() {
//        return product_id;
//    }
//
//    public void setProduct_id(String product_id) {
//        this.product_id = product_id;
//    }
//
//    public String getProduct_name() {
//        return product_name;
//    }
//
//    public void setProduct_name(String product_name) {
//        this.product_name = product_name;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getCategory_name() {
//        return category_name;
//    }
//
//    public void setCategory_name(String category_name) {
//        this.category_name = category_name;
//    }
//
//    public String getImage_url() {
//        return image_url;
//    }
//
//    public void setImage_url(String image_url) {
//        this.image_url = image_url;
//    }
//
//    public String getImage_public_id() {
//        return image_public_id;
//    }
//
//    public void setImage_public_id(String image_public_id) {
//        this.image_public_id = image_public_id;
//    }
//
//    public String getSeller_user_id() {
//        return seller_user_id;
//    }
//
//    public void setSeller_user_id(String seller_user_id) {
//        this.seller_user_id = seller_user_id;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    public int getStock_quantity() {
//        return stock_quantity;
//    }
//
//    public void setStock_quantity(int stock_quantity) {
//        this.stock_quantity = stock_quantity;
//    }
//}
