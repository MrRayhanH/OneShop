package com.example.oneshop.Products;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapterSeller extends RecyclerView.Adapter<ProductAdapterSeller.ViewHolder> {

    private Context context;
    private List<ProductS> productList;

    // Constructor
    public ProductAdapterSeller(Context context, List<ProductS> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_seller_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductS product = productList.get(position); // Get product data

        // Bind product data to UI components
        holder.tvProductName.setText(product.getProduct_name());
        holder.tvProductPrice.setText("Price: $" + product.getPrice());
        holder.tvProductStock.setText("Stock: " + product.getStock_quantity());

        // Load product image using Picasso
        Picasso.get()
                .load(product.getImage_url())
                .placeholder(R.drawable.product) // Placeholder image
                .error(R.drawable.error_image) // Error image
                .into(holder.ivProductImage);
    }

    @Override
    public int getItemCount() {
        return productList.size(); // Return the size of the product list
    }

    // ViewHolder Class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductStock;
        ImageView ivProductImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductStock = itemView.findViewById(R.id.tv_product_stock);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
        }
    }
}
