package com.example.sharepulse;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class codeScan extends AppCompatActivity {

    private Button scanCodeBtn, enterCodeBtn;
    private EditText manualQR;
    private DatabaseReference donationCampsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_code_scan);

        scanCodeBtn = findViewById(R.id.scanButton);
        scanCodeBtn.setOnClickListener(v->
        {
            codeScanning();
        });

        enterCodeBtn = findViewById(R.id.toCenterForm);
        manualQR = findViewById(R.id.QR_manual_input);

        enterCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String QRmanual = manualQR.getText().toString();
                if(QRmanual.isEmpty()){
                    manualQR.setError("You have to enter the code manually or scan using the scanner!");
                    manualQR.requestFocus();
                } else {
                    checkCenterCode(QRmanual);
                }
            }
        });
    }

    private void codeScanning() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Aim the camera at the code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private void checkCenterCode(String centerCode) {
        donationCampsRef = FirebaseDatabase.getInstance().getReference().child("donationCamps");
        Query query = donationCampsRef.orderByChild("campId").equalTo(centerCode);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String campName = snapshot.child("name").getValue(String.class);

                        AlertDialog.Builder builder = new AlertDialog.Builder(codeScan.this);
                        builder.setTitle("Results");
                        builder.setMessage("Camp Name: " + campName + "\nCenter Code: " + centerCode);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(codeScan.this, CenterForm.class);
                                intent.putExtra("QRcode", centerCode);
                                startActivity(intent);
                            }
                        }).show();

                        //there's only one matching camp
                        break;
                    }
                } else {
                    Toast.makeText(codeScan.this, "Invalid center code", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(codeScan.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String centerCode = result.getContents();
            checkCenterCode(centerCode);
        }
    });
}
