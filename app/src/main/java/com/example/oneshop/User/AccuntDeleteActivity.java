package com.example.oneshop.User;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oneshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccuntDeleteActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button btnDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accunt_delete);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // Reference to "Users" node

        // Find the delete account button
        btnDeleteAccount = findViewById(R.id.btn_delete_account);

        // Set a click listener on the button to delete account
        btnDeleteAccount.setOnClickListener(v -> {
            deleteAccount();
        });
    }

    private void deleteAccount() {
        // Get the current user's UID
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Delete the user's data from Firebase Realtime Database
        databaseReference.child(userId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Data deleted successfully from Firebase Realtime Database
                // Now delete the user from Firebase Authentication
                firebaseAuth.getCurrentUser().delete().addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        // Account deleted successfully from Firebase Authentication
                        Toast.makeText(AccuntDeleteActivity.this, "Account deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after deletion
                    } else {
                        // Error deleting account from Firebase Authentication
                        Toast.makeText(AccuntDeleteActivity.this, "Error deleting account!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Error deleting data from Firebase Realtime Database
                Toast.makeText(AccuntDeleteActivity.this, "Error deleting data from Realtime Database!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
