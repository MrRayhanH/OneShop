package com.example.oneshop.Admin.Order.Warehouse;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.Admin.Order.Accept.DetailsActivity;
import com.example.oneshop.OrderClass.Order;
import com.example.oneshop.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WarhouseAdapter extends RecyclerView.Adapter<WarhouseAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Order> orderList;

    // Constructor for WarhouseAdapter
    public WarhouseAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set order details to views
        holder.tvOrderId.setText("Order ID: " + order.getOrderId());
        holder.tvTotalPrice.setText("Total Price: BDT " + order.getTotalPrice());
        holder.tvTotalProduct.setText("Total Product: " + order.getTotalProduct());
        holder.tvStatus.setText("Status: " + order.getOrderStatus());

        // Load product image using Picasso
        Picasso.get()
                .load(order.getProductImageUrl())
                .placeholder(R.drawable.placeholder_image)  // Placeholder image
                .error(R.drawable.error_image)  // Error image if loading fails
                .fit()
                .centerCrop()
                .into(holder.ivProductImage);

        // Handle item click to navigate to DetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsWarhouseActivity.class);

            intent.putExtra("orderId", order.getOrderId());
            intent.putExtra("productImage", order.getProductImageUrl());
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
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvTotalPrice, tvStatus, tvTotalProduct;
        ImageView ivProductImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvTotalProduct = itemView.findViewById(R.id.tv_total_product);

        }
    }
}
