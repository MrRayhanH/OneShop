package com.example.oneshop.Admin.Order.Accept;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.OrderClass.Order;
import com.example.oneshop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private ArrayList<Order> orderList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order3);

        recyclerView = findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        recyclerView.setAdapter(orderAdapter);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        loadAcceptedOrders();
    }

    private void loadAcceptedOrders() {
        ordersRef.orderByChild("orderStatus").equalTo("Accepted").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                if (!snapshot.exists()) {
                    Toast.makeText(OrderActivity.this, "No Accepted Orders Found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
