package com.example.oneshop.User.Setting;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oneshop.Password.ResetPasswordActivity;
import com.example.oneshop.R;
import com.example.oneshop.User.AccuntDeleteActivity;
import com.example.oneshop.User.AddressActivity;
import com.example.oneshop.User.Card.CardActivity;
import com.example.oneshop.User.Favourite.FavouriteActivity;
import com.example.oneshop.LoginSingup.LoginActivity;
import com.example.oneshop.User.ProductDisplay.ProductsDisplay;
import com.example.oneshop.User.Order.OrderActivity;
import com.google.firebase.auth.FirebaseAuth;
public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button btn_logout = findViewById(R.id.btn_logout);
        Button btn_order = findViewById(R.id.btn_order);
        ImageView home, card, favourite;
        home = findViewById(R.id.iv_home);
        card = findViewById(R.id.iv_card);
        favourite = findViewById(R.id.iv_favourite);
        TextView address_book = findViewById(R.id.address_book);
        TextView account_delet = findViewById(R.id.request_account_deletion);
        TextView reset_password = findViewById(R.id.tv_Reset_password);


        address_book.setOnClickListener(v -> {startActivity(new Intent(SettingActivity.this, AddressActivity.class));});
        reset_password.setOnClickListener(v -> {startActivity(new Intent(SettingActivity.this, ResetPasswordActivity.class));});
        btn_order.setOnClickListener(v -> {startActivity(new Intent(SettingActivity.this, OrderActivity.class));});
        account_delet.setOnClickListener(v -> {startActivity(new Intent(SettingActivity.this, AccuntDeleteActivity.class));});

        // main 3 page
        home.setOnClickListener(v -> {startActivity(new Intent(SettingActivity.this, ProductsDisplay.class));});
        card.setOnClickListener(v -> {startActivity(new Intent(SettingActivity.this, CardActivity.class));});
        favourite.setOnClickListener(v -> {startActivity(new Intent(SettingActivity.this, FavouriteActivity.class));});


        btn_logout.setOnClickListener(v -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Toast.makeText(SettingActivity.this, "Logout", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close the current activity
            //If the target activity is already running in the current task, all activities on top of it are cleared (removed from the stack).
            // The target activity is brought to the front instead of creating a new instance.

        });

        statusBar();
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