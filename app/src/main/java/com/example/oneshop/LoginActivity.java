package com.example.oneshop;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.Lottie;
import com.example.oneshop.DatabaseHelper;
import com.example.oneshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.airbnb.lottie.LottieAnimationView;
public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private boolean isPasswordVisivle = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        EditText et_Username = findViewById(R.id.et_User_Name);
        EditText et_Password = findViewById(R.id.et_login_password);
        Button btn_Login = findViewById(R.id.btn_login);
        TextView tv_signup = findViewById(R.id.tv_signUpText);
        TextView tv_forgetPass = findViewById(R.id.tv_forgetPassword);
        ImageView iv_eye = findViewById(R.id.iv_eye);

        //View lottieLoader = findViewById(R.id.lottieLoader);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            Intent intent = new Intent(LoginActivity.this, ProductsDisplay.class);
            startActivity(intent);
            finish();
            return;
        }
        iv_eye.setOnClickListener(v -> { hideShowPass();});
        tv_signup.setOnClickListener(v->{Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);startActivity(intent);});

        btn_Login.setOnClickListener(v->{

            String Email = et_Username.getText().toString();
            String Password = et_Password.getText().toString();
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            String passwordRegex ="^[a-zA-Z0-9._-]{6,}$";;
            if (Email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Username is empty", Toast.LENGTH_SHORT).show();
            }
            else if(Email.equals("admin") && Password.equals("admin")){Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);startActivity(intent);}
            else if (!Email.matches(emailRegex)) {
                Toast.makeText(LoginActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();}
            else if(Password.isEmpty()){
                Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();}
            else if (!Password.matches(passwordRegex)) {
                Toast.makeText(LoginActivity.this, "Invalid password format", Toast.LENGTH_SHORT).show();}
            else {
                lottieLoderAnimation();
                mAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if(user != null && !user.isEmailVerified()){
                                        Intent intent = new Intent(LoginActivity.this, SendVerificationActivity.class);
                                        startActivity(intent);
                                         Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(user != null && user.isEmailVerified()){
                                        et_Username.setText("");
                                        et_Password.setText("");
                                        finish();
                                        updateUI(user);
                                    }
                                    else{
                                        Intent intent = new Intent(LoginActivity.this, SendVerificationActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(LoginActivity.this, "Please verify your email first", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Sign in failed
                                    Toast.makeText(LoginActivity.this, "Create accunt first " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
            }
        });

        tv_forgetPass.setOnClickListener(v -> {Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);startActivity(intent);});

        statusbar();

    }

        private void updateUI(FirebaseUser user) {
            if (user != null) {
                Intent intent = new Intent(LoginActivity.this, ProductsDisplay.class); // Change to your desired activity
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    private void hideShowPass() {
        EditText et_Password = findViewById(R.id.et_login_password);
        ImageView iv_eye = findViewById(R.id.iv_eye);

        if (isPasswordVisivle) {
            // Hide password
            et_Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            iv_eye.setImageResource(R.drawable.ic_eye); // Replace with closed-eye icon
            isPasswordVisivle = false;
        } else {
            // Show password
            et_Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            iv_eye.setImageResource(R.drawable.ic_eye); // Replace with open-eye icon
            isPasswordVisivle = true;
        }
        // Keep the cursor at the end of the text
        et_Password.setSelection(et_Password.getText().length());
    }
    private void lottieLoderAnimation(){
        LottieAnimationView lottieLoader = findViewById(R.id.lottiePopup);
        lottieLoader.setVisibility(View.VISIBLE);
        lottieLoader.playAnimation();
    }
    private void statusbar(){
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