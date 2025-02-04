package com.example.oneshop.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.Order.Order;
import com.example.oneshop.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Order> orders;

    public OrderAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate item layout for order
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = orders.get(position);

        // Format the order date as a human-readable date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(new Date(order.getOrder_date()));

        // Bind data to views
        holder.orderIdTextView.setText("Order ID: " + order.getOrder_id());
        holder.totalPriceTextView.setText("Total Price: BDT " + order.getTotal_price());
        holder.totalProductsTextView.setText("Total Products: " + order.getTotal_quantity());
        holder.tv_order_date.setText("Order Date: " + formattedDate); // Show formatted order date
        holder.tv_order_status.setText("Order Status: " + order.getOrder_status());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    // ViewHolder pattern to reduce findViewById calls
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, totalPriceTextView, totalProductsTextView, tv_order_date, tv_order_status;

        public ViewHolder(View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.tv_order_id);
            totalPriceTextView = itemView.findViewById(R.id.tv_order_total_price);
            tv_order_date = itemView.findViewById(R.id.tv_order_date);
            tv_order_status = itemView.findViewById(R.id.tv_order_status);
            totalProductsTextView = itemView.findViewById(R.id.tv_order_total_item);
        }
    }
}
