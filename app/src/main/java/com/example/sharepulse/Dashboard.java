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
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


public class Dashboard extends AppCompatActivity {
    private Button toCenterSearchList;
    private ImageButton toMapView, startDonationBtn, historyBtn, profileBtn;

    private TextView userName;
    private ImageView userProfilePic;
    private ProgressBar progressBar;
    private String imageID;
    private EditText centerNameSearchInput;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dashboard);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar10);
        userName = findViewById(R.id.dashboardUserName);
        userProfilePic = findViewById(R.id.user_profile_pic);
        centerNameSearchInput = findViewById(R.id.centerSearchName);

        //checking if the user is available or not
        if(firebaseUser == null){
            Toast.makeText(Dashboard.this, "User details are not available!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }else{
            String user_ID = firebaseUser.getUid();
            DatabaseReference referenceP = FirebaseDatabase.getInstance().getReference("Registered Users");
            referenceP.child(user_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    if(readWriteUserDetails != null){
                        userName.setText("Hello "+readWriteUserDetails.fName);
                        imageID = readWriteUserDetails.imageUrl;
                        int radius = 100; // Corner radius in pixels
                        if(imageID != null){
                            Picasso.get()
                                    .load(imageID)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            Bitmap roundedBitmap = getRoundedCornerBitmap(bitmap, radius);
                                            userProfilePic.setImageBitmap(roundedBitmap);
                                            progressBar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                            Toast.makeText(Dashboard.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    });
                        }else{
                            Toast.makeText(Dashboard.this, "No profile picture in this account!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Dashboard.this, "user details are not linking!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

        toCenterSearchList = findViewById(R.id.toCenterSearch);
        toCenterSearchList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,CenterSearchT.class);
                intent.putExtra("CENTER_NAME", centerNameSearchInput.getText().toString());
                startActivity(intent);
            }
        });

        toMapView = findViewById(R.id.toMap);
        toMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,MapsActivity.class);
                startActivity(intent);
            }
        });

        startDonationBtn = findViewById(R.id.startDonationBtn);
        startDonationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,codeScan.class);
                startActivity(intent);
            }
        });

        historyBtn = findViewById(R.id.historyBtn);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,UserHistory.class);
                startActivity(intent);
            }
        });

        profileBtn = findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this,UserProfile.class);
                startActivity(intent);
            }
        });
    }
}