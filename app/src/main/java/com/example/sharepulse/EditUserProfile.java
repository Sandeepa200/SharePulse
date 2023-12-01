package com.example.sharepulse;

import static android.content.ContentValues.TAG;

import static com.example.sharepulse.ImageHelper.getRoundedCornerBitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class EditUserProfile extends AppCompatActivity {

    private Button BODbtn, toDashboardbtn, editUserDatabtn;
    private TextView birthDate;
    private ImageView profilePic;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private TextView  fnameDisplay, lnameDisplay, emailDisplay, nicDisplay, mobileDisplay, genderDisplay, toDeleteScreenBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_user_profile);

        progressBar = findViewById(R.id.progressBar5);
        progressBar.setVisibility(View.GONE);

        //firebase object creation
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        fnameDisplay = findViewById(R.id.Fname);
        lnameDisplay = findViewById(R.id.Lname);
        //emailDisplay = findViewById(R.id.email);
        nicDisplay = findViewById(R.id.nic);
        mobileDisplay = findViewById(R.id.mobile);
        genderDisplay = findViewById(R.id.selectGender);
        birthDate = findViewById(R.id.inputBirthDate);
        profilePic = findViewById(R.id.user_profile_picture);


        fnameDisplay.setText(getIntent().getStringExtra("FNAME"));
        lnameDisplay.setText(getIntent().getStringExtra("LNAME"));
        //emailDisplay.setText(getIntent().getStringExtra("EMAIL"));
        nicDisplay.setText(getIntent().getStringExtra("NIC"));
        mobileDisplay.setText(getIntent().getStringExtra("MOBILE"));
        genderDisplay.setText(getIntent().getStringExtra("GENDER"));
        birthDate.setText(getIntent().getStringExtra("DOB"));
        String imageID = getIntent().getStringExtra("IMAGE");

        int radius = 100; // Corner radius in pixels
        if(imageID != null){
            Picasso.get()
                    .load(imageID)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Bitmap roundedBitmap = getRoundedCornerBitmap(bitmap, radius);
                            profilePic.setImageBitmap(roundedBitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Toast.makeText(EditUserProfile.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
        }else{
            Toast.makeText(EditUserProfile.this, "No profile picture in this account!", Toast.LENGTH_SHORT).show();
        }

        //navigation
        toDeleteScreenBtn = findViewById(R.id.toDeleteAccBtn);
        toDeleteScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditUserProfile.this,DeleteUser.class);
                startActivity(intent);
            }
        });

        toDashboardbtn = findViewById(R.id.toDashboard);
        toDashboardbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditUserProfile.this,Dashboard.class);
                startActivity(intent);
            }
        });

        //BOD selection
        BODbtn = findViewById(R.id.dateSelect);
        birthDate = findViewById(R.id.inputBirthDate);

        BODbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarHelper.openCalendar(EditUserProfile.this, getIntent().getStringExtra("DOB"),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int years, int months, int days) {
                            birthDate.setText(String.valueOf(years) + "/" + String.valueOf(months + 1) + "/" + String.valueOf(days));
                        }
                });

            }
        });

        //gender selection
        String[] genders = {"Male","Female","Non Binary"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.gender_dropdown,genders);
        AutoCompleteTextView autoCompleteTextView=findViewById(R.id.selectGender);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(EditUserProfile.this, autoCompleteTextView.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        //altering user database
        editUserDatabtn = findViewById(R.id.toSaveEdit);
        editUserDatabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                //String user = emailDisplay.getText().toString().trim();
                String fName = fnameDisplay.getText().toString().trim();
                String lName = lnameDisplay.getText().toString().trim();
                String mobileNo = mobileDisplay.getText().toString().trim();
                String gender = genderDisplay.getText().toString().trim();
                String nic = nicDisplay.getText().toString().trim();
                String bDate = birthDate.getText().toString().trim();


                if(fName.isEmpty()){
                    fnameDisplay.setError("First Name Required!");
                    fnameDisplay.requestFocus();
                    return;
                }
                if(lName.isEmpty()){
                    lnameDisplay.setError("Last Name Required!");
                    lnameDisplay.requestFocus();
                    return;
                }/*
                if(user.isEmpty()){
                    emailDisplay.setError("Email Required!");
                    emailDisplay.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(user).matches()){
                    emailDisplay.setError("Valid Email Required!");
                    emailDisplay.requestFocus();
                    return;
                }*/
                if(mobileNo.isEmpty()){
                    mobileDisplay.setError("Mobile Number Required!");
                    mobileDisplay.requestFocus();
                    return;
                }
                if(gender.isEmpty()){
                    genderDisplay.setError("Gender Required!");
                    genderDisplay.requestFocus();
                    return;
                }
                if(nic.isEmpty()){
                    nicDisplay.setError("NIC Number Required!");
                    nicDisplay.requestFocus();
                    return;
                }
                if(bDate.isEmpty()){
                    birthDate.setError("Date of Birth Required!");
                    birthDate.requestFocus();
                    return;
                }else{
                    progressBar.setVisibility(View.VISIBLE);


                    //Inserting the user details to the database
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(fName,lName,mobileNo,gender,nic,bDate);

                    //getting the reference from the database
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(EditUserProfile.this, "Changes saved Successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent(EditUserProfile.this, UserProfile.class);
                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent2);
                                finish();
                            }else{
                                Toast.makeText(EditUserProfile.this, "Changes Not Saved!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    progressBar.setVisibility(View.GONE);

                }
            }
        });

    }

}