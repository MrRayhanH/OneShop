package com.example.oneshop.Admin.Order.Warehouse;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oneshop.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class DetailsWarhouseActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_warhouse);
        databaseReference = FirebaseDatabase.getInstance().getReference("orders");

        // Get order details from the intent
        String orderId = getIntent().getStringExtra("orderId");
        String productImage = getIntent().getStringExtra("productImage");
        double totalPrice = getIntent().getDoubleExtra("totalPrice", 0.0); // Fixed: Use getDoubleExtra
        int totalProduct = getIntent().getIntExtra("totalProduct", 0); // Fixed: Use getIntExtra
        String productId = getIntent().getStringExtra("productId");
        String userId = getIntent().getStringExtra("userId");
        String sellerId = getIntent().getStringExtra("sellerId");
        String orderStatus = getIntent().getStringExtra("orderStatus");
        String orderDate = getIntent().getStringExtra("orderDate");

        // Initialize UI components
        ImageView ivProductImage = findViewById(R.id.iv_product);
        TextView tvOrderId = findViewById(R.id.tvOrderId);
        TextView tvTotalPrice = findViewById(R.id.tvTotalPrice);
        TextView tvTotalProduct = findViewById(R.id.tvTotalProduct);
        TextView tvOrderStatus = findViewById(R.id.tvOrderStatus);
        TextView tvOrderDate = findViewById(R.id.tvOrderDate);
        Button btUpdateStatus = findViewById(R.id.btUpdateStatus);

        // Set data to UI
        tvOrderId.setText("Order ID: " + orderId);
        tvTotalPrice.setText("Total Price: BDT " + totalPrice); // Fixed: Ensure proper format
        tvTotalProduct.setText("Total Products: " + totalProduct); // Fixed: Ensure proper format
        tvOrderStatus.setText("Order Status: " + orderStatus);
        tvOrderDate.setText("Order Date: " + orderDate);

        // Load the product image using Picasso
        if (!TextUtils.isEmpty(productImage)) {
            Picasso.get().load(productImage).placeholder(R.drawable.product)
                    .error(R.drawable.product).into(ivProductImage);
        } else {
            ivProductImage.setImageResource(R.drawable.product);
        }

        // Update order status button click
        btUpdateStatus.setOnClickListener(view -> {
            String newStatus = "Out of delivery";
            databaseReference.child(orderId).child("orderStatus").setValue(newStatus).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Order status updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to update order status", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}