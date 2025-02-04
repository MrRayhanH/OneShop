package com.example.oneshop.Favourite;

import android.content.Intent;
import android.os.Bundle;
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

public class FavouriteProductDetails extends AppCompatActivity {

    private DatabaseReference favRef;
    private DatabaseReference cartRef;
    private ImageView ivProductImage, ivDelete;
    private TextView tvProductName, tvProductPrice, tvProductDescription, tvProductQuantity;
    private Button btnAddToCart;
    private String productId, productName, productDescription, imageUrl, userId;
    private int productQuantity;
    private double productPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_product_details1);

        // Initialize views
        ivProductImage = findViewById(R.id.iv_product_image);
        tvProductName = findViewById(R.id.tv_ProductNameFav);
        tvProductPrice = findViewById(R.id.tv_ProductPriceFav);
        tvProductQuantity = findViewById(R.id.tv_productQuantityfav);
        tvProductDescription = findViewById(R.id.tv_ProductDescription);
        ivDelete = findViewById(R.id.iv_delete);
        btnAddToCart = findViewById(R.id.btn_addToCardFav);

        // Initialize Firebase
        favRef = FirebaseDatabase.getInstance().getReference("Favorites");
        cartRef = FirebaseDatabase.getInstance().getReference("Cart");

        // Get product details passed from the previous activity
        Intent intent = getIntent();
        productId = intent.getStringExtra("PRODUCT_ID");  // Product ID passed as String
        productName = intent.getStringExtra("PRODUCT_NAME");
        productPrice = intent.getDoubleExtra("PRODUCT_PRICE", 0.0);
        productQuantity = intent.getIntExtra("PRODUCT_QUANTITY", 0); // Quantity passed
        productDescription = intent.getStringExtra("PRODUCT_DESCRIPTION");
        imageUrl = intent.getStringExtra("PRODUCT_IMAGE_URL");

        // Set product details in the UI
        setDetails();

        // Set onClick listener for Add to Cart button
        btnAddToCart.setOnClickListener(v -> addToCart());

        // Set onClick listener for delete button
        ivDelete.setOnClickListener(v -> deleteProduct());
    }

    private void setDetails() {
        tvProductName.setText(productName);
        tvProductPrice.setText("Total BDT: " + productPrice);
        tvProductQuantity.setText("Total Stock: " + productQuantity);  // Display the quantity
        tvProductDescription.setText("Product Details: " + productDescription);
        Picasso.get().load(imageUrl).into(ivProductImage);
    }

    private void addToCart() {
        // Get the current user ID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reference to the Cart in Firebase Realtime Database
        cartRef = FirebaseDatabase.getInstance().getReference("Cart");

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
                                    Toast.makeText(FavouriteProductDetails.this, "Product is already in the cart", Toast.LENGTH_SHORT).show();
                                    return; // Exit the method if product is already in the cart
                                }
                            }
                        }

                        // If the product is not in the cart, add it
                        Map<String, Object> cartData = new HashMap<>();
                        cartData.put("user_id", userId);
                        cartData.put("product_id", productId);
                        cartData.put("quantity", 1); // Default quantity

                        cartRef.push().setValue(cartData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(FavouriteProductDetails.this, "Product added to Cart", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(FavouriteProductDetails.this, "Failed to add to Cart", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(FavouriteProductDetails.this, "Error checking cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void deleteProduct() {
        // Get the current user ID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query to find the specific record with product_id and user_id in the Favorites node
        favRef.orderByChild("product_id").equalTo(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot favSnapshot : snapshot.getChildren()) {
                            String uid = favSnapshot.child("user_id").getValue(String.class);
                            // Check if the user_id matches the current user's id
                            if (uid != null && uid.equals(userId)) {
                                // Delete this favorite
                                favSnapshot.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(FavouriteProductDetails.this, "Product deleted from favorites.", Toast.LENGTH_SHORT).show();
                                            finish(); // Close the activity
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(FavouriteProductDetails.this, "Failed to delete product.", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error here
                        Toast.makeText(FavouriteProductDetails.this, "Error deleting product", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
