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

public class DeleteProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DeleteAdapter deleteAdapter;
    private List<ProductS> productList;
    private DatabaseReference productRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        productRef = FirebaseDatabase.getInstance().getReference("products");

        // Initialize Adapter
        productList = new ArrayList<>();
        deleteAdapter = new DeleteAdapter(this, productList);
        recyclerView.setAdapter(deleteAdapter);

        // Load products added by the seller
        if (currentUser != null) {
            loadSellerProducts(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSellerProducts(String sellerUserId) {
        productRef.orderByChild("seller_user_id").equalTo(sellerUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            ProductS product = productSnapshot.getValue(ProductS.class);
                            if (product != null) {
                                productList.add(product);
                            }
                        }
                        deleteAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DeleteProductActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
