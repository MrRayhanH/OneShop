package com.example.oneshop;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oneshop.DatabaseHelper;
import com.example.oneshop.R;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView imageView, iv_favourite;
    private TextView productNameTextView, productPriceTextView, productQuantityTextView;
    private DatabaseHelper databaseHelper;
    private String productName;
    private double productPrice;
    private byte[] productImageByteArray;
    private int productId, productId_favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize views
        imageView = findViewById(R.id.iv_product_image);
        productNameTextView = findViewById(R.id.tv_ProductName);
        productPriceTextView = findViewById(R.id.tv_ProductPrice);
        productQuantityTextView = findViewById(R.id.tv_productQuantity);
        Button btnAddToCart = findViewById(R.id.btn_addToCard);
        iv_favourite = findViewById(R.id.iv_favourite_details);
        databaseHelper = new DatabaseHelper(this);

        // Get the product name passed from the previous activity
        Intent intent = getIntent();
        productName = intent.getStringExtra("PRODUCT_NAME");
        productId_favourite = intent.getIntExtra("PRODUCT_ID",-1);

        if (productId_favourite!=-1) {
            displayProductDetails();
        }
        else {
            Toast.makeText(this, "Product is missing", Toast.LENGTH_SHORT).show();
        }

        // Set up the button to add product to the cart
        btnAddToCart.setOnClickListener(v -> {addToCard();
        });
        iv_favourite.setOnClickListener(v ->{
            addToFavorite();
        });

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));

        // Make the status bar icons light (for dark background)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void displayProductDetails( ) {
        // Query the database for the product
        Cursor cursor = databaseHelper.getProductById(productId_favourite);

        if (cursor != null && cursor.moveToFirst()) {
            // Retrieve product details from the cursor
            productPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE));
            int productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_QUANTITY));
            productImageByteArray = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_IMAGE_URI));
            productId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));

            // Display the product details in the views
            productNameTextView.setText(productName);
            productPriceTextView.setText("Total BDT:" + productPrice);
            productQuantityTextView.setText("Total Quantity: " + productQuantity);

            // Display product image if available
            if (productImageByteArray != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(productImageByteArray, 0, productImageByteArray.length);
                imageView.setImageBitmap(bitmap);
            }
            cursor.close();  // Close the cursor to avoid memory leaks
        }
        else {Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();}
    }

    private void addToCard() {


        boolean isProductInCart = databaseHelper.isProductInCart(productId);

        if (productName.isEmpty() || productImageByteArray == null || productPrice<=0) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
        }

        else if(!isProductInCart) {
            databaseHelper.addProductToCart(productName, productPrice, 1, productImageByteArray, productId);
            Toast.makeText(this, "Product inserted successfully", Toast.LENGTH_SHORT).show();
        }

        else{
            Toast.makeText(this,"Product already in card" , Toast.LENGTH_SHORT).show();
        }
    }
    private void addToFavorite(){

        boolean isProductIn = databaseHelper.isProductInFavourite(productId);
        if (productName.isEmpty() || productImageByteArray == null || productPrice<=0) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
        }
        else if(!isProductIn){
            databaseHelper.addToFavourite(productId);
            Toast.makeText(this, "Added to Favourite", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Product already in favourite", Toast.LENGTH_SHORT).show();
        }
    }
}
