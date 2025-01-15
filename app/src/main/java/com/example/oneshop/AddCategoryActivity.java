package com.example.oneshop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.example.oneshop.DatabaseHelper;
import com.example.oneshop.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddCategoryActivity extends AppCompatActivity {

    private ImageView ivCategoryImage;
    private EditText etCategoryName;
    private DatabaseHelper databaseHelper;
    private byte[] imageByteArray;

    // Image picker launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            ivCategoryImage.setImageBitmap(imageBitmap);
                            imageByteArray = bitmapToByteArray(imageBitmap);
                        } catch (IOException e) {
                            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Initialize views
        ivCategoryImage = findViewById(R.id.iv_category_image);
        etCategoryName = findViewById(R.id.et_catagory_name);
        Button btnSelectImage = findViewById(R.id.btn_select_image);
        Button btnInsertCategory = findViewById(R.id.btn_insert_catagory);
        ImageView backArrow = findViewById(R.id.addCatagoryBackArrow);
        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

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

        // Make the status bar icons light (for dark background)
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
        if (imageByteArray == null) {
            Toast.makeText(this, "Please select a category image", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = databaseHelper.insertCategory(categoryName, imageByteArray);

        if (isInserted) {
            Toast.makeText(this, "Category added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        return outputStream.toByteArray();
    }
}
