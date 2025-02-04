package com.example.oneshop.Seller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.oneshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertProductActivity extends AppCompatActivity {

    private EditText etProductName, etProductDescription, etProductPrice, etProductStock;
    private Spinner categorySpinner;
    private ImageView ivProductImage;
    private Button btnInsertProduct;

    private Uri imageUri;
    private FirebaseAuth auth;
    private DatabaseReference categoryRef, productRef;
    private static final String TAG = "InsertProductActivity";
    private static final String CLOUD_NAME = "ddfkdln9b";
    private static final String API_KEY = "975694452154685";
    private static final String API_SECRET = "eDBFh-qA3BJJRtd87qmY3Yzeq3o";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    ivProductImage.setImageURI(imageUri); // Show selected image
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);

        // Initialize views
        etProductName = findViewById(R.id.et_product_name);
        etProductDescription = findViewById(R.id.et_product_description);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductStock = findViewById(R.id.et_product_stock);
        categorySpinner = findViewById(R.id.spinner_category);
        ivProductImage = findViewById(R.id.iv_selected_image);
        Button btnSelectImage = findViewById(R.id.btn_select_image);
        btnInsertProduct = findViewById(R.id.btn_insert_product);

        categoryRef = FirebaseDatabase.getInstance().getReference("categories");
        productRef = FirebaseDatabase.getInstance().getReference("products");

        // Load categories
        loadCategories();

        btnSelectImage.setOnClickListener(v -> pickImageFromGallery());
        btnInsertProduct.setOnClickListener(v -> insertProduct());

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Check if the user is logged in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to add products", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        statusbar();
    }

    private void loadCategories() {
        categoryRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> categoryNames = new java.util.ArrayList<>();
                for (var categorySnapshot : task.getResult().getChildren()) {
                    String categoryName = categorySnapshot.child("category_name").getValue(String.class);
                    categoryNames.add(categoryName);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void insertProduct() {
        String productName = etProductName.getText().toString().trim();
        String productDescription = etProductDescription.getText().toString().trim();
        String productPrice = etProductPrice.getText().toString().trim();
        String productStock = etProductStock.getText().toString().trim();
        String selectedCategory = categorySpinner.getSelectedItem() != null
                ? categorySpinner.getSelectedItem().toString() : null;
        FirebaseUser currentUser = auth.getCurrentUser(); // Get the logged-in user
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productName.isEmpty() || productDescription.isEmpty() || productPrice.isEmpty() ||
                productStock.isEmpty() || selectedCategory == null || imageUri == null) {
            Toast.makeText(this, "All fields and an image are required", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                        "cloud_name", CLOUD_NAME,
                        "api_key", API_KEY,
                        "api_secret", API_SECRET
                ));

                // Upload image using InputStream instead of file path
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Map uploadResult = cloudinary.uploader().upload(imageStream, ObjectUtils.emptyMap());
                String imageUrl = uploadResult.get("secure_url").toString();
                String imagePublicId = uploadResult.get("public_id").toString();

                // Save product details to Firebase
                String productId = productRef.push().getKey();
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("product_id", productId);
                productMap.put("product_name", productName);
                productMap.put("description", productDescription);
                productMap.put("price", Double.parseDouble(productPrice));
                productMap.put("stock_quantity", Integer.parseInt(productStock));
                productMap.put("category_name", selectedCategory);
                productMap.put("image_url", imageUrl);
                productMap.put("image_public_id", imagePublicId);
                productMap.put("seller_user_id", currentUser.getUid());

                productRef.child(productId).setValue(productMap)
                        .addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(this, "ProductS added successfully", Toast.LENGTH_SHORT).show();
                        }))
                        .addOnFailureListener(e -> runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Log.e(TAG, "Database Error: " + e.getMessage());
                            Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
                        }));

            } catch (Exception e) {
                progressDialog.dismiss();
                Log.e(TAG, "Error: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
    private void statusbar() {
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
