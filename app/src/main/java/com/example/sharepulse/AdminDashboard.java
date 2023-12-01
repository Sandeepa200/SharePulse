package com.example.sharepulse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminDashboard extends AppCompatActivity {

    private Button toLocationAdd, toDonorForm, adminLogoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        getSupportActionBar().setTitle("Admin Dashboard");

        toLocationAdd = findViewById(R.id.locationBtn);
        toLocationAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, LocationAddForm.class);
                startActivity(intent);
            }
        });

        toDonorForm = findViewById(R.id.DonorFormBtn);
        toDonorForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, DonorFormAccepting.class);
                startActivity(intent);
            }
        });

        adminLogoutBtn = findViewById(R.id.admin_logout_btn);
        adminLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

    }
}