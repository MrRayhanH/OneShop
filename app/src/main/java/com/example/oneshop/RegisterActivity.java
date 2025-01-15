package com.example.oneshop;

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

import com.example.oneshop.DatabaseHelper;
import com.example.oneshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        EditText et_Username = findViewById(R.id.et_signup_username);
        EditText et_Email = findViewById(R.id.et_email);
        EditText et_Password = findViewById(R.id.et_password);
        EditText et_ConfirmPassword = findViewById(R.id.et_confirmPassword);
        EditText et_Mobile = findViewById(R.id.et_phone);
        Button btn_Register = findViewById(R.id.btn_SU_Signup);

        TextView tv_Login = findViewById(R.id.tv_SU_login);



        btn_Register.setOnClickListener(v->{
            String username = et_Username.getText().toString();
            String email = et_Email.getText().toString();
            String password = et_Password.getText().toString();
            String confirmPassword = et_ConfirmPassword.getText().toString();
            String mobile = et_Mobile.getText().toString();

            et_Username.setText("");
            et_Email.setText("");
            et_Password.setText("");
            et_ConfirmPassword.setText("");
            et_Mobile.setText("");


            if (username.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Username is empty", Toast.LENGTH_SHORT).show();
            }
            else if (email.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
            }
            else if (password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
            }
            else if (confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Confirm Password is empty", Toast.LENGTH_SHORT).show();
            }
            else if(!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else if (mobile.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Mobile is empty", Toast.LENGTH_SHORT).show();
            }
            else {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                assert user != null;
                                user.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, SendVerificationActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegisterActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

//            if (password.equals(confirmPassword) && !password.isEmpty() && !username.isEmpty()){
//                Toast.makeText(RegisterActivity.this, "well done! Let me insert your info in DB!", Toast.LENGTH_SHORT).show();
//                //connection with DB
//                DatabaseHelper dbHelper = new DatabaseHelper(RegisterActivity.this);
//                boolean isInserted = dbHelper.insertUser(username, email, password, mobile);
//
//                if (isInserted) {
//                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(RegisterActivity.this, "Passwords do not match or empty password or empty username!", Toast.LENGTH_SHORT).show();
//
//            }

        });

        tv_Login.setOnClickListener(v -> finish());

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