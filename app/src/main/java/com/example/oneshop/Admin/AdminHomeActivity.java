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

import com.example.oneshop.Admin.Catagory.AddCategoryActivity;
import com.example.oneshop.Admin.Catagory.CategoryListActivity;
import com.example.oneshop.Admin.Order.Deleverd.OrderdDeleverdActivity;
import com.example.oneshop.Admin.Order.OutOfDelivery.OutofDeliveryActivity;
import com.example.oneshop.Admin.Order.Warehouse.WarhouseActivity;
import com.example.oneshop.LoginSingup.LoginActivity;
import com.example.oneshop.Seller.Delete.DeleteProductActivity;
import com.example.oneshop.Seller.Insert.InsertProductActivity;
import com.example.oneshop.Seller.SellerHomeActivity;
import com.example.oneshop.Seller.ViewProduct.SellerProductView;
import com.example.oneshop.Seller.Update.UpdateProductListActivity;
import com.example.oneshop.R;
import com.example.oneshop.Admin.Order.Accept.OrderActivity;
import com.example.oneshop.User.Setting.SettingActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        CardView cv_viewOder, cv_Deleberd, CV_DeleteProduct, cv_warehouse, CV_InsertCategory, CV_DeleteCategory, CV_outOfDelevery;

        cv_warehouse =findViewById(R.id.cv_warehouse);
        cv_Deleberd =findViewById(R.id.cv_Deleberd);
        CV_outOfDelevery =findViewById(R.id.cv_outOfDelevery);
        cv_viewOder =findViewById(R.id.cv_viewOder);
        CV_InsertCategory =findViewById(R.id.cv_insert_Catagory);
        CV_DeleteCategory =findViewById(R.id.cv_delete_category);
        Button btn_logout = findViewById(R.id.btn_logout);
        ImageView backArrow = findViewById(R.id.adminBackArrow);

        cv_warehouse.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, WarhouseActivity.class);startActivity(intent);});
        cv_viewOder.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, OrderActivity.class);startActivity(intent);});
        cv_Deleberd.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, OrderdDeleverdActivity.class);startActivity(intent);});
        CV_outOfDelevery.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, OutofDeliveryActivity.class);startActivity(intent);});
        CV_InsertCategory.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, AddCategoryActivity.class);startActivity(intent);});
        CV_DeleteCategory.setOnClickListener(v -> {Intent intent = new Intent(AdminHomeActivity.this, CategoryListActivity.class);startActivity(intent);});
        backArrow.setOnClickListener(v -> finish());

        btn_logout.setOnClickListener(v -> {
            logout();
//            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//            firebaseAuth.signOut();
//            Toast.makeText(AdminHomeActivity.this, "Logout", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish(); // Close the current activity
            //If the target activity is already running in the current task, all activities on top of it are cleared (removed from the stack).
            // The target activity is brought to the front instead of creating a new instance.

        });

        statusBar();
    }
    private void logout() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Toast.makeText(AdminHomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void statusBar(){
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