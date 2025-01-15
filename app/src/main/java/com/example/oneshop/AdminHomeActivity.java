package com.example.oneshop;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.oneshop.R;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        CardView CV_ViewProduct, CV_UpdateProduct, CV_DeleteProduct, CV_InsertProduct, CV_InsertCategory, CV_DeleteCategory;

        CV_InsertProduct =findViewById(R.id.cv_insert_product);
        CV_UpdateProduct =findViewById(R.id.cv_update);
        CV_DeleteProduct =findViewById(R.id.cv_delete);
        CV_ViewProduct =findViewById(R.id.cv_viewProduct);
        CV_InsertCategory =findViewById(R.id.cv_insert_Catagory);
        CV_DeleteCategory =findViewById(R.id.cv_delete_category);

        ImageView backArrow = findViewById(R.id.adminBackArrow);


        CV_InsertProduct.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, InsertProductActivity.class);startActivity(intent);});
        CV_ViewProduct.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, ViewProductActivity.class);startActivity(intent);});
        CV_UpdateProduct.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, UpdateProductActivity.class);startActivity(intent);});
        CV_DeleteProduct.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, DeleteProductActivity.class);startActivity(intent);});
        CV_InsertCategory.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, AddCategoryActivity.class);startActivity(intent);});
        CV_DeleteCategory.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, DeleteCategoryActivity.class);startActivity(intent);});
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


}