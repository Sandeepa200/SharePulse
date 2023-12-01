package com.example.sharepulse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DonorFormAccepting extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> dataList;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_form_accepting);

        databaseReference = FirebaseDatabase.getInstance().getReference("BloodDonationRequests");
        dataList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);

        listView = findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Handle list item click here
                String listItem = arrayAdapter.getItem(position);
                String[] listItemParts = listItem.split("\n"); // Assuming each line represents a different field

                if (listItemParts.length == 4) {
                    String donationRequestId = listItemParts[1].substring(listItemParts[1].indexOf(":") + 2); // Extract DonationRequest ID

                    // Retrieve detailed data for the clicked item using the DonationRequest ID
                    showUserDetailsPopup(donationRequestId);
                }
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();
                for (DataSnapshot parentSnapshot : dataSnapshot.getChildren()) {
                    String donationRequestID = parentSnapshot.getKey(); // Get the DonationRequest ID (parent node)

                    // the structure under each user ID includes "currentDateAndTime" field
                    String currentDateAndTime = parentSnapshot.child("currentDateAndTime").getValue(String.class);

                    // Separate date and time components
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    try {
                        Date dateTime = sdf.parse(currentDateAndTime);

                        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String date = dateSdf.format(dateTime);

                        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        String time = timeSdf.format(dateTime);

                        // Assuming the structure under each user ID also includes "fName" and "lName" fields
                        String fName = parentSnapshot.child("fName").getValue(String.class);
                        String lName = parentSnapshot.child("lName").getValue(String.class);

                        // Concatenate first name and last name to get the full name
                        String userName = fName + " " + lName;

                        // Add the details to the dataList
                        dataList.add("User Name: " + userName + "\nUser ID: " + donationRequestID + "\nDate: " + date + "\nTime: " + time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(DonorFormAccepting.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showUserDetailsPopup(String DonationRequestId) {
        DatabaseReference userRef = databaseReference.child(DonationRequestId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get user details from the dataSnapshot
                String centerCode = dataSnapshot.child("centerCode").getValue(String.class);
                String userId = dataSnapshot.child("userId").getValue(String.class);
                String fName = dataSnapshot.child("fName").getValue(String.class);
                String lName = dataSnapshot.child("lName").getValue(String.class);
                String user = dataSnapshot.child("user").getValue(String.class);
                String mobileNo = dataSnapshot.child("mobileNo").getValue(String.class);
                String bDate = dataSnapshot.child("bDate").getValue(String.class);
                String gender = dataSnapshot.child("gender").getValue(String.class);
                String nic = dataSnapshot.child("nic").getValue(String.class);
                boolean athsmaChecked = dataSnapshot.child("athsmaChecked").getValue(Boolean.class);
                boolean cancerChecked = dataSnapshot.child("cancerChecked").getValue(Boolean.class);
                boolean hivChecked = dataSnapshot.child("hivChecked").getValue(Boolean.class);
                boolean is16WeekChecked = dataSnapshot.child("is16WeekChecked").getValue(Boolean.class);
                boolean noMedicineChecked = dataSnapshot.child("noMedicineChecked").getValue(Boolean.class);
                boolean noPregnantChecked = dataSnapshot.child("noPregnantChecked").getValue(Boolean.class);
                boolean noTransfusionChecked = dataSnapshot.child("noTransfusionChecked").getValue(Boolean.class);
                boolean surgeryChecked = dataSnapshot.child("surgeryChecked").getValue(Boolean.class);

                // Create and show the popup dialog
                final Dialog dialog = new Dialog(DonorFormAccepting.this);
                dialog.setContentView(R.layout.donation_request_details_popup);

                TextView centerCodeTextView = dialog.findViewById(R.id.centerCodeTextView);
                TextView userIdTextView = dialog.findViewById(R.id.userIdTextView);
                TextView fNameTextView = dialog.findViewById(R.id.fNameTextView);
                TextView lNameTextView = dialog.findViewById(R.id.lNameTextView);
                TextView userTextView = dialog.findViewById(R.id.userTextView);
                TextView mobileNoTextView = dialog.findViewById(R.id.mobileNoTextView);
                TextView bDateTextView = dialog.findViewById(R.id.bDateTextView);
                TextView genderTextView = dialog.findViewById(R.id.genderTextView);
                TextView nicTextView = dialog.findViewById(R.id.nicTextView);
                TextView athsmaCheckedTextView = dialog.findViewById(R.id.athsmaCheckedTextView);
                TextView cancerCheckedTextView = dialog.findViewById(R.id.cancerCheckedTextView);
                TextView hivCheckedTextView = dialog.findViewById(R.id.hivCheckedTextView);
                TextView is16WeekCheckedTextView = dialog.findViewById(R.id.is16WeekCheckedTextView);
                TextView noMedicineCheckedTextView = dialog.findViewById(R.id.noMedicineCheckedTextView);
                TextView noPregnantCheckedTextView = dialog.findViewById(R.id.noPregnantCheckedTextView);
                TextView noTransfusionCheckedTextView = dialog.findViewById(R.id.noTransfusionCheckedTextView);
                TextView surgeryCheckedTextView = dialog.findViewById(R.id.surgeryCheckedTextView);

                centerCodeTextView.setText("Center Code: " + centerCode);
                userIdTextView.setText("user Id: " + userId);
                fNameTextView.setText("First Name: " + fName);
                lNameTextView.setText("Last Name: " + lName);
                userTextView.setText("User Email: " + user);
                mobileNoTextView.setText("Mobile No: " + mobileNo);
                bDateTextView.setText("Birth Date: " + bDate);
                genderTextView.setText("Gender: " + gender);
                nicTextView.setText("NIC: " + nic);
                athsmaCheckedTextView.setText("Athsma Checked: " + athsmaChecked);
                cancerCheckedTextView.setText("Cancer Checked: " + cancerChecked);
                hivCheckedTextView.setText("HIV Checked: " + hivChecked);
                is16WeekCheckedTextView.setText("16 Week Checked: " + is16WeekChecked);
                noMedicineCheckedTextView.setText("No Medicine Checked: " + noMedicineChecked);
                noPregnantCheckedTextView.setText("Not Pregnant Checked: " + noPregnantChecked);
                noTransfusionCheckedTextView.setText("No Transfusion Checked: " + noTransfusionChecked);
                surgeryCheckedTextView.setText("Surgery Checked: " + surgeryChecked);

                Button acceptButton = dialog.findViewById(R.id.acceptButton);
                Button cancelButton = dialog.findViewById(R.id.cancelButton);
                Button backButton = dialog.findViewById(R.id.backButton);

                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle accept button click
                        Toast.makeText(DonorFormAccepting.this, "Accept button clicked", Toast.LENGTH_SHORT).show();
                        updateStatus(DonationRequestId, "Approved");
                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle cancel button click
                        Toast.makeText(DonorFormAccepting.this, "Cancel button clicked", Toast.LENGTH_SHORT).show();
                        updateStatus(DonationRequestId, "Decline");
                        dialog.dismiss();
                    }
                });

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(DonorFormAccepting.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus(String DonationRequestId, String status) {
        DatabaseReference userRef = databaseReference.child(DonationRequestId).child("status");
        userRef.setValue(status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Status updated successfully
                        Toast.makeText(DonorFormAccepting.this, "Status updated: " + status, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to update status
                        Toast.makeText(DonorFormAccepting.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
