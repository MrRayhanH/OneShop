package com.example.myapplication.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText et_Username = findViewById(R.id.et_User_Name);
        EditText et_Password = findViewById(R.id.et_login_password);
        Button btn_Login = findViewById(R.id.btn_login);
        TextView tv_signup = findViewById(R.id.tv_signUpText);
        TextView tv_forgetPass = findViewById(R.id.tv_forgetPassword);


        tv_signup.setOnClickListener(v->{Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);startActivity(intent);});

        btn_Login.setOnClickListener(v->{
            String username = et_Username.getText().toString();
            String password = et_Password.getText().toString();

            et_Username.setText("");
            et_Password.setText("");

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(username.equals("admin") && password.equals("admin")){Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);startActivity(intent);}
                else
                {
                    DatabaseHelper dbHelper = new DatabaseHelper(LoginActivity.this);
                    boolean result = dbHelper.checkUserByUsername(username, password);
                    if(result) { Intent intent = new Intent(LoginActivity.this, ProductsDisplay.class);startActivity(intent);}
                    else{ Toast.makeText(LoginActivity.this, "Invalid Username and password!", Toast.LENGTH_SHORT).show();}
                }
            }
        });

        tv_forgetPass.setOnClickListener(v -> {Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);startActivity(intent);});

        // Change the status bar color
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