package com.example.sharepulse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CenterForm extends AppCompatActivity {

    private Button toCodeScanBack, toCenterForm1, BODbtn;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private TextView displayFname, displayLname, displayEmail, displayNIC,
            displayMobile, displayBdate, displayGender;
    private CheckBox checkBox_Drug, checkBox_Homo, checkBox_Sex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_center_form);

        progressBar = findViewById(R.id.progressBar7);
        // user data displaying
        displayFname = findViewById(R.id.Fname);
        displayLname = findViewById(R.id.Lname);
        displayEmail = findViewById(R.id.Email);
        displayNIC = findViewById(R.id.nic);
        displayMobile = findViewById(R.id.mobile);
        displayGender = findViewById(R.id.select_gender);
        checkBox_Drug = findViewById(R.id.checkBoxdrug);
        checkBox_Homo = findViewById(R.id.checkBoxhomo);
        checkBox_Sex = findViewById(R.id.checkBoxsex);
        progressBar.setVisibility(View.GONE);

        //navigation
        toCodeScanBack = findViewById(R.id.cancel);
        toCodeScanBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CenterForm.this,codeScan.class);
                startActivity(intent);
            }
        });

        toCenterForm1 = findViewById(R.id.next);
        toCenterForm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the checkboxes are checked
                if (!checkBox_Drug.isChecked()) {
                    checkBox_Drug.setError("required!");
                    return;
                }
                if (!checkBox_Homo.isChecked()) {
                    checkBox_Homo.setError("required!");
                    return;
                }
                if (!checkBox_Sex.isChecked()) {
                    checkBox_Sex.setError("required!");
                    return;
                }else{
                    String center_code = getIntent().getStringExtra("QRcode");
                    Intent intent = new Intent(CenterForm.this, CenterForm1.class);
                    intent.putExtra("FNAME", displayFname.getText().toString());
                    intent.putExtra("LNAME", displayLname.getText().toString());
                    intent.putExtra("EMAIL", displayEmail.getText().toString());
                    intent.putExtra("NIC", displayNIC.getText().toString());
                    intent.putExtra("MOBILE", displayMobile.getText().toString());
                    intent.putExtra("GENDER", displayGender.getText().toString());
                    intent.putExtra("BDAY", displayBdate.getText().toString());
                    intent.putExtra("QR_CODE", center_code);
                    startActivity(intent);
                }

            }
        });

        //firebase object creation
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();


        //checking if the user is available or not
        if(firebaseUser == null){
            Toast.makeText(CenterForm.this, "user details are not available!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

        //BOD selection
        BODbtn = findViewById(R.id.dateSelect);
        displayBdate = findViewById(R.id.inputBirthDate);

        BODbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarHelper.openCalendar(CenterForm.this, displayBdate.getText().toString().trim(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int years, int months, int days) {
                            displayBdate.setText(String.valueOf(years) + "/" + String.valueOf(months + 1) + "/" + String.valueOf(days));
                        }
                    });
            }

        });



    }
    private void showUserProfile(FirebaseUser firebaseUser){
        String userID = firebaseUser.getUid();

        //reading user details from the database
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    //inserting user details to screen
                    displayFname.setText(readUserDetails.fName);
                    displayLname.setText(readUserDetails.lName);
                    displayEmail.setText(firebaseUser.getEmail());
                    displayNIC.setText(readUserDetails.nic);
                    displayMobile.setText(readUserDetails.mobileNo);
                    displayBdate.setText(readUserDetails.bDate);
                    //gender selection
                    String[] genders = {"Male","Female","Non Binary"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CenterForm.this, R.layout.gender_dropdown, genders);
                    AutoCompleteTextView autoCompleteTextView = findViewById(R.id.select_gender);
                    autoCompleteTextView.setAdapter(adapter);
                    autoCompleteTextView.setText(readUserDetails.gender, false);

                    autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Toast.makeText(CenterForm.this, autoCompleteTextView.getText().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CenterForm.this, "user details are not linking!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}