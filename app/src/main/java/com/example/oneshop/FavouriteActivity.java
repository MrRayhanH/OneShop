package com.example.oneshop;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oneshop.DatabaseHelper;
import com.example.oneshop.ProductAdapter1;
import com.example.oneshop.R;

public class FavouriteActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Cursor cursor;

    private static final String TAG = "FavouriteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        // Bottom navigation buttons
        ImageView home = findViewById(R.id.iv_home);
        ImageView card = findViewById(R.id.iv_card);
        ImageView setting = findViewById(R.id.iv_setting);

        home.setOnClickListener(v -> startActivity(new Intent(FavouriteActivity.this, ProductsDisplay.class)));
        card.setOnClickListener(v -> startActivity(new Intent(FavouriteActivity.this, CardActivity.class)));
        setting.setOnClickListener(v -> startActivity(new Intent(FavouriteActivity.this, SettingActivity.class)));


        try {
            databaseHelper = new DatabaseHelper(this);
            cursor = databaseHelper.getAllProducts();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database or fetching products", e);
        }


        try {

            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns in grid
            ProductAdapter1 adapter = new ProductAdapter1(this, cursor);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView", e);
        }


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
