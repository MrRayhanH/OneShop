package com.example.oneshop.User.ProductDisplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.ProductsClass.ProductS;
import com.example.oneshop.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<ProductS> productList;
    private OnItemClickListener onItemClickListener;

    public ProductAdapter(Context context, List<ProductS> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_custom, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductS product = productList.get(position);
        holder.tvProductName.setText(product.getProduct_name());
        holder.tvProductPrice.setText("BDT " + product.getPrice());
        Picasso.get().load(product.getImage_url()).placeholder(R.drawable.placeholder_image).into(holder.ivProductImage);
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

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice;
        ImageView ivProductImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_Name);
            tvProductPrice = itemView.findViewById(R.id.tv_Product_Price);
            ivProductImage = itemView.findViewById(R.id.product_image);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ProductS product);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
