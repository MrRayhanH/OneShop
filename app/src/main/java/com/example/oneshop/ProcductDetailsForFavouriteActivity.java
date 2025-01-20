package com.example.oneshop;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.oneshop.FavouriteActivity;

import androidx.appcompat.app.AppCompatActivity;

public class ProcductDetailsForFavouriteActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView tv_ProductNameFav, tv_ProductPriceFav, tv_productQuantityfav;
    private ImageView iv_product_image, iv_delete_details;
    private double productPrice;
    private String productName;
    private byte[] productImageByteArray;
    private int productId, productQuantity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procduct_details_for_favourite);

        // Initialize views
        iv_delete_details = findViewById(R.id.iv_delete_details);
        iv_product_image = findViewById(R.id.iv_product_image);
        tv_ProductNameFav = findViewById(R.id.tv_ProductNameFav);
        tv_ProductPriceFav = findViewById(R.id.tv_ProductPriceFav);
        tv_productQuantityfav = findViewById(R.id.tv_productQuantityfav);
        Button btnAddToCart = findViewById(R.id.btn_addToCardFav);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Get product ID from Intent
        Intent intent = getIntent();
        productId = intent.getIntExtra("PRODUCT_ID", -1);

        if (productId != -1) {
            displayProductDetails(productId);
        } else {
            Toast.makeText(this, "Product ID is missing", Toast.LENGTH_SHORT).show();
        }
        iv_delete_details.setOnClickListener(v -> {deleteProduct();});
        btnAddToCart.setOnClickListener(v -> {addToCard();});
    }

    private void displayProductDetails(int productId) {
        // Query the database for the product
        Cursor cursor = databaseHelper.getProductById(productId);

        if (cursor != null && cursor.moveToFirst()) {
            // Retrieve product details from the cursor
            productPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE));
            productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_QUANTITY));
            productImageByteArray = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_IMAGE_URI));
            productName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_NAME));

            // Display the product details in the views
            tv_ProductNameFav.setText(productName);
            tv_ProductPriceFav.setText("Total BDT: " + productPrice);
            tv_productQuantityfav.setText("Total Quantity: " + productQuantity);

            // Display product image if available
            if (productImageByteArray != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(productImageByteArray, 0, productImageByteArray.length);
                iv_product_image.setImageBitmap(bitmap);
            }

            cursor.close(); // Close the cursor to avoid memory leaks
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteProduct() {
        boolean isProductDeleted = databaseHelper.deleteProductFromFavourite(productId);
        if(isProductDeleted){
            Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProcductDetailsForFavouriteActivity.this, FavouriteActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this, "Failed to delete product", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToCard() {
        try {
            // Check if the product is already in the cart
            boolean isProductInCart = databaseHelper.isProductInCart(productId);

            // Validate fields
            if (productName.isEmpty() || productImageByteArray == null || productPrice <= 0) {
                Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isProductInCart) {
                // Add product to the cart
                databaseHelper.addProductToCart(productName, productPrice, 1, productImageByteArray, productId);
                Toast.makeText(this, "Product inserted successfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Product already in cart", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "An error occurred while adding the product to the cart", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}
