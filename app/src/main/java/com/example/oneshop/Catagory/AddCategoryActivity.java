package com.example.oneshop.Catagory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.oneshop.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddCategoryActivity extends AppCompatActivity {

    private ImageView ivCategoryImage;
    private EditText etCategoryName;
    private Uri imageUri;

    private DatabaseReference databaseReference;


    private static final String CLOUD_NAME = "ddfkdln9b";
    private static final String API_KEY = "975694452154685";
    private static final String API_SECRET = "eDBFh-qA3BJJRtd87qmY3Yzeq3o";

    // Image picker launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    if (imageUri != null) {
                        ivCategoryImage.setImageURI(imageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("categories");

        // Initialize views
        ivCategoryImage = findViewById(R.id.iv_category_image);
        etCategoryName = findViewById(R.id.et_category_name);
        Button btnSelectImage = findViewById(R.id.btn_select_image);
        Button btnInsertCategory = findViewById(R.id.btn_insert_category);
        ImageView backArrow = findViewById(R.id.addCategoryBackArrow);

        // Set click listeners
        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnInsertCategory.setOnClickListener(v -> saveCategory());
        backArrow.setOnClickListener(v -> finish());

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

    private void openImagePicker() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        imagePickerLauncher.launch(pickIntent);
    }

    private void saveCategory() {
        String categoryName = etCategoryName.getText().toString().trim();

        if (categoryName.isEmpty()) {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please select a category image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Cloudinary
        uploadImageToCloudinary(categoryName);
    }

    private void uploadImageToCloudinary(String categoryName) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                // Convert the image to a byte array
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                byte[] imageData = getBytes(inputStream);

                Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                        "cloud_name", CLOUD_NAME,
                        "api_key", API_KEY,
                        "api_secret", API_SECRET
                ));

                // Upload image and retrieve response
                Map uploadResult = cloudinary.uploader().upload(imageData, ObjectUtils.emptyMap());
                String imageUrl = uploadResult.get("secure_url").toString();
                String publicId = uploadResult.get("public_id").toString(); // Get public_id

                progressDialog.dismiss();
                saveCategoryToDatabase(categoryName, imageUrl, publicId); // Save public_id to Firebase
            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AddCategoryActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


//    private void uploadImageToCloudinary(String categoryName) {
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Uploading image...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        new Thread(() -> {
//            String imageUrl = uploadImage(imageUri);
//            progressDialog.dismiss();
//            if (imageUrl != null) {
//                saveCategoryToDatabase(categoryName, imageUrl);
//            } else {
//                runOnUiThread(() -> Toast.makeText(AddCategoryActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
//            }
//        }).start();
//    }

    private String uploadImage(Uri imageUri) {
        try {
            // Convert the image to a byte array
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageData = getBytes(inputStream);

            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", CLOUD_NAME,
                    "api_key", API_KEY,
                    "api_secret", API_SECRET
            ));

            // Upload as a byte array stream
            Map uploadResult = cloudinary.uploader().upload(imageData, ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to convert InputStream to byte array
    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void saveCategoryToDatabase(String categoryName, String imageUrl, String publicId) {
        String categoryId = databaseReference.push().getKey();

        HashMap<String, Object> categoryMap = new HashMap<>();
        categoryMap.put("category_id", categoryId);
        categoryMap.put("category_name", categoryName);
        categoryMap.put("image_url", imageUrl);
        categoryMap.put("image_public_id", publicId); // Save public_id to Firebase

        databaseReference.child(categoryId).setValue(categoryMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddCategoryActivity.this, "Category added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddCategoryActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


//    private void saveCategoryToDatabase(String categoryName, String imageUrl) {
//        String categoryId = databaseReference.push().getKey();
//
//        HashMap<String, Object> categoryMap = new HashMap<>();
//        categoryMap.put("category_id", categoryId);
//        categoryMap.put("category_name", categoryName);
//        categoryMap.put("image_url", imageUrl);
//
//        databaseReference.child(categoryId).setValue(categoryMap)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(AddCategoryActivity.this, "Category added successfully!", Toast.LENGTH_SHORT).show();
//                        finish();
//                    } else {
//                        Toast.makeText(AddCategoryActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//}



//package com.example.oneshop.Catagory;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.oneshop.DatabaseHelper;
//import com.example.oneshop.R;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//
//public class AddCategoryActivity extends AppCompatActivity {
//
//    private ImageView ivCategoryImage;
//    private EditText etCategoryName;
//    private DatabaseHelper databaseHelper;
//    private byte[] imageByteArray;
//
//    // Image picker launcher
//    private final ActivityResultLauncher<Intent> imagePickerLauncher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                    Uri imageUri = result.getData().getData();
//                    if (imageUri != null) {
//                        try {
//                            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                            ivCategoryImage.setImageBitmap(imageBitmap);
//                            imageByteArray = bitmapToByteArray(imageBitmap);
//                        } catch (IOException e) {
//                            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            });
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_category);
//
//        // Initialize views
//        ivCategoryImage = findViewById(R.id.iv_category_image);
//        etCategoryName = findViewById(R.id.et_catagory_name);
//        Button btnSelectImage = findViewById(R.id.btn_select_image);
//        Button btnInsertCategory = findViewById(R.id.btn_insert_catagory);
//        ImageView backArrow = findViewById(R.id.addCatagoryBackArrow);
//        // Initialize database helper
//        databaseHelper = new DatabaseHelper(this);
//
//        // Set click listeners
//        btnSelectImage.setOnClickListener(v -> openImagePicker());
//        btnInsertCategory.setOnClickListener(v -> saveCategory());
//        backArrow.setOnClickListener(v -> finish());
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));
//        }
//
//        // Make the status bar icons light (for dark background)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }
//    }
//
//    private void openImagePicker() {
//        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        pickIntent.setType("image/*");
//        imagePickerLauncher.launch(pickIntent);
//    }
//
//    private void saveCategory() {
//        String categoryName = etCategoryName.getText().toString().trim();
//
//        if (categoryName.isEmpty()) {
//            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (imageByteArray == null) {
//            Toast.makeText(this, "Please select a category image", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        boolean isInserted = databaseHelper.insertCategory(categoryName, imageByteArray);
//
//        if (isInserted) {
//            Toast.makeText(this, "Category added successfully!", Toast.LENGTH_SHORT).show();
//            finish();
//        } else {
//            Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private byte[] bitmapToByteArray(Bitmap bitmap) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
//        return outputStream.toByteArray();
//    }
//}
