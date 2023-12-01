package com.example.sharepulse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLogin extends AppCompatActivity {

    private TextView username, password;
    private Button adminLoginBtn;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        getSupportActionBar().setTitle("Admin Login");

        // Initialize the Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Admins");

        // Linking
        username = findViewById(R.id.adminUsername);
        password = findViewById(R.id.adminPassword);
        progressBar = findViewById(R.id.progressBar8);
        adminLoginBtn = findViewById(R.id.adminLoginBtn);

        // Hide progress bar when starting
        progressBar.setVisibility(View.GONE);

        // Admin login function
        adminLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String adUser = username.getText().toString().trim();
                String adPass = password.getText().toString().trim();

                if (adUser.isEmpty() || adPass.isEmpty()) {
                    Toast.makeText(AdminLogin.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Show progress bar while logging in
                    progressBar.setVisibility(View.VISIBLE);

                    // Retrieve the password from the Firebase Realtime Database
                    databaseReference.child(adUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Hide progress bar after retrieving the password
                            progressBar.setVisibility(View.GONE);

                            if (dataSnapshot.exists()) {
                                String actualPassword = dataSnapshot.child("password").getValue(String.class);
                                if (actualPassword.equals(adPass)) {
                                    // Login successful, proceed with your logic
                                    Toast.makeText(AdminLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    // Add your logic for successful login

                                    Intent intent = new Intent(AdminLogin.this, AdminDashboard.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // Incorrect password
                                    Toast.makeText(AdminLogin.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Username not found
                                Toast.makeText(AdminLogin.this, "Username not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Hide progress bar on error
                            progressBar.setVisibility(View.GONE);
                            // Handle any errors
                            Toast.makeText(AdminLogin.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
