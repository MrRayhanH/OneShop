package com.example.oneshop.Admin.Order.Deleverd;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.Admin.Order.Warehouse.WarhouseActivity;
import com.example.oneshop.Admin.Order.Warehouse.WarhouseAdapter;
import com.example.oneshop.OrderClass.Order;
import com.example.oneshop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderdDeleverdActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderdDeleverdAdapter orderdDeleverdAdapter;
    private ArrayList<Order> orderList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderd_deleverd);

        recyclerView = findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        orderdDeleverdAdapter = new OrderdDeleverdAdapter(this, orderList);
        recyclerView.setAdapter(orderdDeleverdAdapter);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        loadAcceptedOrders();


    }

    private void loadAcceptedOrders() {
        ordersRef.orderByChild("orderStatus").equalTo("Product Delivered").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();

                if (!snapshot.exists()) {
                    Toast.makeText(OrderdDeleverdActivity.this, "No Products Found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String orderId = orderSnapshot.child("orderId").getValue(String.class);
                    Double totalPrice = orderSnapshot.child("totalPrice").getValue(Double.class);
                    Integer totalProduct = orderSnapshot.child("totalProduct").getValue(Integer.class);
                    String productId = orderSnapshot.child("productId").getValue(String.class);
                    String userId = orderSnapshot.child("userId").getValue(String.class);
                    String sellerId = orderSnapshot.child("sellerId").getValue(String.class);
                    String orderStatus = orderSnapshot.child("orderStatus").getValue(String.class);
                    String orderDate = orderSnapshot.child("orderDate").getValue(String.class);
                    String imageUrl = orderSnapshot.child("image_url").getValue(String.class);

                    if (orderId != null && productId != null && userId != null) {
                        Order order = new Order(orderId, totalPrice, totalProduct, productId, userId, sellerId, orderStatus, orderDate, imageUrl);
                        orderList.add(order);
                    }
                }

                orderdDeleverdAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderdDeleverdActivity.this, "Failed to load Products Delivered: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        statusBar();
    }
    private void statusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));
        }

        // Make the status bar icons light (for dark background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}