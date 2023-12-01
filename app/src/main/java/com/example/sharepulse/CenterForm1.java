package com.example.sharepulse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CenterForm1 extends AppCompatActivity {

    private Button toCenterForm, toSubmit;
    private CheckBox checkBox16week, checkBoxPregnant, checkBoxTransfusion, checkBoxMedicine,
            checkBoxSurgery, checkBoxAthsma, checkBoxCancer, checkBoxHiv;

    ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_center_form1);

        progressBar = findViewById(R.id.progressBar_donationRequest);
        progressBar.setVisibility(View.GONE);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Get the user ID
        String userId = currentUser.getUid();

        // Getting components from previous screen
        String fName = getIntent().getStringExtra("FNAME");
        String lName = getIntent().getStringExtra("LNAME");
        String user = getIntent().getStringExtra("EMAIL");
        String mobileNo = getIntent().getStringExtra("MOBILE");
        String gender = getIntent().getStringExtra("GENDER");
        String nic = getIntent().getStringExtra("NIC");
        String bDate = getIntent().getStringExtra("BDAY");
        String centerCode = getIntent().getStringExtra("QR_CODE");

        toCenterForm = findViewById(R.id.backBtn);
        toCenterForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CenterForm1.this, CenterForm.class);
                startActivity(intent);
            }
        });

        toSubmit = findViewById(R.id.submitBtn);
        toSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                // Get the current date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String currentDateAndTime = sdf.format(new Date());

                // Generate a random number between 1000 and 9999
                int randomNumber = (int) (Math.random() * 9000) + 1000;

                // Concatenate the random number with the userID
                String DonationRequestId = userId + randomNumber;

                // Linking the checkboxes
                checkBox16week = findViewById(R.id.checkBox_16week);
                checkBoxPregnant = findViewById(R.id.checkBox_pregnant);
                checkBoxTransfusion = findViewById(R.id.checkBox_transfusion);
                checkBoxMedicine = findViewById(R.id.checkBox_medicine);
                checkBoxSurgery = findViewById(R.id.checkBox_surgery);
                checkBoxAthsma = findViewById(R.id.checkBox_athsma);
                checkBoxCancer = findViewById(R.id.checkBox_cancer);
                checkBoxHiv = findViewById(R.id.checkBox_hiv);

                // Getting the status of the checkboxes
                boolean is16WeekChecked = checkBox16week.isChecked();
                boolean isNoPregnantChecked = checkBoxPregnant.isChecked();
                boolean isNoTransfusionChecked = checkBoxTransfusion.isChecked();
                boolean isNoMedicineChecked = checkBoxMedicine.isChecked();
                boolean isSurgeryChecked = checkBoxSurgery.isChecked();
                boolean isAthsmaChecked = checkBoxAthsma.isChecked();
                boolean isCancerChecked = checkBoxCancer.isChecked();
                boolean isHivChecked = checkBoxHiv.isChecked();

                // Validation
                if (!is16WeekChecked && !isNoPregnantChecked && !isNoTransfusionChecked &&
                        !isNoMedicineChecked && !isSurgeryChecked && !isAthsmaChecked &&
                        !isCancerChecked && !isHivChecked) {
                    Toast.makeText(CenterForm1.this, "Please select at least one condition", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // Create a new BloodDonationRequest object with the data
                BloodDonationRequest donationRequest = new BloodDonationRequest(fName, lName, user,userId, mobileNo, gender, nic, bDate, centerCode,
                        is16WeekChecked, isNoPregnantChecked, isNoTransfusionChecked, isNoMedicineChecked,
                        isSurgeryChecked, isAthsmaChecked, isCancerChecked, isHivChecked, currentDateAndTime);

                // Save the BloodDonationRequest object to the Firebase Realtime Database
                databaseReference.child("BloodDonationRequests").child(DonationRequestId).setValue(donationRequest)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(CenterForm1.this, "Request submitted successfully", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(CenterForm1.this, DonateSteps.class);
                                intent.putExtra("DonationRequestId", DonationRequestId);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Toast.makeText(CenterForm1.this, "Request submission canceled", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }
}
