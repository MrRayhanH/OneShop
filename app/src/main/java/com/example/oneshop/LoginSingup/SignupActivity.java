
package com.example.oneshop.LoginSingup;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.oneshop.R;
import com.example.oneshop.Password.SendVerificationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private boolean isPasswordVisible1st = false, isPasswordVisible2nd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        EditText et_Username = findViewById(R.id.et_signup_username);
        EditText et_Email = findViewById(R.id.et_email);
        EditText et_Password = findViewById(R.id.et_password);
        EditText et_ConfirmPassword = findViewById(R.id.et_confirmPassword);
        EditText et_Mobile = findViewById(R.id.et_phone);
        Spinner spinnerRole = findViewById(R.id.spinnerRole);
        Button btn_Register = findViewById(R.id.btn_SU_Signup);

        TextView tv_Login = findViewById(R.id.tv_SU_login);
        ImageView iv_eye1st = findViewById(R.id.iv_eye1st);
        ImageView iv_eye2nd = findViewById(R.id.iv_eye2nd);

        iv_eye1st.setOnClickListener(v -> togglePasswordVisibility(et_Password, iv_eye1st, true));
        iv_eye2nd.setOnClickListener(v -> togglePasswordVisibility(et_ConfirmPassword, iv_eye2nd, false));

        btn_Register.setOnClickListener(v -> {
            String username = et_Username.getText().toString();
            String email = et_Email.getText().toString();
            String password = et_Password.getText().toString();
            String confirmPassword = et_ConfirmPassword.getText().toString();
            String mobile = et_Mobile.getText().toString();
            String role = spinnerRole.getSelectedItem().toString();  // Get selected role (User, Seller, Admin)

            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            String usernameRegex = "^[a-zA-Z0-9._-]{6,}$";
            String phoneRegex = "^01[3-9]\\d{8,}$";
            String passwordRegex = "^[a-zA-Z0-9._-]{6,}$";

            if (username.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Username is empty", Toast.LENGTH_SHORT).show();
            } else if (!username.matches(usernameRegex)) {
                Toast.makeText(SignupActivity.this, "Invalid username format", Toast.LENGTH_SHORT).show();
            } else if (email.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
            } else if (!email.matches(emailRegex)) {
                Toast.makeText(SignupActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
            } else if (!password.matches(passwordRegex)) {
                Toast.makeText(SignupActivity.this, "Invalid password format", Toast.LENGTH_SHORT).show();
            } else if (confirmPassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Confirm Password is empty", Toast.LENGTH_SHORT).show();
            } else if (!confirmPassword.matches(passwordRegex)) {
                Toast.makeText(SignupActivity.this, "Invalid confirm password format", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (mobile.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Mobile is empty", Toast.LENGTH_SHORT).show();
            } else if (!mobile.matches(phoneRegex)) {
                Toast.makeText(SignupActivity.this, "Invalid mobile format", Toast.LENGTH_SHORT).show();
            } else {
                lottieLoderAnimation();
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    user.sendEmailVerification();
                                    String userId = user.getUid();
                                    saveUserData(userId, username, email, mobile, role);
                                }
                            } else {
                                Toast.makeText(SignupActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        tv_Login.setOnClickListener(v -> finish());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_color_white));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void saveUserData(String userId, String username, String email, String mobile, String role) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("user_id", userId);
        userMap.put("name", username);
        userMap.put("email", email);
        userMap.put("mobile", mobile);
        userMap.put("role", role);
        userMap.put("created_at", System.currentTimeMillis());

        databaseReference.child(userId).setValue(userMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(SignupActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, SendVerificationActivity.class);
                        startActivity(intent);
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Failed to save user data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void togglePasswordVisibility(EditText editText, ImageView eyeIcon, boolean isFirst) {
        boolean isPasswordVisible = isFirst ? isPasswordVisible1st : isPasswordVisible2nd;

        if (isPasswordVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeIcon.setImageResource(R.drawable.ic_eye_close);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeIcon.setImageResource(R.drawable.ic_eye);
        }
        editText.setSelection(editText.getText().length());
        if (isFirst) {
            isPasswordVisible1st = !isPasswordVisible1st;
        } else {
            isPasswordVisible2nd = !isPasswordVisible2nd;
        }
    }

    private void lottieLoderAnimation() {
        LottieAnimationView lottieLoader = findViewById(R.id.lottiePopup);
        lottieLoader.setVisibility(View.VISIBLE);
        lottieLoader.playAnimation();
    }
}
