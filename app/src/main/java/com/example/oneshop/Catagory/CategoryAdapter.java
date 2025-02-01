package com.example.oneshop.Catagory;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private DatabaseReference databaseReference;

    private static final String CLOUD_NAME = "ddfkdln9b";
    private static final String API_KEY = "975694452154685";
    private static final String API_SECRET = "eDBFh-qA3BJJRtd87qmY3Yzeq3o";

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        databaseReference = FirebaseDatabase.getInstance().getReference("categories");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getCategory_name());

        // Load image using Picasso
        Picasso.get()
                .load(category.getImage_url())
                .placeholder(R.drawable.product) // Replace with your placeholder image
                .error(R.drawable.error_image) // Replace with your error image
                .into(holder.ivCategoryImage);

        // Delete category on delete icon click
        holder.ivDelete.setOnClickListener(v -> {
            // Prevent accidental propagation
            v.setClickable(false);
            deleteCategory(category.getCategory_id(), position);
            // Reactivate clickable after operation
            new Handler(Looper.getMainLooper()).postDelayed(() -> v.setClickable(true), 1000);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryImage, ivDelete;
        TextView tvCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryImage = itemView.findViewById(R.id.iv_category_image);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }

    private void deleteCategory(String categoryId, int position) {
        DatabaseReference categoryRef = databaseReference.child(categoryId);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String publicId = snapshot.child("image_public_id").getValue(String.class);

                    // Delete image from Cloudinary
                    if (publicId != null) {
                        deleteImageFromCloudinary(publicId);
                    }

                    // Remove category from Firebase
                    categoryRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                categoryList.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Category deleted successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete category", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to retrieve category data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteImageFromCloudinary(String publicId) {
        new Thread(() -> {
            try {
                Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                        "cloud_name", CLOUD_NAME,
                        "api_key", API_KEY,
                        "api_secret", API_SECRET
                ));

                // Delete image from Cloudinary
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (Exception e) {
                e.printStackTrace();

                // Use Handler to update the UI
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(context, "Failed to delete image from Cloudinary", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
