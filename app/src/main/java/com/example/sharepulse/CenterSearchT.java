package com.example.sharepulse;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CenterSearchT extends AppCompatActivity {
    SearchView searchView;
    ListView listView;

    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;

    DatabaseReference donationCampsRef; // Reference to "donationCamps" table in Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_search_t);

        searchView = findViewById(R.id.searchBar);
        listView = findViewById(R.id.centerList);

        String initialCenterName = getIntent().getStringExtra("CENTER_NAME");

        listView.setVisibility(View.GONE);

        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);

        listView.setAdapter(adapter);

        // Initialize the DatabaseReference for "donationCamps" table
        donationCampsRef = FirebaseDatabase.getInstance().getReference().child("donationCamps");

        // Fetch the parent node names from "donationCamps" table
        donationCampsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String campName = snapshot.getKey();
                    arrayList.add(campName);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(CenterSearchT.this, "Failed to retrieve data from database", Toast.LENGTH_SHORT).show();
            }
        });

        if (initialCenterName != null) {
            searchView.setQuery(initialCenterName, false);
        }

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String campName = arrayList.get(position);
            DatabaseReference campRef = donationCampsRef.child(campName);

            campRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Retrieve the details from the snapshot
                    String name = dataSnapshot.child("name").getValue().toString();
                    String startTime = dataSnapshot.child("startTime").getValue().toString();
                    String endTime = dataSnapshot.child("endTime").getValue().toString();
                    String startDate = dataSnapshot.child("startDate").getValue().toString();
                    String endDate = dataSnapshot.child("endDate").getValue().toString();
                    String link = dataSnapshot.child("link").getValue().toString();
                    String description = dataSnapshot.child("description").getValue().toString();
                    String address = dataSnapshot.child("address").getValue().toString();
                    String xcoordinate = dataSnapshot.child("xcoordinate").getValue().toString();
                    String ycoordinate = dataSnapshot.child("ycoordinate").getValue().toString();
                    String campID = dataSnapshot.child("campId").getValue().toString();

                    // Create an intent to navigate to the separate screen
                    Intent intent = new Intent(CenterSearchT.this, CenterName.class);

                    // Pass the details as extras to the intent
                    intent.putExtra("name", name);
                    intent.putExtra("startTime", startTime);
                    intent.putExtra("endTime", endTime);
                    intent.putExtra("startDate", startDate);
                    intent.putExtra("endDate", endDate);
                    intent.putExtra("link", link);
                    intent.putExtra("description", description);
                    intent.putExtra("address", address);
                    intent.putExtra("xcoordinate", xcoordinate);
                    intent.putExtra("ycoordinate", ycoordinate);
                    intent.putExtra("campID", campID);

                    // Start the separate screen activity
                    startActivity(intent);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle database error
                    Toast.makeText(CenterSearchT.this, "Failed to retrieve camp data from database", Toast.LENGTH_SHORT).show();
                }
            });
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                listView.setVisibility(View.VISIBLE);
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }
}
