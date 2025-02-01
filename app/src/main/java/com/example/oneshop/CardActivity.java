package com.example.oneshop;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oneshop.ProductDisplay.ProductsDisplay;

public class CardActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView listViewCart;
    private TextView priceTextView, totalProductTextView;
    private double totalPrice=0 ;
    private int totalProducts=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        // Initialize views
        databaseHelper = new DatabaseHelper(this);
        listViewCart = findViewById(R.id.list_view_cart);
        priceTextView = findViewById(R.id.tv_total_price);
        totalProductTextView = findViewById(R.id.tv_total_product);
        Button paymentButton = findViewById(R.id.btn_payment);

        ImageView home, favourite, setting;

        home = findViewById(R.id.iv_home);
        favourite = findViewById(R.id.iv_favourite);
        setting = findViewById(R.id.iv_setting);

        home.setOnClickListener(v -> {startActivity(new Intent(CardActivity.this, ProductsDisplay.class));});
        favourite.setOnClickListener(v -> {startActivity(new Intent(CardActivity.this, FavouriteActivity.class));});
        setting.setOnClickListener(v -> {startActivity(new Intent(CardActivity.this, SettingActivity.class));});



        Cursor cursor = databaseHelper.getAllCartItems();
        updateTotals(cursor);
        // Handle payment button click
        paymentButton.setOnClickListener(v -> {
         getTotalPrice();
         getTotalProducts();
         Toast.makeText(CardActivity.this, "Payment Done\nTotal Price: $" + totalPrice + "\nTotal Products: " + totalProducts, Toast.LENGTH_SHORT).show();

        });

        // Display cart items and set up the payment button
        displayCartItems();

        // status bar transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));

        // Make the status bar icons light (for dark background)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayCartItems();

    }
    private void displayCartItems() {
        Cursor cursor = databaseHelper.getAllCartItems();
        //CartAdapter cartAdapter = new CartAdapter(this, cursor, 0);
       // listViewCart.setAdapter(cartAdapter);
    }

    public void refreshTotals() {
        Cursor cursor = databaseHelper.getAllCartItems();
        updateTotals(cursor);
        cursor.close(); // Always close the cursor when done
    }

    private void updateTotals(Cursor cursor) {
        getTotalPrice();
        getTotalProducts();
        // Iterate over the cursor to calculate totals
        priceTextView.setText(String.format("Total BDT : %.2f", totalPrice));
        totalProductTextView.setText("Total Products : " + totalProducts);
    }

    // Optional method to retrieve total price directly
    private void getTotalPrice() {
        Cursor cursor = databaseHelper.getAllCartItems();
        totalPrice = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                double productPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE_CARD));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_QUANTITY_CARD));
                totalPrice += productPrice * quantity;
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    // Optional method to retrieve total product count directly
    private void getTotalProducts() {
        totalProducts = 0;
        Cursor cursor = databaseHelper.getAllCartItems();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_QUANTITY_CARD));
                totalProducts += quantity;
            } while (cursor.moveToNext());
            cursor.close();
        }

    }
}
