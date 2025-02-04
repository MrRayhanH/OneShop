package com.example.oneshop.Seller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdateProductActivity extends AppCompatActivity {

    private EditText etProductName, etProductPrice, etProductQuantity;
    private Spinner spinnerCategory;
    private ImageView ivProductImage;
    private Button btnUpdateProduct, btnSelectImage;

    private Uri imageUri;
    private String productId, oldImageUrl, oldImagePublicId;
    private DatabaseReference productRef, categoryRef;
    private FirebaseUser currentUser;

    // Cloudinary Credentials
    private static final String CLOUD_NAME = "ddfkdln9b";
    private static final String API_KEY = "975694452154685";
    private static final String API_SECRET = "eDBFh-qA3BJJRtd87qmY3Yzeq3o";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        ivProductImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        // Initialize views
        etProductName = findViewById(R.id.edit_text_product_name);
        etProductPrice = findViewById(R.id.edit_text_product_price);
        etProductQuantity = findViewById(R.id.edit_text_product_quantity);
        spinnerCategory = findViewById(R.id.spinner_category);
        ivProductImage = findViewById(R.id.image_view_product);
        btnUpdateProduct = findViewById(R.id.button_update);
        btnSelectImage = findViewById(R.id.button_select_image);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        productRef = FirebaseDatabase.getInstance().getReference("products");
        categoryRef = FirebaseDatabase.getInstance().getReference("categories");

        // Load categories into spinner
        loadCategories();

        // Get product ID from intent
        productId = getIntent().getStringExtra("product_id");
        if (productId != null) {
            loadProductDetails(productId);
        }

        btnSelectImage.setOnClickListener(v -> pickImageFromGallery());
        btnUpdateProduct.setOnClickListener(v -> updateProduct());
        statusbar();
    }

    private void loadCategories() {
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateProductActivity.this,
                        android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryName = categorySnapshot.child("category_name").getValue(String.class);
                    adapter.add(categoryName);
                }
                spinnerCategory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(UpdateProductActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void loadProductDetails(String productId) {
        productRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                etProductName.setText(snapshot.child("product_name").getValue(String.class));
                etProductPrice.setText(String.valueOf(snapshot.child("price").getValue(Double.class)));
                etProductQuantity.setText(String.valueOf(snapshot.child("stock_quantity").getValue(Integer.class)));

                oldImageUrl = snapshot.child("image_url").getValue(String.class);
                oldImagePublicId = snapshot.child("image_public_id").getValue(String.class);

                // Load Image
                Picasso.get().load(oldImageUrl).into(ivProductImage);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(UpdateProductActivity.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProduct() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Product...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                        "cloud_name", CLOUD_NAME,
                        "api_key", API_KEY,
                        "api_secret", API_SECRET
                ));

                String imageUrl = oldImageUrl;
                String imagePublicId = oldImagePublicId;

                // If new image selected, upload to Cloudinary
                if (imageUri != null) {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Map uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap());
                    imageUrl = uploadResult.get("secure_url").toString();
                    imagePublicId = uploadResult.get("public_id").toString();

                    // Delete old image if exists
                    if (oldImagePublicId != null) {
                        cloudinary.uploader().destroy(oldImagePublicId, ObjectUtils.emptyMap());
                    }
                }

                // Ensure category selection is valid
                String categoryName = spinnerCategory.getSelectedItem() != null
                        ? spinnerCategory.getSelectedItem().toString()
                        : "Unknown Category";

                // Update Product Data
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("product_name", etProductName.getText().toString().trim());
                productMap.put("price", Double.parseDouble(etProductPrice.getText().toString().trim()));
                productMap.put("stock_quantity", Integer.parseInt(etProductQuantity.getText().toString().trim()));
                productMap.put("category_name", categoryName);
                productMap.put("image_url", imageUrl);
                productMap.put("image_public_id", imagePublicId);
                // Update Firebase Realtime Database
                productRef.child(productId).updateChildren(productMap)
                        .addOnSuccessListener(aVoid -> {
                            progressDialog.dismiss();
                            runOnUiThread(() -> Toast.makeText(UpdateProductActivity.this, "Product Updated Successfully", Toast.LENGTH_SHORT).show());
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            runOnUiThread(() -> Toast.makeText(UpdateProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show());
                        });

            } catch (Exception e) {
                progressDialog.dismiss();
                runOnUiThread(() -> Toast.makeText(UpdateProductActivity.this, "Update Failed", Toast.LENGTH_SHORT).show());
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
