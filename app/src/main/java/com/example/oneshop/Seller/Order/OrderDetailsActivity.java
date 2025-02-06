package com.example.oneshop.Seller.Order;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oneshop.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView orderIdTextView, totalPriceTextView, totalProductsTextView, productIdTextView,
            userIdTextView, sellerIdTextView, orderStatusTextView, orderDateTextView;
    private Button acceptButton, cancelButton;
    private DatabaseReference ordersRef;

    private String orderId, userId, sellerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        // Initialize Firebase Database Reference
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Initialize views
        orderIdTextView = findViewById(R.id.tv_order_id);
        totalPriceTextView = findViewById(R.id.tv_order_total_price);
        totalProductsTextView = findViewById(R.id.tv_order_total_products);
        productIdTextView = findViewById(R.id.tv_order_product_id);
        userIdTextView = findViewById(R.id.tv_order_user_id);
        sellerIdTextView = findViewById(R.id.tv_order_seller_id);
        orderStatusTextView = findViewById(R.id.tv_order_status);
        orderDateTextView = findViewById(R.id.tv_order_date);
        acceptButton = findViewById(R.id.btn_accept_order);
        cancelButton = findViewById(R.id.btn_cancel_order);

        // Get data from Intent
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        double totalPrice = intent.getDoubleExtra("totalPrice", 0);
        int totalProduct = intent.getIntExtra("totalProduct", 0);
        String productId = intent.getStringExtra("productId");
        userId = intent.getStringExtra("userId");
        sellerId = intent.getStringExtra("sellerId");
        String orderStatus = intent.getStringExtra("orderStatus");
        String orderDate = intent.getStringExtra("orderDate");

        // Set data to TextViews
        orderIdTextView.setText("Order ID: " + orderId);
        totalPriceTextView.setText("Total Price: BDT " + totalPrice);
        totalProductsTextView.setText("Total Products: " + totalProduct);
        productIdTextView.setText("Product ID: " + productId);
        userIdTextView.setText("User ID: " + userId);
        sellerIdTextView.setText("Seller ID: " + sellerId);
        orderStatusTextView.setText("Order Status: " + orderStatus);
        orderDateTextView.setText("Order Date: " + orderDate);

        // Accept Order Button
        acceptButton.setOnClickListener(v -> updateOrderStatus("Accepted"));

        // Cancel Order Button
        cancelButton.setOnClickListener(v -> updateOrderStatus("Cancelled"));
        statusBar();
    }

    private void updateOrderStatus(String newStatus) {
        if (orderId != null) {
            ordersRef.child(orderId).child("orderStatus").setValue(newStatus)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(OrderDetailsActivity.this, "Order " + newStatus, Toast.LENGTH_SHORT).show();
                        finish(); // Close activity after update
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(OrderDetailsActivity.this, "Failed to update order", Toast.LENGTH_SHORT).show());
        }
    }
    private void statusBar(){
        // Update the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));
        }

        // Set light status bar icons for dark backgrounds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
