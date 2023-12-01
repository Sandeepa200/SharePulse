package com.example.sharepulse;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.sharepulse.databinding.ActivityMapsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_CODE = 101;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ImageView nearestLocation;
    private List<Marker> markerList; // List to store markers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        nearestLocation = findViewById(R.id.nearestIcon); // Initialize the nearestLocation ImageView

        if (isLocationPermissionGranted()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            requestLocationPermission();
        }

        // Set OnClickListener for nearestLocation ImageView
        nearestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the focusOnNearestMarker method when the icon is clicked
                focusOnNearestMarker(getCurrentLatLng());
            }
        });
    }

    // Retrieve the current LatLng from the map's camera position
    private LatLng getCurrentLatLng() {
        CameraPosition cameraPosition = mMap.getCameraPosition();
        return cameraPosition.target;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        markerList = new ArrayList<>(); // Initialize the marker list

        // Call the addMarker method for each location
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("donationCamps");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear previous markers from the list and map
                for (Marker marker : markerList) {
                    marker.remove();
                }
                markerList.clear();

                // Add markers for each location
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    double latitude = Double.parseDouble(snapshot.child("xcoordinate").getValue().toString());
                    double longitude = Double.parseDouble(snapshot.child("ycoordinate").getValue().toString());
                    String title = snapshot.child("name").getValue().toString();
                    Marker marker = addMarker(latitude, longitude, title, mMap);
                    markerList.add(marker); // Add the marker to the list
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database read error
                Toast.makeText(MapsActivity.this, "Failed to read database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // Retrieve the marker's position
                LatLng markerPosition = marker.getPosition();

                // Retrieve the corresponding details from the database or any other data source
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("donationCamps");
                databaseReference.orderByChild("xcoordinate").equalTo(String.valueOf(markerPosition.latitude)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Iterate over the children to find the matching ycoordinate
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            double ycoordinate = Double.parseDouble(snapshot.child("ycoordinate").getValue().toString());
                            if (ycoordinate == markerPosition.longitude) {
                                // Retrieve the details from the snapshot
                                String name = snapshot.child("name").getValue().toString();
                                String startTime = snapshot.child("startTime").getValue().toString();
                                String endTime = snapshot.child("endTime").getValue().toString();
                                String startDate = snapshot.child("startDate").getValue().toString();
                                String endDate = snapshot.child("endDate").getValue().toString();
                                String link = snapshot.child("link").getValue().toString();
                                String description = snapshot.child("description").getValue().toString();
                                String address = snapshot.child("address").getValue().toString();
                                String xcoordinate = snapshot.child("xcoordinate").getValue().toString();
                                String campID = snapshot.child("campId").getValue().toString();

                                // Create an intent to navigate to the separate screen
                                Intent intent = new Intent(MapsActivity.this, CenterName.class);
                                //String.valueOf(ycoordinate);
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
                                intent.putExtra("ycoordinate", String.valueOf(ycoordinate));
                                intent.putExtra("campID", campID);

                                // Start the separate screen activity
                                startActivity(intent);

                                // Exit the loop once the matching marker is found
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database read error
                        Toast.makeText(MapsActivity.this, "Failed to read database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                return false;
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if location service is enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);

                // Get the current location
                Criteria criteria = new Criteria();
                Location currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

                // Check if the current location is not null
                if (currentLocation != null) {
                    LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                } else {
                    // Register the location listener to get the updated location
                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));

                            // Remove the listener to prevent continuous updates
                            locationManager.removeUpdates(this);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {}

                        @Override
                        public void onProviderEnabled(String provider) {}

                        @Override
                        public void onProviderDisabled(String provider) {}
                    };

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }
        } else {
            // Display dialog to enable location service
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Location Service Disabled");
            dialogBuilder.setMessage("Please enable location services to use this feature.");
            dialogBuilder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Open settings to enable location service
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        }
    }

    // Add a marker to the map
    private Marker addMarker(double latitude, double longitude, String title, GoogleMap map) {
        LatLng location = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        return map.addMarker(markerOptions);
    }

    // Check if location permission is granted
    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // Request location permission
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
    }

    // Handle location permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Focus the map camera on the nearest marker
    private void focusOnNearestMarker(LatLng currentLatLng) {
        if (currentLatLng != null) {
            double minDistance = Double.MAX_VALUE;
            Marker nearestMarker = null;

            for (Marker marker : markerList) {
                LatLng markerLatLng = marker.getPosition();
                double distance = getDistance(currentLatLng, markerLatLng);

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestMarker = marker;
                }
            }

            if (nearestMarker != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(nearestMarker.getPosition()));
                nearestMarker.showInfoWindow();
            }
        }
    }

    // Calculate distance between two LatLng points
    private double getDistance(LatLng point1, LatLng point2) {
        double lat1 = point1.latitude;
        double lon1 = point1.longitude;
        double lat2 = point2.latitude;
        double lon2 = point2.longitude;

        double theta = lon1 - lon2;
        double distance = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.cos(Math.toRadians(theta));
        distance = Math.acos(distance);
        distance = Math.toDegrees(distance);
        distance = distance * 60 * 1.1515;

        return distance;
    }
}
