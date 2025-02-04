package com.example.oneshop.Order;

public class Order {
    private String order_id;
    private String user_id;
    private double total_price;
    private int total_quantity;
    private long order_date;  // Storing order date as a timestamp
    private String order_status;

    // Default constructor for Firebase
    public Order() {
    }

    public Order(String order_id, String user_id, double total_price, int total_quantity, long order_date, String order_status) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.total_price = total_price;
        this.total_quantity = total_quantity;
        this.order_date = order_date;
        this.order_status = order_status;
    }

    // Getters and setters
    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public int getTotal_products() {
        return total_quantity;
    }

    public long getOrder_date() {
        return order_date;
    }

    public void setOrder_date(long order_date) {
        this.order_date = order_date;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getTotal_quantity() {
        return String.valueOf(total_quantity);
    }
}
