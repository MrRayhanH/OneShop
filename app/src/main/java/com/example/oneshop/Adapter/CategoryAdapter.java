package com.example.oneshop.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private Cursor cursor;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Constructor to initialize context and cursor
    public CategoryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_catagoris, parent, false);
        return new CategoryViewHolder(view, listener);
    }

    @Override

    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            // Safely get column indices
            int nameIndex = cursor.getColumnIndex("categoryName");
            int imageIndex = cursor.getColumnIndex("categoryImageUri");

            if (nameIndex != -1 && imageIndex != -1) {
                String categoryName = cursor.getString(nameIndex);
                byte[] imageByteArray = cursor.getBlob(imageIndex);

                // Bind data to the view
                holder.tvCategoryName.setText(categoryName);

                // Convert byte array to bitmap and display in ImageView
                if (imageByteArray != null && imageByteArray.length > 0) {
                    Bitmap categoryImage = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                    holder.ivCategoryImage.setImageBitmap(categoryImage);
                } else {
                    holder.ivCategoryImage.setImageResource(R.drawable.product); // Use a placeholder
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    // ViewHolder for the RecyclerView
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        ImageView ivCategoryImage;

        public CategoryViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category);
            ivCategoryImage = itemView.findViewById(R.id.iv_category);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                    }
            });

        }
    }

    // Update the cursor when new data is available
    public void swapCursor(Cursor newCursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }



}
