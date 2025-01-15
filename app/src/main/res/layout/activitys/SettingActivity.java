package com.example.myapplication.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ImageView home, card, favourite;
        home = findViewById(R.id.iv_home);
        card = findViewById(R.id.iv_card);
        favourite = findViewById(R.id.iv_favourite);

        home.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, ProductsDisplay.class));
        });

        card.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, CardActivity.class));
        });

        favourite.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, FavouriteActivity.class));
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