package com.example.oneshop.User.ProductDisplay;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oneshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView imageView, iv_favourite;
    private TextView tv_productName, tv_productPrice, tv_productQuantity, tv_productDescription;
    private String productName, productDescription, imageUrl, productId, sellerid;
    private int productQuantity;
    private double  productPrice;
    private FirebaseDatabase database;
    private DatabaseReference favoritesRef;
    private DatabaseReference cartRef;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize views
        imageView = findViewById(R.id.iv_product_image);
        tv_productName = findViewById(R.id.tv_ProductName);
        tv_productPrice = findViewById(R.id.tv_ProductPrice);
        tv_productQuantity = findViewById(R.id.tv_productQuantity);
        tv_productDescription = findViewById(R.id.tv_ProductDescription);
        Button btnAddToCart = findViewById(R.id.btn_addToCard);
        iv_favourite = findViewById(R.id.iv_favourite_details);

        // get current user id
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if user is logged in
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        favoritesRef = database.getReference("Favorites");
        cartRef = database.getReference("Cart");

        // Get product details passed from the previous activity
        Intent intent = getIntent();
        productId = intent.getStringExtra("PRODUCT_ID");  // Now productId is a String
        productName = intent.getStringExtra("PRODUCT_NAME");
        productPrice = intent.getDoubleExtra("PRODUCT_PRICE",0.0);
        productQuantity = intent.getIntExtra("PRODUCT_QUANTITY",0);  // Ensure quantity is passed
        productDescription = intent.getStringExtra("PRODUCT_DESCRIPTION");
        imageUrl = intent.getStringExtra("PRODUCT_IMAGE_URL");
        sellerid = intent.getStringExtra("SELLER_ID");
        //userId = intent.getStringExtra("USER_ID");

        // Debugging: Ensure product details are passed correctly
//        Toast.makeText(this, "User ID 2: " + userId, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Product Name: " + productName, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Product Price: " + productPrice, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Product Quantity: " + productQuantity, Toast.LENGTH_SHORT).show();

        if (productId != null && !productId.isEmpty()) {
            // Set product details in the UI
            tv_productName.setText(productName);
            tv_productPrice.setText("Total BDT: " + productPrice);
            tv_productQuantity.setText("Total Quantity: " + productQuantity);  // Display the quantity
            tv_productDescription.setText("Product Details: " + productDescription);

            // Load image using Picasso
            Picasso.get().load(imageUrl).into(imageView);
        } else {
            Toast.makeText(this, "Product ID is missing", Toast.LENGTH_SHORT).show();
        }

        // Add to Cart button
        btnAddToCart.setOnClickListener(v -> addToCart());

        // Add to Favorites button
        iv_favourite.setOnClickListener(v -> addToFavorites());
    }

    private void addToFavorites() {
        // Check if product already exists in the favorites
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites");

        // Query to find if the current product already exists in the user's favorites
        favoritesRef.orderByChild("user_id").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isAlreadyFavorite = false;

                        // Iterate over the results to check if the product_id exists
                        for (DataSnapshot favSnapshot : snapshot.getChildren()) {
                            String existingProductId = favSnapshot.child("product_id").getValue(String.class);
                            if (existingProductId != null && existingProductId.equals(productId)) {
                                isAlreadyFavorite = true;
                                break;
                            }
                        }

                        // If product is not in favorites, add it
                        if (!isAlreadyFavorite) {
                            Map<String, Object> favoriteData = new HashMap<>();
                            favoriteData.put("user_id", userId);
                            favoriteData.put("product_id", productId);

                            favoritesRef.push().setValue(favoriteData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ProductDetailsActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ProductDetailsActivity.this, "Failed to add to Favorites", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Product is already in favorites
                            Toast.makeText(ProductDetailsActivity.this, "Product is already in Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProductDetailsActivity.this, "Error checking favorites", Toast.LENGTH_SHORT).show();
                    }
                });
        statusBar();
    }


    private void addToCart() {
        // Prepare data for Firebase
        Map<String, Object> cartData = new HashMap<>();
        cartData.put("user_id", userId);
        cartData.put("product_id", productId);  // Store productId as a String
        cartData.put("quantity", 1); // Default to 1 for simplicity

        // Add product to cart in Firebase
        // Check if the product is already in the cart
        cartRef.orderByChild("product_id")
                .equalTo(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // The product is already in the cart, notify the user
                            for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                                String cartUserId = cartSnapshot.child("user_id").getValue(String.class);
                                if (cartUserId != null && cartUserId.equals(userId)) {
                                    // Product already in the cart
                                    Toast.makeText(ProductDetailsActivity.this, "Product is already in the cart", Toast.LENGTH_SHORT).show();
                                    return; // Exit the method if product is already in the cart
                                }
                            }
                        }

                        // If the product is not in the cart, add it
                        Map<String, Object> cartData = new HashMap<>();
                        cartData.put("user_id", userId);
                        cartData.put("product_id", productId);
                        cartData.put("quantity", 1); // Default quantity
                        cartData.put("seller_user_id", sellerid);

                        cartRef.push().setValue(cartData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ProductDetailsActivity.this, "Product added to Cart", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ProductDetailsActivity.this, "Failed to add to Cart", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProductDetailsActivity.this, "Error checking cart", Toast.LENGTH_SHORT).show();
                    }
                });
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
