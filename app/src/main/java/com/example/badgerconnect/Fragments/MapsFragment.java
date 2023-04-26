package com.example.badgerconnect.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.badgerconnect.MapsActivity;
import com.example.badgerconnect.R;
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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.badgerconnect.SignInActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        // Get references to UI elements
        editTextLatitude = view.findViewById(R.id.editText);
        editTextLongitude = view.findViewById(R.id.editText2);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Data/Map");

        // Check if user is already signed in
        if (mAuth.getCurrentUser() != null) {
            // User is signed in, display map
            setUpMap();
        } else {
            // User is not signed in, display login screen
            showSignInScreen();
        }

    return view;
    }

//    private void showSignInScreen() {
//        Intent signInIntent = new Intent(this, SignInActivity.class);
//        startActivity(signInIntent);
//        finish();
//        previousLocation = null;
//    }

    private void setUpMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentActivity fragmentActivity= new FragmentActivity();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentActivity.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Get the current user's ID
        String uid = mAuth.getCurrentUser().getUid();

        // Set up a database reference for the user's location
        DatabaseReference userLocationReference = databaseReference.child(uid);

        userLocationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                try {
                    String databaseLatitudeString = dataSnapshot.child("user_latitude").getValue().toString();
                    String databaseLongitudedeString = dataSnapshot.child("user_longitude").getValue().toString();

                    LatLng latLng = new LatLng(Double.parseDouble(databaseLatitudeString), Double.parseDouble(databaseLongitudedeString));

                    if (previousMarker != null) {
                        previousMarker.remove(); // remove previous marker
                    }
                    previousMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(databaseLatitudeString + " , " + databaseLongitudedeString));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String title;
        mMap = googleMap;
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference eventRef = databaseReference.child(uid).child("Events").push();


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // Show a pop-up dialog with EditTexts for the user to input the event details
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Set event details");

                // Set up the input fields
                final EditText titleInput = new EditText(MapsActivity.this);
                titleInput.setHint("Title");
                final EditText descriptionInput = new EditText(MapsActivity.this);
                descriptionInput.setHint("Description");
                final EditText timeInput = new EditText(MapsActivity.this);
                timeInput.setHint("Time");

                // Set up the view
                LinearLayout layout = new LinearLayout(MapsActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setBackgroundColor(Color.parseColor("#90CAF9"));
                layout.addView(titleInput);
                layout.addView(descriptionInput);
                layout.addView(timeInput);
                builder.setView(layout);

                // Set up the OK button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Save the event details and add a marker to the map
                        String title = titleInput.getText().toString();
                        String description = descriptionInput.getText().toString();
                        String time = timeInput.getText().toString();
                        String markerTitle = "Title: " + title + "\nDescription: " + description + "\nTime: " + time;
                        Map<String, Object> eventMap = new HashMap<>();
                        eventMap.put("title", title);
                        eventMap.put("description", description);
                        eventMap.put("time", time);
                        eventMap.put("latitude", latLng.latitude);
                        eventMap.put("longitude", latLng.longitude);

                        eventRef.setValue(eventMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Create the marker
                                            MarkerOptions markerOptions = new MarkerOptions()
                                                    .position(latLng)
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                            Marker marker = mMap.addMarker(markerOptions);

                                            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                                @Override
                                                public View getInfoWindow(Marker marker) {
                                                    return null;
                                                }

                                                @Override
                                                public View getInfoContents(Marker marker) {
                                                    // Inflate the layout for the InfoWindow
                                                    View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                                                    // Set the title, description, and time in the layout
                                                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView titleTextView = view.findViewById(R.id.event_title);
                                                    TextView descriptionTextView = view.findViewById(R.id.event_description);
                                                    TextView timeTextView = view.findViewById(R.id.event_time);


                                                    titleTextView.setText(title);
                                                    descriptionTextView.setText(description);
                                                    timeTextView.setText(time);

                                                    return view;
                                                }
                                            });

// Show the InfoWindow when the marker is clicked
                                            marker.showInfoWindow();

                                        }
                                    }
                                });
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

                    userLocationReference.child("user_latitude").setValue(location.getLatitude());
                    userLocationReference.child("user_longitude").setValue(location.getLongitude());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}