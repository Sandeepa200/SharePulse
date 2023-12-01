package com.example.sharepulse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class LocationAddForm extends AppCompatActivity {
    private Button startTimeBtn, endTimeBtn, startDateBtn, endDateBtn, cancelBtn, addBtn;

    private ImageView imagePreview;
    private ProgressBar progressBar;
    private TextView centerName, centerDescript, centerLink, centerXco, centerYco, centerAddress;

    private Uri imageUri;

    private ImagePickerHelper imagePickerHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Donor Camp Adding");
        setContentView(R.layout.activity_location_add_form);

        // initializing progress bar
        progressBar = findViewById(R.id.progressBarAddLocation);
        progressBar.setVisibility(View.GONE);

        // navigation buttons
        addBtn = findViewById(R.id.add_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        // linking all the input fields
        centerName = findViewById(R.id.center_name);
        centerDescript = findViewById(R.id.center_discrip);
        centerLink = findViewById(R.id.center_link);
        centerXco = findViewById(R.id.x_co);
        centerYco = findViewById(R.id.y_co);
        centerAddress = findViewById(R.id.center_address);

        startDateBtn = findViewById(R.id.start_date_button);
        endDateBtn = findViewById(R.id.end_date_button);
        startTimeBtn = findViewById(R.id.start_time_button);
        endTimeBtn = findViewById(R.id.end_time_button);
        imagePreview = findViewById(R.id.image_preview);
        Button chooseImageButton = findViewById(R.id.choose_image_button);

        // cancel Button
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocationAddForm.this, AdminDashboard.class);
                startActivity(intent);
                finish();
            }
        });

        // time picking
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(startTimeBtn);
            }
        });

        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(endTimeBtn);
            }
        });

        // time picking
        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender(startDateBtn);
            }
        });

        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender(endDateBtn);
            }
        });

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        imagePickerHelper = new ImagePickerHelper(this, new ImagePickerHelper.ImagePickerCallback() {
            @Override
            public void onImageSelected(Bitmap bitmap, Uri imageUri) {
                // Handle the selected image
                imagePreview.setImageBitmap(bitmap);
                // Store the imageUri for later use
                LocationAddForm.this.imageUri = imageUri;
            }
        });


        // add button click listener
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    progressBar.setVisibility(View.VISIBLE);

                    // Get all the input values
                    String name = centerName.getText().toString();
                    String description = centerDescript.getText().toString();
                    String link = centerLink.getText().toString();
                    String xCoordinate = centerXco.getText().toString();
                    String yCoordinate = centerYco.getText().toString();
                    String startDate = startDateBtn.getText().toString();
                    String endDate = endDateBtn.getText().toString();
                    String address = centerAddress.getText().toString();
                    String startTime = startTimeBtn.getText().toString();
                    String endTime = endTimeBtn.getText().toString();

                    // Generate a unique key for the donation camp
                    String campId = FirebaseDatabase.getInstance().getReference().child("donationCamps").push().getKey();

                    // Create a DonationCamp object with the input values
                    DonationCamp donationCamp = new DonationCamp(name, description, link, xCoordinate, yCoordinate,
                            startDate, endDate, address, startTime, endTime, campId);

                    // Store the donationCamp object in the Firebase Realtime Database
                    FirebaseDatabase.getInstance().getReference().child("donationCamps")
                            .child(name)
                            .setValue(donationCamp)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Donation camp data added successfully
                                    uploadImageToStorage(campId); // Upload the image to Firebase Storage

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Error occurred while adding donation camp data
                                    Toast.makeText(LocationAddForm.this, "Failed to add donation camp data!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });

    }

    private boolean validateFields() {
        // Validate all the input fields
        if (TextUtils.isEmpty(centerName.getText().toString())) {
            centerName.setError("Please enter the center name");
            return false;
        }

        if (TextUtils.isEmpty(centerDescript.getText().toString())) {
            centerDescript.setError("Please enter the center description");
            return false;
        }

        if (TextUtils.isEmpty(centerLink.getText().toString())) {
            centerLink.setError("Please enter the center link");
            return false;
        }

        // Validate X and Y coordinates
        String xCoordinateText = centerXco.getText().toString();
        if (TextUtils.isEmpty(xCoordinateText)) {
            centerXco.setError("Please enter the X coordinate");
            return false;
        }
        double xCoordinate;
        try {
            xCoordinate = Double.parseDouble(xCoordinateText);
        } catch (NumberFormatException e) {
            centerXco.setError("Invalid X coordinate. Please enter a valid number.");
            return false;
        }

        String yCoordinateText = centerYco.getText().toString();
        if (TextUtils.isEmpty(yCoordinateText)) {
            centerYco.setError("Please enter the Y coordinate");
            return false;
        }
        double yCoordinate;
        try {
            yCoordinate = Double.parseDouble(yCoordinateText);
        } catch (NumberFormatException e) {
            centerYco.setError("Invalid Y coordinate. Please enter a valid number.");
            return false;
        }

        if (TextUtils.isEmpty(startDateBtn.getText().toString())) {
            Toast.makeText(this, "Please select the start date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(endDateBtn.getText().toString())) {
            Toast.makeText(this, "Please select the end date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(centerAddress.getText().toString())) {
            centerAddress.setError("Please enter the center address");
            return false;
        }

        if (TextUtils.isEmpty(startTimeBtn.getText().toString())) {
            Toast.makeText(this, "Please select the start time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(endTimeBtn.getText().toString())) {
            Toast.makeText(this, "Please select the end time", Toast.LENGTH_SHORT).show();
            return false;
        }



        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showTimePickerDialog(final Button timeButton) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                LocationAddForm.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Convert 24-hour format to 12-hour format
                        String format = (hourOfDay < 12) ? "AM" : "PM";
                        int hour = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, format);
                        timeButton.setText(selectedTime);
                    }
                },
                hour,
                minute,
                false // set true if you want the TimePickerDialog to display in 24-hour format
        );
        timePickerDialog.show();
    }

    private void openCalender(final Button dateButton) {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dateButton.setText(String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day));
            }
        }, 2023, 0, 15);
        dialog.show();
    }

    private void openImagePicker() {
        imagePickerHelper.openImagePicker();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePickerHelper.handleActivityResult(requestCode, resultCode, data);
    }


    private void uploadImageToStorage(String campId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("donationCampImages/" + campId + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        Toast.makeText(LocationAddForm.this, "Location added successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LocationAddForm.this, AdminDashboard.class);
                        startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while uploading the image
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LocationAddForm.this, "Failed to upload the image!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
