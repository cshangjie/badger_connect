package com.example.badgerconnect;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;

    private Marker previousMarker;
    private Location previousLocation;
    private EditText editTextLatitude;
    private EditText editTextLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_maps);

        editTextLatitude = findViewById(R.id.editText);
        editTextLongitude = findViewById(R.id.editText2);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Location");

        // Check if user is already signed in
        if (mAuth.getCurrentUser() != null) {
            // User is signed in, display map
            setUpMap();
        } else {
            // User is not signed in, display login screen
            showSignInScreen();
        }
    }

    private void showSignInScreen() {
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
        finish();
        previousLocation = null;
    }

    private void setUpMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        // Set up a database reference for all users' locations
        DatabaseReference allUserLocationsReference = databaseReference;
        String uid = mAuth.getCurrentUser().getUid();
        allUserLocationsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear(); // Clear existing markers
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    try {
                        String databaseLatitudeString = userSnapshot.child("latitude").getValue().toString();
                        String databaseLongitudeString = userSnapshot.child("longitude").getValue().toString();
                        String name = userSnapshot.child("name").getValue().toString();
                        String major = userSnapshot.child("major").getValue().toString();



                        LatLng latLng = new LatLng(Double.parseDouble(databaseLatitudeString), Double.parseDouble(databaseLongitudeString));


                        if (userSnapshot.getKey().equals(uid)) { // Check if the user's ID matches the current user's ID
                            // Add a special marker to the map to indicate the user's location, such as changing the marker color or adding a custom icon
                            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
                        } else {
                            String description = userSnapshot.child("description").getValue(String.class);
                            String markerTitle = name + "-" + major;
                            if (description != null && !description.isEmpty()) {
                                markerTitle += "-" + description;
                            }
                            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(markerTitle));
                        }
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle().equals("You are here")) { // Check if the marker clicked is the user's location marker
                    // Show a pop-up dialog with an EditText for the user to input their meeting description
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle("Set meeting description");

                    // Set up the input
                    final EditText input = new EditText(MapsActivity.this);
                    builder.setView(input);

                    // Set up the OK button
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Save the meeting description to the user's database location
                            String description = input.getText().toString();
                            String uid = mAuth.getCurrentUser().getUid();
                            DatabaseReference userLocationReference = databaseReference.child(uid);
                            userLocationReference.child("description").setValue(description);
                        }
                    });

                    // Set up the Cancel button
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    // Show the dialog
                    builder.show();

                    return true;
                } else {
                    return false;
                }
            }
        });


        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    editTextLatitude.setText(Double.toString(location.getLatitude()));
                    editTextLongitude.setText(Double.toString(location.getLongitude()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (previousLocation == null || location.distanceTo(previousLocation) > MIN_DIST) {
                    // Get the current user's ID
                    String uid = mAuth.getCurrentUser().getUid();


                    // Set up a database reference for the user's location

                        DatabaseReference userLocationReference = databaseReference.child(uid);

                        userLocationReference.child("latitude").setValue(location.getLatitude());
                        userLocationReference.child("longitude").setValue(location.getLongitude());

                        previousLocation = location;

                }
            }


            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}








