package com.example.oneshop.Seller.Order;

import android.os.Bundle;
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

public class OrderActivitySeller extends AppCompatActivity {

    private ArrayList<Order> orders;
    private OrderAdapterSeller orderAdapter;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order2);

        RecyclerView recyclerView = findViewById(R.id.order_list_view);
        orders = new ArrayList<>();
        orderAdapter = new OrderAdapterSeller(this, orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        loadSellerOrders();
    }

    private void loadSellerOrders() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String sellerId = currentUser.getUid();
            ordersRef.orderByChild("sellerId").equalTo(sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orders.clear();
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null) orders.add(order);
                    }
                    orderAdapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(OrderActivitySeller.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
