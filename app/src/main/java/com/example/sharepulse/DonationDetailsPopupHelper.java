package com.example.sharepulse;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DonationDetailsPopupHelper {
    private DatabaseReference databaseReference;

    public DonationDetailsPopupHelper() {
        // Initialize the database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("DonationRequests");
    }

    public void showUserDetailsPopup(String donationRequestId, Context context, boolean isAdmin) {
        DatabaseReference userRef = databaseReference.child(donationRequestId);
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
                final Dialog dialog = new Dialog(context);
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
                userIdTextView.setText("User ID: " + userId);
                fNameTextView.setText("First Name: " + fName);
                lNameTextView.setText("Last Name: " + lName);
                userTextView.setText("User Email: " + user);
                mobileNoTextView.setText("Mobile No: " + mobileNo);
                bDateTextView.setText("Birth Date: " + bDate);
                genderTextView.setText("Gender: " + gender);
                nicTextView.setText("NIC: " + nic);
                athsmaCheckedTextView.setText("Asthma Checked: " + athsmaChecked);
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

                // Hide/show Buttons based on isAdmin flag
                if (isAdmin) {
                    acceptButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);
                } else {
                    acceptButton.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.GONE);
                    userIdTextView.setVisibility(View.GONE);
                }

                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle accept button click
                        Toast.makeText(context, "Accept button clicked", Toast.LENGTH_SHORT).show();
                        updateStatus(context, donationRequestId, "Approved");
                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle cancel button click
                        Toast.makeText(context, "Cancel button clicked", Toast.LENGTH_SHORT).show();
                        updateStatus(context, donationRequestId, "Decline");
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
                Toast.makeText(context, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void enableTextView(TextView textView) {
        if (textView != null) {
            textView.setEnabled(true);
        }
    }

    public void disableTextView(TextView textView) {
        if (textView != null) {
            textView.setEnabled(false);
        }
    }

    public void enableButton(Button button) {
        if (button != null) {
            button.setEnabled(true);
        }
    }

    public void disableButton(Button button) {
        if (button != null) {
            button.setEnabled(false);
        }
    }

    private void updateStatus(Context context, String DonationRequestId, String status) {
        DatabaseReference userRef = databaseReference.child(DonationRequestId).child("status");
        userRef.setValue(status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Status updated successfully
                        Toast.makeText(context, "Status updated: " + status, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to update status
                        Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

