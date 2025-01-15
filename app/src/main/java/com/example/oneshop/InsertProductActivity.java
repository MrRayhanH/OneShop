package com.example.oneshop;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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

import com.example.oneshop.DatabaseHelper;
import com.example.oneshop.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InsertProductActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;


    private ImageView iv_select;
    private DatabaseHelper databaseHelper;
    private byte[] imageByteArray;
    EditText et_productName, et_productPrice, et_productQuantity, et_productCatagory;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);


        et_productName = findViewById(R.id.et_product_name);
        et_productPrice = findViewById(R.id.et_product_price);
        et_productQuantity = findViewById(R.id.et_product_quantity);
        iv_select = findViewById(R.id.iv_selected_image);
        Button selectImageButton = findViewById(R.id.btn_select_image);
        Button insertProductButton = findViewById(R.id.btn_insert_product);
        Spinner spinner = findViewById(R.id.spinner_category);
        ImageView backArrow = findViewById(R.id.insertProductBackArrow);


        databaseHelper = new DatabaseHelper(this);

        backArrow.setOnClickListener(view -> finish());

        // Populate Spinner from the database
        populateCategorySpinner(spinner);

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    iv_select.setImageBitmap(imageBitmap);
                    imageByteArray = bitmapToByteArray(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        selectImageButton.setOnClickListener(view -> showImageSelectionDialog());

        insertProductButton.setOnClickListener(view -> insertProduct(spinner));

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

    private void showImageSelectionDialog() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        imagePickerLauncher.launch(pickIntent);
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void insertProduct(Spinner spinner) {
        String name = et_productName.getText().toString();
        double price = Double.parseDouble(et_productPrice.getText().toString());
        int quantity = Integer.parseInt(et_productQuantity.getText().toString());
        String catagory = spinner.getSelectedItem() != null ? spinner.getSelectedItem().toString() : null; // Get selected category

        if (name.isEmpty() || imageByteArray == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            Toast.makeText(this, "Product inserted successfully", Toast.LENGTH_SHORT).show();
        }
        databaseHelper.insertProduct(name, price, quantity, catagory, imageByteArray);
        Toast.makeText(this, "Product inserted successfully", Toast.LENGTH_SHORT).show();
    }

    private void populateCategorySpinner(Spinner spinner) {
        // Fetch categories from the database
        Cursor cursor = databaseHelper.getAllCategoriesName(); // Assume getAllCategories() returns a Cursor with category data
        List<String> categories = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("categoryName"));
                categories.add(categoryName);
            } while (cursor.moveToNext());
            cursor.close(); // Always close the cursor
        }

        // Set up ArrayAdapter for Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Handle the case where no categories exist
        if (categories.isEmpty()) {
            Toast.makeText(this, "No categories found. Please add categories first.", Toast.LENGTH_SHORT).show();
        }
    }

}