package com.example.oneshop.User.Order;

import android.net.Uri;
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
import com.example.oneshop.OrderClass.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private ArrayList<Order> orders;
    private OrderAdapter orderAdapter;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.order_list_view);
        orders = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);

        // Firebase Database Reference
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Load User Orders
        loadUserOrders();
        statusBar();
    }

    private void loadUserOrders() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Query orders where userId matches the logged-in user
            ordersRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orders.clear();

                    if (!snapshot.exists()) {
                        Toast.makeText(OrderActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        String orderId = orderSnapshot.child("orderId").getValue(String.class);
                        Double totalPrice = orderSnapshot.child("totalPrice").getValue(Double.class);
                        Integer totalProduct = orderSnapshot.child("totalProduct").getValue(Integer.class);
                        String productId = orderSnapshot.child("productId").getValue(String.class);
                        String sellerId = orderSnapshot.child("sellerId").getValue(String.class);
                        String orderStatus = orderSnapshot.child("orderStatus").getValue(String.class);
                        String orderDate = orderSnapshot.child("orderDate").getValue(String.class);
                        String imageUrl = orderSnapshot.child("image_url").getValue(String.class);
                        if(imageUrl!= null) {
                           // Toast.makeText(OrderActivity.this, "Image URL: " + imageUrl, Toast.LENGTH_SHORT).show();
                        }
                        if(totalPrice== null){
                            Toast.makeText(OrderActivity.this, "Total Price: " + totalPrice, Toast.LENGTH_SHORT).show();
                        }
                        if (orderId != null && productId != null) {
                            Order order = new Order(orderId, totalPrice, totalProduct, productId, userId, sellerId, orderStatus, orderDate, imageUrl);
                            orders.add(order);
                        }
                    }

                    orderAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(OrderActivity.this, "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please log in to view your orders.", Toast.LENGTH_SHORT).show();
        }
    }

    private void statusBar() {
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


//package com.example.oneshop.User.Order;
//
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.oneshop.R;
//import com.example.oneshop.OrderClass.Order;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//
//public class OrderActivity extends AppCompatActivity {
//
//    private ArrayList<Order> orders;
//    private OrderAdapter orderAdapter;
//    private DatabaseReference ordersRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_order);
//
//        // Initialize views
//        RecyclerView recyclerView = findViewById(R.id.order_list_view);
//        orders = new ArrayList<>();
//        orderAdapter = new OrderAdapter(this, orders);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set layout manager to LinearLayout
//        recyclerView.setAdapter(orderAdapter);
//
//        // Get the reference to Orders in Firebase
//        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
//
//        // Load user orders
//        loadUserOrders();
//        statusBar();
//    }
//
//    private void loadUserOrders() {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (currentUser != null) {
//            String userId = currentUser.getUid();  // Get current user's UID
//            ordersRef.orderByChild("user_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    orders.clear();  // Clear any existing orders
//
//                    if (!snapshot.exists()) {
//                        Toast.makeText(OrderActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    // Loop through the orders and add them to the list
//                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
//                        Order order = orderSnapshot.getValue(Order.class);
//                        if (order != null) {
//                            orders.add(order);  // Add the order to the list
//                        }
//                    }
//
//                    // Notify the adapter that the data has changed
//                    orderAdapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    // Handle error when data is not retrieved
//                    Toast.makeText(OrderActivity.this, "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            // Handle case where the user is not logged in
//            Toast.makeText(this, "Please log in to view your orders.", Toast.LENGTH_SHORT).show();
//        }
//    }
//    private void statusBar(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));
//        }
//
//        // Make the status bar icons light (for dark background)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }
//    }
//}
