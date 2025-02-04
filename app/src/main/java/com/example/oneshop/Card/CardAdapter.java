package com.example.oneshop.Card;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.ProductDisplay.ProductDetailsActivity;
import com.example.oneshop.Products.ProductS;
import com.example.oneshop.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private final Context context;
    private final List<ProductS> productList;
    private final DatabaseReference cartRef;
    private final OnItemClickListener onItemClickListener;

    public CardAdapter(Context context, List<ProductS> productList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.productList = productList;
        this.cartRef = FirebaseDatabase.getInstance().getReference("Cart");
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductS product = productList.get(position);

        // Bind product details to UI
        holder.tvProductName.setText(product.getProduct_name());
        holder.tvProductPrice.setText("BDT: " + product.getPrice());
        holder.tvProductQuantity.setText("Quantity "+ product.getQuantity());

        // Load product image using Picasso
        Picasso.get()
                .load(product.getImage_url())  // Cloudinary image URL
                .placeholder(R.drawable.product)  // Placeholder while loading
                .into(holder.ivProductImage);

        // Handle delete functionality
        holder.ivDelete.setOnClickListener(v -> deleteProductFromCart(product, position));

        // Handle quantity increase (iv_add) and decrease (iv_minas) functionality
        holder.iv_add.setOnClickListener(v -> updateProductQuantity(product, position, 1));  // Increase quantity
        holder.iv_minas.setOnClickListener(v -> updateProductQuantity(product, position, -1));  // Decrease quantity
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void deleteProductFromCart(ProductS product, int position) {
        String productId = product.getProduct_id();
        cartRef.orderByChild("product_id").equalTo(productId)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        for (com.google.firebase.database.DataSnapshot cartSnapshot : snapshot.getChildren()) {
                            cartSnapshot.getRef().removeValue(); // Remove product from cart
                            productList.remove(position); // Remove from list
                            notifyItemRemoved(position); // Update RecyclerView
                            Toast.makeText(context, "Product deleted from cart", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                        Toast.makeText(context, "Error removing product from cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProductQuantity(ProductS product, int position, int change) {
        String productId = product.getProduct_id();
        cartRef.orderByChild("product_id").equalTo(productId)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        for (com.google.firebase.database.DataSnapshot cartSnapshot : snapshot.getChildren()) {
                            int currentQuantity = cartSnapshot.child("quantity").getValue(Integer.class);
                            if (currentQuantity > 0) {

                                int updatedQuantity = currentQuantity + change;
                                if (updatedQuantity > 0 ) {
                                    cartSnapshot.getRef().child("quantity").setValue(updatedQuantity);
                                    // Notify item change for updated quantity
                                    notifyItemChanged(position);

                                    // Recalculate the totals in the parent activity (CardActivity)
                                    onItemClickListener.onQuantityUpdated();  // This will call the method in the parent activity to recalculate totals
                                    Toast.makeText(context, "Quantity updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Quantity can not be negative", Toast.LENGTH_SHORT).show();
                                    //deleteProductFromCart(product, position); // Remove item if quantity is 0
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                        Toast.makeText(context, "Error updating quantity", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductQuantity;
        ImageView ivProductImage, ivDelete, iv_minas, iv_add;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductQuantity = itemView.findViewById(R.id.tv_product_quantity);
            ivDelete = itemView.findViewById(R.id.iv_cartDelete);
            iv_minas = itemView.findViewById(R.id.iv_minas);
            iv_add = itemView.findViewById(R.id.iv_add);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ProductS product);
        void onQuantityUpdated();  // New method to notify the activity when quantity is updated
    }
}


//package com.example.oneshop.Card;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.oneshop.ProductDisplay.ProductDetailsActivity;
//import com.example.oneshop.Products.ProductS;
//import com.example.oneshop.R;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
//
//    private final Context context;
//    private final List<ProductS> productList;
//    private final DatabaseReference cartRef;
//    private final OnItemClickListener onItemClickListener;
//
//    public CardAdapter(Context context, List<ProductS> productList, OnItemClickListener onItemClickListener) {
//        this.context = context;
//        this.productList = productList;
//        this.cartRef = FirebaseDatabase.getInstance().getReference("Cart");
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        ProductS product = productList.get(position);
//
//        // Bind product details to UI
//        holder.tvProductName.setText(product.getProduct_name());
//        holder.tvProductPrice.setText("BDT: " + product.getPrice());
//
//        // Load product image using Picasso
//        Picasso.get()
//                .load(product.getImage_url())  // Cloudinary image URL
//                .placeholder(R.drawable.product)  // Placeholder while loading
//                .into(holder.ivProductImage);
//
//        // Open product details page on item click
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, ProductDetailsActivity.class);
//            intent.putExtra("PRODUCT_ID", product.getProduct_id());
//            context.startActivity(intent);
//        });
//
//        // Handle delete functionality
//        holder.ivDelete.setOnClickListener(v -> deleteProductFromCart(product, position));
//
//        // Handle quantity increase (iv_add) and decrease (iv_minas) functionality
//        holder.iv_add.setOnClickListener(v -> updateProductQuantity(product, position, 1));  // Increase quantity
//        holder.iv_minas.setOnClickListener(v -> updateProductQuantity(product, position, -1));  // Decrease quantity
//    }
//
//    @Override
//    public int getItemCount() {
//        return productList.size();
//    }
//
//    private void deleteProductFromCart(ProductS product, int position) {
//        String productId = product.getProduct_id();
//        cartRef.orderByChild("product_id").equalTo(productId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
//                            cartSnapshot.getRef().removeValue(); // Remove product from cart
//                            productList.remove(position); // Remove from list
//                            notifyItemRemoved(position); // Update RecyclerView
//                            Toast.makeText(context, "Product deleted from cart", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(context, "Error removing product from cart", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void updateProductQuantity(ProductS product, int position, int change) {
//        String productId = product.getProduct_id();
//        cartRef.orderByChild("product_id").equalTo(productId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
//                            int currentQuantity = cartSnapshot.child("quantity").getValue(Integer.class);
//                            if (currentQuantity > 0) {
//
//                                int updatedQuantity = currentQuantity + change;
//                                if (updatedQuantity > 0 ) {
//                                    cartSnapshot.getRef().child("quantity").setValue(updatedQuantity);
//                                    // Notify item change for updated quantity
//                                    notifyItemChanged(position);
//
//                                    // Recalculate the totals in the parent activity (CardActivity)
//                                    onItemClickListener.onQuantityUpdated();  // This will call the method in the parent activity to recalculate totals
//                                    Toast.makeText(context, "Quantity updated", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(context, "Quantity can not ne negative", Toast.LENGTH_SHORT).show();
//                                    //deleteProductFromCart(product, position); // Remove item if quantity is 0
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(context, "Error updating quantity", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView tvProductName, tvProductPrice, tvProductQuantity;
//        ImageView ivProductImage, ivDelete, iv_minas, iv_add;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvProductName = itemView.findViewById(R.id.tv_product_name);
//            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
//            ivProductImage = itemView.findViewById(R.id.iv_product_image);
//            tvProductQuantity = itemView.findViewById(R.id.tv_product_quantity);
//            ivDelete = itemView.findViewById(R.id.iv_cartDelete);
//            iv_minas = itemView.findViewById(R.id.iv_minas);
//            iv_add = itemView.findViewById(R.id.iv_add);
//        }
//    }
//
//    public interface OnItemClickListener {
//        void onItemClick(ProductS product);
//        void onQuantityUpdated();  // New method to notify the activity when quantity is updated
//    }
//}
