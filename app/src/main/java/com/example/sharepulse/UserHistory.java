package com.example.sharepulse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserHistory extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> dataList;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        databaseReference = FirebaseDatabase.getInstance().getReference("BloodDonationRequests");
        dataList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);

        listView = findViewById(R.id.history_listView);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Handle list item click here
                String listItem = arrayAdapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(UserHistory.this);
                builder.setTitle("List Item Details");
                builder.setMessage(listItem);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Handle OK button click
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            searchBloodDonationRequests(userId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchBloodDonationRequests(String userId) {
        Query query = databaseReference.orderByChild("userId").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();
                for (DataSnapshot parentSnapshot : dataSnapshot.getChildren()) {

                    // Check the status
                    String status = parentSnapshot.child("status").getValue(String.class);
                    if (status != null && status.equals("Approved")) {
                        // getting date and time
                        String currentDateAndTime = parentSnapshot.child("currentDateAndTime").getValue(String.class);

                        String donationRequestId = parentSnapshot.getKey(); // Get the DonationRequest ID (parent node)

                        //getting center code
                        String centerCode = parentSnapshot.child("centerCode").getValue(String.class);

                        getCenterName(centerCode, new CenterNameCallback() {
                            @Override
                            public void onCenterNameReceived(String centerName) {
                                // Separate date and time components
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                try {
                                    Date dateTime = sdf.parse(currentDateAndTime);

                                    SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    String date = dateSdf.format(dateTime);

                                    // Add the details to the dataList
                                    String listItem = " Date: " + date + "\nCenter: " + centerName + " \nRequest ID: " + donationRequestId;
                                    dataList.add(listItem);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                arrayAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCenterNameError(String errorMessage) {
                                Log.e("CenterName", errorMessage);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(UserHistory.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getCenterName(String centerCode, CenterNameCallback callback) {
        DatabaseReference campsRef = FirebaseDatabase.getInstance().getReference("donationCamps");
        Query query = campsRef.orderByChild("campId").equalTo(centerCode);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                    String parentKey = firstChild.getKey();
                    callback.onCenterNameReceived(parentKey);
                } else {
                    // No matching record found
                    callback.onCenterNameError("No matching record found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                callback.onCenterNameError("Failed to retrieve center name: " + databaseError.getMessage());
            }
        });
    }

    private void showUserDetailsPopup(String donationRequestId) {
        // Implement the logic to show the user details popup for the given donationRequestId
    }

    private interface CenterNameCallback {
        void onCenterNameReceived(String centerName);

        void onCenterNameError(String errorMessage);
    }
}
