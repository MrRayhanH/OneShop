package com.example.oneshop.Admin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.oneshop.Catagory.AddCategoryActivity;
import com.example.oneshop.Catagory.CategoryListActivity;
import com.example.oneshop.LoginSingup.LoginActivity;
import com.example.oneshop.Products.DeleteProductActivity;
import com.example.oneshop.Products.InsertProductActivity;
import com.example.oneshop.Products.SellerProductView;
import com.example.oneshop.Products.UpdateProductActivity;
import com.example.oneshop.Products.UpdateProductListActivity;
import com.example.oneshop.R;
import com.example.oneshop.SettingActivity;
import com.google.firebase.auth.FirebaseAuth;

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
        Button btn_logout = findViewById(R.id.btn_logout);
        ImageView backArrow = findViewById(R.id.adminBackArrow);


        CV_InsertProduct.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, InsertProductActivity.class);startActivity(intent);});
        CV_ViewProduct.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, SellerProductView.class);startActivity(intent);});
        CV_UpdateProduct.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, UpdateProductListActivity.class);startActivity(intent);});
        CV_DeleteProduct.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, DeleteProductActivity.class);startActivity(intent);});
        CV_InsertCategory.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, AddCategoryActivity.class);startActivity(intent);});
        CV_DeleteCategory.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, CategoryListActivity.class);startActivity(intent);});
        //CV_DeleteCategory.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, DeleteCategoryActivity.class);startActivity(intent);});
        backArrow.setOnClickListener(v -> finish());

        btn_logout.setOnClickListener(v -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Toast.makeText(AdminHomeActivity.this, "Logout", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
        });
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