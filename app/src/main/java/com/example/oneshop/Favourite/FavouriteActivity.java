package com.example.oneshop.Favourite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.Card.CardActivity;
import com.example.oneshop.ProductDisplay.ProductsDisplay;
import com.example.oneshop.R;
import com.example.oneshop.SettingActivity;
import com.example.oneshop.Products.ProductS;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private RecyclerView recyclerView;
    private List<ProductS> productList;
    private FavouriteAdapter favouriteAdapter;
    private DatabaseReference productRef, favRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance();
        productRef = FirebaseDatabase.getInstance().getReference("products");
        favRef = database.getReference("Favorites");

        // Bottom navigation buttons
        ImageView home = findViewById(R.id.iv_home);
        ImageView card = findViewById(R.id.iv_card);
        ImageView setting = findViewById(R.id.iv_setting);
        ImageView back = findViewById(R.id.iv_back);
        recyclerView = findViewById(R.id.recycler_view);

        home.setOnClickListener(v -> startActivity(new Intent(FavouriteActivity.this, ProductsDisplay.class)));
        card.setOnClickListener(v -> startActivity(new Intent(FavouriteActivity.this, CardActivity.class)));
        setting.setOnClickListener(v -> startActivity(new Intent(FavouriteActivity.this, SettingActivity.class)));
        back.setOnClickListener(v -> finish());

        // Get the current user ID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to view your favorites.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();  // Get current user UID

        // Set up RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));  // Display in grid (2 columns)
        productList = new ArrayList<>();
        favouriteAdapter = new FavouriteAdapter(this, productList);
        recyclerView.setAdapter(favouriteAdapter);

        loadProducts(userId);
    }

    private void loadProducts(String userId) {
        favRef.orderByChild("user_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot favSnapshot : snapshot.getChildren()) {
                    String productId = favSnapshot.child("product_id").getValue(String.class);
                    if (productId != null) {
                        // Fetch the product details using product_id
                        fetchProductDetails(productId);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavouriteActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
        favouriteAdapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(FavouriteActivity.this, FavouriteProductDetails.class);
            intent.putExtra("PRODUCT_ID", product.getProduct_id());
            intent.putExtra("PRODUCT_NAME", product.getProduct_name());
            intent.putExtra("PRODUCT_PRICE", product.getPrice());
            intent.putExtra("PRODUCT_DESCRIPTION", product.getDescription());
            intent.putExtra("PRODUCT_IMAGE_URL", product.getImage_url());
            intent.putExtra("PRODUCT_QUANTITY", product.getStock_quantity());
            startActivity(intent);
        });
    }

    private void fetchProductDetails(String productId) {
        productRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductS product = snapshot.getValue(ProductS.class);
                if (product != null) {
                    productList.add(product);
                    favouriteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavouriteActivity.this, "Failed to fetch product details", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
