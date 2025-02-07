package com.example.oneshop.User;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oneshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddressActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        TextView tv_address = findViewById(R.id.tv_address);
        EditText et_address = findViewById(R.id.et_address);
        Button bt_address = findViewById(R.id.bt_address);
        ImageView adminBackArrow = findViewById(R.id.adminBackArrow);

        // Initialize Firebase Authentication and Database Reference
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Get current user's UID
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            tv_address.setText("User not logged in.");
            return;
        }

        String userId = currentUser.getUid();

        // Load and display user address
        loadUserAddress(userId, tv_address);

        // Back arrow click listener
        adminBackArrow.setOnClickListener(view -> finish());

        // Button click listener to add or update the address
        bt_address.setOnClickListener(view -> {
            String newAddress = et_address.getText().toString().trim();

            if (TextUtils.isEmpty(newAddress)) {
                et_address.setError("Please enter an address");
                return;
            }

            saveOrUpdateAddress(userId, newAddress, tv_address);
        });
    }

    private void loadUserAddress(String userId, TextView tv_address) {
        databaseReference.child(userId).child("address").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String address = task.getResult().getValue(String.class);
                if (address != null) {
                    tv_address.setText("Your Address: " + address);
                } else {
                    tv_address.setText("Address Not Found. Please add one.");
                }
            } else {
                tv_address.setText("Error loading address");
            }
        });
    }

    private void saveOrUpdateAddress(String userId, String newAddress, TextView tv_address) {
        databaseReference.child(userId).child("address").setValue(newAddress).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                tv_address.setText("Your Address: " + newAddress);
            } else {
                tv_address.setText("Failed to update address");
            }
        });
    }
}
