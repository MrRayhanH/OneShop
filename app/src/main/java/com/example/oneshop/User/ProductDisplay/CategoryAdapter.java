package com.example.oneshop.User.ProductDisplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.Admin.Catagory.Category;
import com.example.oneshop.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<Category> categoryList;
    private OnItemClickListener onItemClickListener;
    private int selectedItemPosition = -1;
    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_catagoris, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getCategory_name());

        // Load category image from Cloudinary
        Picasso.get()
                .load(category.getImage_url())
                .placeholder(R.drawable.product) // Default image
                .into(holder.ivCategoryImage);

        // Set the background color to black when item is clicked
        // Set the background color of the ImageView (circle) to black when item is clicked
        if (position == selectedItemPosition) {
            holder.ivCategoryImage.setBackgroundColor(context.getResources().getColor(R.color.status_bar_color_forget_pass)); // Set circle background color to black
        } else {
            holder.ivCategoryImage.setBackgroundColor(context.getResources().getColor(R.color.Primary)); // Default background color (if not clicked)
        }

        // Handle category clicks
        holder.itemView.setOnClickListener(v -> {
            int temp = position;
            selectedItemPosition = temp;  // Set the clicked item as selected
            notifyDataSetChanged();  // Refresh the adapter to update the UI
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position, category.getCategory_name());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryImage;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryImage = itemView.findViewById(R.id.iv_category);
            tvCategoryName = itemView.findViewById(R.id.tv_category);
        }
    }

    // Interface for click event
    public interface OnItemClickListener {
        void onItemClick(int position, String categoryName);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.onItemClickListener = listener;
    }

    // Method to update category list dynamically
    public void updateCategoryList(List<Category> newCategoryList) {
        categoryList.clear();
        categoryList.addAll(newCategoryList);
        notifyDataSetChanged();
    }
}
