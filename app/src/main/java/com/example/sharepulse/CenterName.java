package com.example.sharepulse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class CenterName extends AppCompatActivity {
    private Button toScanScreen, gmapDirections;

    private ProgressBar progressBar;
    private ImageView campImage;

    private TextView centerName, centerDescription, centerStartDate, centerEndDate,
            centerStartTime, centerEndTime, centerAddress, centerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        getSupportActionBar().hide();
        setContentView(R.layout.activity_center_name);

        progressBar = findViewById(R.id.progressBar9);
        progressBar.setVisibility(View.VISIBLE);

        // Retrieve the passed intents
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String startTime = intent.getStringExtra("startTime");
        String endTime = intent.getStringExtra("endTime");
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");
        String link = intent.getStringExtra("link");
        String description = intent.getStringExtra("description");
        String address = intent.getStringExtra("address");
        String xcoordinate = intent.getStringExtra("xcoordinate");
        String ycoordinate = intent.getStringExtra("ycoordinate");
        String campId = intent.getStringExtra("campID");

        // Use the retrieved values

        centerName = findViewById(R.id.center_name_display);
        centerDescription = findViewById(R.id.center_descript_view);
        centerStartDate = findViewById(R.id.start_date);
        centerEndDate = findViewById(R.id.end_date);
        centerStartTime = findViewById(R.id.start_time);
        centerEndTime = findViewById(R.id.end_time);
        centerAddress = findViewById(R.id.center_address);
        campImage = findViewById(R.id.center_image_display);
        centerLink = findViewById(R.id.center_link);

        // Set the text for TextViews
        centerName.setText(name);
        centerDescription.setText(description);
        centerStartDate.setText(startDate);
        centerEndDate.setText(endDate);
        centerStartTime.setText(startTime);
        centerEndTime.setText(endTime);
        centerAddress.setText(address);
        centerLink.setText(link);

        String imageFileName = "donationCampImages/" + campId + ".jpg"; // Adjust the file extension according to image type
        StorageReference imageRef = storageRef.child(imageFileName);
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use Picasso library to load the image into the ImageView
                Picasso.get().load(uri).into(campImage);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that occur during image retrieval
                Toast.makeText(CenterName.this, "Failed to retrieve image from Firebase" + campId, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        centerLink.setClickable(true);
        centerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyLink(v);
            }
        });

        toScanScreen = findViewById(R.id.toScanScreen);
        toScanScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(CenterName.this, codeScan.class);
                startActivity(intent1);
            }
        });

        gmapDirections = findViewById(R.id.directionBtn);
        gmapDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + xcoordinate + "," + ycoordinate));
                intent2.setPackage("com.google.android.apps.maps");

                if (intent2.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent2);
                }
            }
        });
    }

    public void copyLink(View view) {
        TextView textView = (TextView) view;
        String link = textView.getText().toString();

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Link", link);
        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}
