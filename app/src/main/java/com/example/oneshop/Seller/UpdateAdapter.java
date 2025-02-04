package com.example.oneshop.Seller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.Products.ProductS;
import com.example.oneshop.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.ViewHolder> {

    private Context context;
    private List<ProductS> productList;

    public UpdateAdapter(Context context, List<ProductS> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_seller_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductS product = productList.get(position);

        // Set Product Name, Price, and Stock Quantity
        holder.tvProductName.setText(product.getProduct_name());
        holder.tvProductPrice.setText("Price: $" + product.getPrice());
        holder.tvProductStock.setText("Stock: " + product.getStock_quantity());  // Add stock display

        // Load Product Image
        Picasso.get()
                .load(product.getImage_url())
                .placeholder(R.drawable.product)
                .into(holder.ivProductImage);

        // Click to open UpdateProductActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateProductActivity.class);
            intent.putExtra("product_id", product.getProduct_id());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductStock;  // Added stock TextView
        ImageView ivProductImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductStock = itemView.findViewById(R.id.tv_product_stock);  // Ensure this exists in XML
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
        }
    }
}
