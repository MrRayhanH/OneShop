package com.example.oneshop.Seller.Order;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.OrderClass.Order;
import com.example.oneshop.R;

import java.util.ArrayList;

public class OrderAdapterSeller extends RecyclerView.Adapter<OrderAdapterSeller.ViewHolder> {

    private final Context context;
    private final ArrayList<Order> orders;

    public OrderAdapterSeller(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_seller, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.orderIdTextView.setText("Order ID: " + order.getOrderId());
        holder.totalPriceTextView.setText("Total Price: BDT " + order.getTotalPrice());
        holder.totalProductsTextView.setText("Total Products: " + order.getTotalProduct());
        holder.tv_order_date.setText("Order Date: " + order.getOrderDate());
        holder.tv_order_status.setText("Order Status: " + order.getOrderStatus());

        // Click event to open Order Details Activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("orderId", order.getOrderId());
            intent.putExtra("totalPrice", order.getTotalPrice());
            intent.putExtra("totalProduct", order.getTotalProduct());
            intent.putExtra("productId", order.getProductId());
            intent.putExtra("userId", order.getUserId());
            intent.putExtra("sellerId", order.getSellerId());
            intent.putExtra("orderStatus", order.getOrderStatus());
            intent.putExtra("orderDate", order.getOrderDate());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, totalPriceTextView, totalProductsTextView, tv_order_date, tv_order_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.tv_order_id);
            totalPriceTextView = itemView.findViewById(R.id.tv_total_price);
            totalProductsTextView = itemView.findViewById(R.id.tv_total_products);
            tv_order_date = itemView.findViewById(R.id.tv_order_date);
            tv_order_status = itemView.findViewById(R.id.tv_order_status);
        }
    }
}
