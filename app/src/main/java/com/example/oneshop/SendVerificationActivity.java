package com.example.oneshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oneshop.LoginSingup.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SendVerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_verification);

        Button resendCode = findViewById(R.id.btn_resend_code);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if(user.isEmailVerified()) {Intent intent = new Intent(SendVerificationActivity.this, LoginActivity.class);}
            else {
                resendCode.setOnClickListener(v -> {
                    //if(user.isEmailVerified()) {Intent intent = new Intent(SendVerificationActivity.this, LoginActivity.class);}
                    user.sendEmailVerification();
                    Toast.makeText(SendVerificationActivity.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                });
            }
        }





    }
}