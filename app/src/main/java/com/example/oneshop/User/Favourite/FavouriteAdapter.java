package com.example.oneshop.User.Favourite;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.ProductsClass.ProductS;
import com.example.oneshop.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {

    private Context context;
    private List<ProductS> productList;
    private FirebaseDatabase database;
    private DatabaseReference favRef;
    private FavouriteAdapter.OnItemClickListener onItemClickListener;
    public FavouriteAdapter(Context context, List<ProductS> productList) {
        this.context = context;
        this.productList = productList;
        this.database = FirebaseDatabase.getInstance();
        this.favRef = database.getReference("Favorites");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductS product = productList.get(position);

        // Bind product details to UI
        holder.tvProductName.setText(product.getProduct_name());
        holder.tvProductPrice.setText("$ " + product.getPrice());

        // Load product image using Picasso (Cloudinary URL)
        Picasso.get()
                .load(product.getImage_url())  // Cloudinary image URL
                .placeholder(R.drawable.product)  // Placeholder while loading
                .into(holder.ivProductImage);

        // On item click, open the product details page
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FavouriteProductDetails.class);
            intent.putExtra("PRODUCT_ID", product.getProduct_id());
            context.startActivity(intent);
        });
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice;
        ImageView ivProductImage, ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_Name);
            tvProductPrice = itemView.findViewById(R.id.tv_Product_Price);
            ivProductImage = itemView.findViewById(R.id.product_image);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ProductS product);
    }

    public void setOnItemClickListener(FavouriteAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
