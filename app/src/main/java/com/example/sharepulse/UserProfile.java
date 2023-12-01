package com.example.sharepulse;

import static com.example.sharepulse.ImageHelper.getRoundedCornerBitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class UserProfile extends AppCompatActivity {

    private Button toEditProfile, toDashboard;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private ImageView profilePicture;
    private String imageID;
    private TextView logoutBtn, fNameDisplayTop, lNameDisplayTop, fnameDisplay, lnameDisplay,
            emailDisplay, nicDisplay, mobileDisplay, genderDisplay, dobDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_user_profile);

        // user data displaying
        fNameDisplayTop = findViewById(R.id.fname_display_top);
        lNameDisplayTop = findViewById(R.id.lname_display_top);
        fnameDisplay = findViewById(R.id.fname_display);
        lnameDisplay = findViewById(R.id.lname_display);
        emailDisplay = findViewById(R.id.email_display);
        nicDisplay = findViewById(R.id.nic_display);
        mobileDisplay = findViewById(R.id.mobile_display);
        genderDisplay = findViewById(R.id.gender_display);
        dobDisplay = findViewById(R.id.dob_display);
        profilePicture = findViewById(R.id.profile_picture);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        progressBar = findViewById(R.id.progressBar3);


        //checking if the user is available or not
        if(firebaseUser == null){
            Toast.makeText(UserProfile.this, "user details are not available!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

        //navigation links
        toEditProfile = findViewById(R.id.toEditProfile);
        toEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this,EditUserProfile.class);
                intent.putExtra("FNAME", fnameDisplay.getText().toString());
                intent.putExtra("LNAME", lnameDisplay.getText().toString());
                intent.putExtra("EMAIL", emailDisplay.getText().toString());
                intent.putExtra("NIC", nicDisplay.getText().toString());
                intent.putExtra("MOBILE", mobileDisplay.getText().toString());
                intent.putExtra("GENDER", genderDisplay.getText().toString());
                intent.putExtra("DOB", dobDisplay.getText().toString());
                intent.putExtra("IMAGE", imageID);
                startActivity(intent);
            }
        });
        //navigation to dashboard
        toDashboard = findViewById(R.id.toDashboard);
        toDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this,Dashboard.class);
                startActivity(intent);
                finish();
            }
        });
        //logout functionalities
        logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserProfile.this, Login.class);
                intent.putExtra("message", "You are logged out");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
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
                    fNameDisplayTop.setText(readUserDetails.fName);
                    lNameDisplayTop.setText(readUserDetails.lName);
                    fnameDisplay.setText(readUserDetails.fName);
                    lnameDisplay.setText(readUserDetails.lName);
                    emailDisplay.setText(firebaseUser.getEmail());
                    nicDisplay.setText(readUserDetails.nic);
                    mobileDisplay.setText(readUserDetails.mobileNo);
                    genderDisplay.setText(readUserDetails.gender);
                    dobDisplay.setText(readUserDetails.bDate);
                    imageID = readUserDetails.imageUrl;
                    int radius = 100; // Corner radius in pixels
                    if(imageID != null){
                        Picasso.get()
                                .load(imageID)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        Bitmap roundedBitmap = getRoundedCornerBitmap(bitmap, radius);
                                        profilePicture.setImageBitmap(roundedBitmap);
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        Toast.makeText(UserProfile.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    }
                                });
                    }else{
                        Toast.makeText(UserProfile.this, "No profile picture in this account!", Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this, "user details are not linking!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}