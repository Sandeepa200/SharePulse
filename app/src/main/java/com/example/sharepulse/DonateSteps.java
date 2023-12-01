package com.example.sharepulse;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DonateSteps extends AppCompatActivity {

    private DatabaseReference donationRequestRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_donate_steps);

        String donationReqId = getIntent().getStringExtra("DonationRequestId");

        donationRequestRef = FirebaseDatabase.getInstance().getReference().child("BloodDonationRequests").child(donationReqId);
        donationRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String status = dataSnapshot.child("status").getValue(String.class);
                    if (status != null && status.equals("Approved")) {
                        Toast.makeText(DonateSteps.this, "Your request is approved", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(DonateSteps.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        navigateToDashboard();
    }

    private void navigateToDashboard() {
        startActivity(new Intent(DonateSteps.this, Dashboard.class));
        finish();
    }
}
