package com.example.oneshop.User.Order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.OrderClass.Order;
import com.example.oneshop.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Order> orders;

    public OrderAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate order item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        // Format the order date
        String formattedDate = formatDate(order.getOrderDate());

        // Bind data to views
        holder.orderIdTextView.setText("Order ID: " + order.getOrderId());
        holder.totalPriceTextView.setText("Total Price: BDT " + order.getTotalPrice());
        holder.totalProductsTextView.setText("Total Products: " + order.getTotalProduct());
        holder.tv_order_date.setText("Order Date: " + formattedDate);
        holder.tv_order_status.setText("Order Status: " + order.getOrderStatus());
        Picasso.get().load(order.getProductImageUrl()).into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    // Convert orderDate string to a readable format
    private String formatDate(String orderDate) {
        if (orderDate == null || orderDate.isEmpty()) {
            return "Unknown";
        }
        return orderDate.replace("T", " ").replace("Z", ""); // Convert ISO 8601 format to readable format
    }

    // ViewHolder class to optimize performance
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, totalPriceTextView, totalProductsTextView, tv_order_date, tv_order_status;
        ImageView productImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.tv_order_id);
            totalPriceTextView = itemView.findViewById(R.id.tv_order_total_price);
            totalProductsTextView = itemView.findViewById(R.id.tv_order_total_item);
            tv_order_date = itemView.findViewById(R.id.tv_order_date);
            tv_order_status = itemView.findViewById(R.id.tv_order_status);
            productImageView = itemView.findViewById(R.id.iv_product);
        }
    }
}
