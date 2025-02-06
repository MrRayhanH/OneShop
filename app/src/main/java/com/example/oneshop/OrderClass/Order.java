package com.example.oneshop.OrderClass;

import android.net.Uri;

public class Order {
    private String orderId;       // Unique order ID
    private String userId;        // User who placed the order
    private double totalPrice;    // Total price of the order
    private int totalProduct;     // Number of products in the order
    private String productId;     // Product ID for the order
    private String sellerId;      // Seller ID
    private String orderStatus;   // Order status (e.g., Pending, Shipped)
    private String orderDate;     // Order date in string format (ISO 8601)
    private Uri ImageUrl;

    // Default constructor for Firebase
    public Order() {}

    public Order(String orderId, double totalPrice, int totalProduct,
                 String productId, String userId, String sellerId, String orderStatus, String orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.totalProduct = totalProduct;
        this.productId = productId;
        this.sellerId = sellerId;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public int getTotalProduct() { return totalProduct; }
    public void setTotalProduct(int totalProduct) { this.totalProduct = totalProduct; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public Uri getProductImageUrl(){
        return ImageUrl;
    }
}

//
//public class Order {
//    private String order_id;
//    private String user_id;
//    private double total_price;
//    private int total_quantity;
//    private long order_date;  // Storing order date as a timestamp
//    private String order_status;
//    private int total_products;
//
//    // Default constructor for Firebase
//    public Order() {
//    }
//
//    public Order(String order_id, String user_id, double total_price, int total_quantity, long order_date, String order_status, int total_products) {
//        this.order_id = order_id;
//        this.user_id = user_id;
//        this.total_price = total_price;
//        this.total_quantity = total_quantity;
//        this.order_date = order_date;
//        this.order_status = order_status;
//        this.total_products = total_products;
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
//    public String getUser_id() {
//        return user_id;
//    }
//
//    public void setUser_id(String user_id) {
//        this.user_id = user_id;
//    }
//
//    public double getTotal_price() {
//        return total_price;
//    }
//
//    public void setTotal_price(double total_price) {
//        this.total_price = total_price;
//    }
//
//    public int getTotal_products() {
//        return total_quantity;
//    }
//
//    public long getOrder_date() {
//        return order_date;
//    }
//
//    public void setOrder_date(long order_date) {
//        this.order_date = order_date;
//    }
//
//    public String getOrder_status() {
//        return order_status;
//    }
//
//    public void setOrder_status(String order_status) {
//        this.order_status = order_status;
//    }
//
//    public String getTotal_quantity() {
//        return String.valueOf(total_quantity);
//    }
//    public void setTotal_products(int totalProducts){
//        this.total_products = totalProducts;
//    }
//}
