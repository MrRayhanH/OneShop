package com.example.oneshop.Admin.Order.OutOfDelivery;

import android.os.Bundle;
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

public class OutofDeliveryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OutOfDeleveryAdapter outOfDeleveryAdapter;
    private ArrayList<Order> orderList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outof_delivery);
        recyclerView = findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        outOfDeleveryAdapter = new OutOfDeleveryAdapter(this, orderList);
        recyclerView.setAdapter(outOfDeleveryAdapter);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        loadAcceptedOrders();

    }
    private void loadAcceptedOrders() {
        ordersRef.orderByChild("orderStatus").equalTo("Out of delivery").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();

                if (!snapshot.exists()) {
                    Toast.makeText(OutofDeliveryActivity.this, "No Accepted Orders Found", Toast.LENGTH_SHORT).show();
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

                outOfDeleveryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OutofDeliveryActivity.this, "Failed to load accepted orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}