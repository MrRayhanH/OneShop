package com.example.oneshop.Seller;
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

import com.example.oneshop.LoginSingup.LoginActivity;
import com.example.oneshop.R;
import com.example.oneshop.Seller.Delete.DeleteProductActivity;
import com.example.oneshop.Seller.Insert.InsertProductActivity;
import com.example.oneshop.Seller.Order.OrderActivitySeller;
import com.example.oneshop.Seller.Update.UpdateProductListActivity;
import com.example.oneshop.Seller.ViewProduct.SellerProductView;
import com.google.firebase.auth.FirebaseAuth;

public class SellerHomeActivity extends AppCompatActivity {
    CardView CV_ViewProduct, CV_UpdateProduct, CV_DeleteProduct, CV_InsertProduct, CV_ViewOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
        CV_InsertProduct = findViewById(R.id.cv_insertProduct);
        CV_UpdateProduct = findViewById(R.id.cv_updateProduct);
        CV_DeleteProduct = findViewById(R.id.cv_deleteProduct);
        CV_ViewProduct = findViewById(R.id.cv_viewProduct);
        CV_ViewOrder = findViewById(R.id.cv_viewOrders);
        Button btn_logout = findViewById(R.id.btn_logout);
        ImageView backArrow = findViewById(R.id.sellerBackArrow);
        // Setting onClick for cards
        CV_InsertProduct.setOnClickListener(v -> {Intent intent = new Intent(SellerHomeActivity.this, InsertProductActivity.class);startActivity(intent);});
        CV_ViewProduct.setOnClickListener(v -> {Intent intent = new Intent(SellerHomeActivity.this, SellerProductView.class);startActivity(intent);});
        CV_UpdateProduct.setOnClickListener(v -> {Intent intent = new Intent(SellerHomeActivity.this, UpdateProductListActivity.class);startActivity(intent);});
        CV_DeleteProduct.setOnClickListener(v -> {Intent intent = new Intent(SellerHomeActivity.this, DeleteProductActivity.class);startActivity(intent);});
        CV_ViewOrder.setOnClickListener(v -> {Intent intent = new Intent(SellerHomeActivity.this, OrderActivitySeller.class);startActivity(intent);});
        // Logout functionality
        btn_logout.setOnClickListener(v -> logout());
        // Back button functionality
        backArrow.setOnClickListener(v -> logout());
        statusBar();
    }
    private void logout() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Toast.makeText(SellerHomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void statusBar(){
        // Update the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));
        }

        // Set light status bar icons for dark backgrounds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
