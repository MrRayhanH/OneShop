package com.example.oneshop.Seller;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.oneshop.R;
import com.example.oneshop.Order.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderActivitySeller extends AppCompatActivity {

    private ArrayList<Order> orders;
    private OrderAdapter orderAdapter;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize views
        RecyclerView recyclerView = findViewById(R.id.order_list_view);
        orders = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set layout manager to LinearLayout
        recyclerView.setAdapter(orderAdapter);

        // Get the reference to Orders in Firebase
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        // Load seller orders
        loadSellerOrders();
        statusbar();
    }

    private void loadSellerOrders() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String sellerId = currentUser.getUid();  // Get current seller's UID
            ordersRef.orderByChild("seller_id").equalTo(sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orders.clear();  // Clear any existing orders

                    if (!snapshot.exists()) {
                        Toast.makeText(OrderActivitySeller.this, "No orders found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Loop through the orders and add them to the list
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null) {
                            orders.add(order);  // Add the order to the list
                        }
                    }

                    // Notify the adapter that the data has changed
                    orderAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error when data is not retrieved
                    Toast.makeText(OrderActivitySeller.this, "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle case where the seller is not logged in
            Toast.makeText(this, "Please log in to view your orders.", Toast.LENGTH_SHORT).show();
        }
    }
    private void statusbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
