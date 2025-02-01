package com.example.oneshop.Products;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SellerProductView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapterSeller productAdapter;
    private List<ProductS> productList; // Using the ProductS class
    private DatabaseReference productRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product_view);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView_seller_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapterSeller(this, productList);
        recyclerView.setAdapter(productAdapter);

        // Firebase setup
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        productRef = FirebaseDatabase.getInstance().getReference("products");

        if (currentUser != null) {
            loadSellerProducts(currentUser.getUid()); // Load products for the logged-in seller
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSellerProducts(String userId) {
        productRef.orderByChild("seller_user_id").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            ProductS product = productSnapshot.getValue(ProductS.class);
                            if (product != null) {
                                productList.add(product); // Add product to the list
                            }
                        }
                        productAdapter.notifyDataSetChanged(); // Notify adapter of data change
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SellerProductView.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
