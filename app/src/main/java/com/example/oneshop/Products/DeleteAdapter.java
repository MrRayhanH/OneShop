package com.example.oneshop.Products;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.oneshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.List;

public class DeleteAdapter extends RecyclerView.Adapter<DeleteAdapter.ViewHolder> {

    private Context context;
    private List<ProductS> productList;
    private DatabaseReference productRef;
    private static final String CLOUD_NAME = "ddfkdln9b";
    private static final String API_KEY = "975694452154685";
    private static final String API_SECRET = "eDBFh-qA3BJJRtd87qmY3Yzeq3o";

    public DeleteAdapter(Context context, List<ProductS> productList) {
        this.context = context;
        this.productList = productList;
        this.productRef = FirebaseDatabase.getInstance().getReference("products");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delete_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductS product = productList.get(position);

        // Bind product details to UI
        holder.tvProductName.setText(product.getProduct_name());
        holder.tvProductPrice.setText("Price: $" + product.getPrice());
        holder.tvProductStock.setText("Stock: " + product.getStock_quantity());

        // Load product image using Picasso
        Picasso.get()
                .load(product.getImage_url())
                .placeholder(R.drawable.product)
                .into(holder.ivProductImage);

        // Delete product when delete icon is clicked
        holder.ivDelete.setOnClickListener(v -> deleteProduct(product, position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductStock;
        ImageView ivProductImage, ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductStock = itemView.findViewById(R.id.tv_product_stock);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }

    private void deleteProduct(ProductS product, int position) {
        String productId = product.getProduct_id();
        String imagePublicId = product.getImage_public_id();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Prevent unauthorized deletion
        if (currentUser == null || !currentUser.getUid().equals(product.getSeller_user_id())) {
            Toast.makeText(context, "You can only delete your own products!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Delete image from Cloudinary
        if (imagePublicId != null) {
            new Thread(() -> {
                try {
                    Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                            "cloud_name", CLOUD_NAME,
                            "api_key", API_KEY,
                            "api_secret", API_SECRET
                    ));

                    cloudinary.uploader().destroy(imagePublicId, ObjectUtils.emptyMap());
                } catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(context, "Failed to delete image from Cloudinary", Toast.LENGTH_SHORT).show());
                }
            }).start();
        }

        // Remove product from Firebase Realtime Database
        productRef.child(productId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    productList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Product deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show());
    }
}
