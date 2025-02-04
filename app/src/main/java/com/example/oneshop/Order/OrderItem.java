package com.example.oneshop.Order;

public class OrderItem {
    private String order_id;
    private String product_id;
    private String seller_id;
    private double price;
    private int quantity;

    // Constructor
    public OrderItem(String order_id, String product_id, String seller_id, double price, int quantity) {
        this.order_id = order_id;
        this.product_id = product_id;
        this.seller_id = seller_id;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}


//package com.example.oneshop.Order;
//
//public class OrderItem {
//    private String order_id;
//    private String product_id;
//    private String seller_id;
//    private double price;
//    private int quantity;
//
//    // Default constructor for Firebase
//    public OrderItem() {
//    }
//
//    public OrderItem(String order_id, String product_id, String seller_id, double price, int quantity) {
//        this.order_id = order_id;
//        this.product_id = product_id;
//        this.seller_id = seller_id;
//        this.price = price;
//        this.quantity = quantity;
//    }
//
//    // Getters and setters
//    public String getOrder_id() {
//        return order_id;
//    }
//
//    public void setOrder_id(String order_id) {
//        this.order_id = order_id;
//    }
//
//    public String getProduct_id() {
//        return product_id;
//    }
//
//    public void setProduct_id(String product_id) {
//        this.product_id = product_id;
//    }
//
//    public String getSeller_id() {
//        return seller_id;
//    }
//
//    public void setSeller_id(String seller_id) {
//        this.seller_id = seller_id;
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
//    public int getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }
//}
