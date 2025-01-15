package com.example.myapplication.activitys;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.CategoryAdapter;
import com.example.myapplication.Adapter.ProductAdapter1;
import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;

public class ProductsDisplay extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Cursor cursor, cursorCatagory, cursorCataryView;

    private static final String TAG = "ProductsDisplay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_display);

        databaseHelper = new DatabaseHelper(this);
        ImageView favourite, card, setting, home;
        favourite = findViewById(R.id.iv_favourite);
        card = findViewById(R.id.iv_card);
        setting = findViewById(R.id.iv_setting);
        home = findViewById(R.id.iv_home);
        home.setEnabled(false);

        home.setOnClickListener(v -> {home.setEnabled(false);Intent intent = new Intent(ProductsDisplay.this, ProductsDisplay.class);startActivity(intent);});
        card.setOnClickListener(v -> {Intent intent = new Intent(ProductsDisplay.this, CardActivity.class);startActivity(intent);});
        favourite.setOnClickListener(v -> {Intent intent = new Intent(ProductsDisplay.this, FavouriteActivity.class);startActivity(intent);});
        setting.setOnClickListener(v -> {Intent intent = new Intent(ProductsDisplay.this, SettingActivity.class);startActivity(intent);});

        try {
            databaseHelper = new DatabaseHelper(this);
            cursor = databaseHelper.getAllProducts();
            cursorCatagory = databaseHelper.getAllCategories();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database or fetching products", e);
        }


        try {
            
            // Catagory View and display
            
            RecyclerView recyclerView_catagory = findViewById(R.id.recycler_view_catagory);
            recyclerView_catagory.setLayoutManager(new GridLayoutManager(this, 4));
            CategoryAdapter categoryAdapter = new CategoryAdapter(this, cursorCatagory);
            recyclerView_catagory.setAdapter(categoryAdapter);

            categoryAdapter.setOnItemClickListener(position -> {
                if (cursorCatagory.moveToPosition(position)) {
                   home.setEnabled(true);
                   // Toast.makeText(this, "Clicked: " + position, Toast.LENGTH_SHORT).show();

                    String categoryName = cursorCatagory.getString(cursorCatagory.getColumnIndexOrThrow("categoryName"));
                    cursorCataryView = databaseHelper.getProductsByCategory(categoryName);

                    RecyclerView recyclerView = findViewById(R.id.recycler_view1);
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns in grid
                    ProductAdapter1 adapter = new ProductAdapter1(this, cursorCataryView);
                    recyclerView.setAdapter(adapter);

                    // for product click
                    adapter.setOnItemClickListener(position1 -> {
                        if (cursorCataryView.moveToPosition(position1)) {
                            String productName = cursorCataryView.getString(cursorCataryView.getColumnIndexOrThrow("productName"));

                            // Start product detail activity
                            Intent intent = new Intent(ProductsDisplay.this, ProductDetailsActivity.class);
                            intent.putExtra("PRODUCT_NAME", productName);
                            startActivity(intent);
                            
                        }
                    });
                }
            });


            //Products display
            RecyclerView recyclerView = findViewById(R.id.recycler_view1);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns in grid
            ProductAdapter1 adapter = new ProductAdapter1(this, cursor);
            adapter.setOnItemClickListener(position -> {
                if (cursor.moveToPosition(position)) {
                    String productName = cursor.getString(cursor.getColumnIndexOrThrow("productName"));

                    // Start product detail activity
                    Intent intent = new Intent(ProductsDisplay.this, ProductDetailsActivity.class);
                    intent.putExtra("PRODUCT_NAME", productName);
                    startActivity(intent);

                    //Toast.makeText(this, "Clicked: " + productName, Toast.LENGTH_SHORT).show();
                }
            });


            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            Log.e(TAG, "Error setting up ProductDisplay", e);
        }




        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));
        // Make the status bar icons light (for dark background)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Close the cursor and database with error handling
        try {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (databaseHelper != null) {
                databaseHelper.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing database or cursor", e);
        }
    }



}