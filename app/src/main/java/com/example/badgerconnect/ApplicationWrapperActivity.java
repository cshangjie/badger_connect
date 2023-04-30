package com.example.badgerconnect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.badgerconnect.Fragments.MapsFragment;
//import com.example.badgerconnect.Fragments.PendingRequests;
import com.example.badgerconnect.Fragments.PendingRequestsFragment;
import com.example.badgerconnect.Fragments.UsersFragment;
import com.example.badgerconnect.Model.User;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ApplicationWrapperActivity extends AppCompatActivity
        implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_wrapper);

        /////////////////////SIGN IN BYPASS- VIP//////////////////////
        auth=FirebaseAuth.getInstance();

        String email1 = "test1@gmail.com";
        String email2 = "cbfu@wisc.edu";
        String email3 = "cjsu@wisc.edu";
        String password = "000000";
        String password3 = "010289";

        //auth.signOut();
        //TODO remove upon integration
        auth.signInWithEmailAndPassword(email3, password3).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);
        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }


    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {

        switch (item.getItemId()) {
            case R.id.navigation_people:
                getSupportActionBar().setTitle("BadgerConnect");
                getSupportActionBar().setSubtitle("Connections");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, new UsersFragment())
                        .commit();
                return true;

            case R.id.navigation_home:
                getSupportActionBar().setTitle("BadgerConnect");
                getSupportActionBar().setSubtitle("Home");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, new HomepageFragment())
                        .commit();
                return true;
            case R.id.navigation_pending_requests:
                getSupportActionBar().setTitle("BadgerConnect");
                getSupportActionBar().setSubtitle("Pending Requests");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, new PendingRequestsFragment())
                        .commit();
                return true;

            case R.id.navigation_map:
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
                //getSupportFragmentManager().beginTransaction().add(R.id.flFragment, new MapsFragment(), "tag").commit();
                getSupportActionBar().setTitle("BadgerConnect");
                getSupportActionBar().setSubtitle("Map");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, new MapsFragment())
                        .commit();
                return true;
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);


        reference= FirebaseDatabase.getInstance().getReference("Data").child("Users");
        Query query= reference;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                User user = new User();
                for (DataSnapshot userinfo : datasnapshot.getChildren()) {

                    user = userinfo.getValue(User.class);
                    if (user.getUid().equals(firebaseUser.getUid())) {
                        if (!user.getProfile_pic().equals("default")) {

                            CircleImageView profileImageForMenu = findViewById(R.id.profile_image_icon);
                            TextView profile_username = findViewById(R.id.profile_username);
                            MenuItem profileImageMenuItem=menu.findItem(R.id.profile_image_menu);
                            MenuItem username_menu=menu.findItem(R.id.username_menu);
                            profile_username.setText(user.getName());

                            // Inflate the layout and set it as the action view for the menu item
                            View profileImageView = profileImageMenuItem.getActionView();
                            if (profileImageView != null) {
                                profileImageMenuItem.setActionView(profileImageView);
                            }

                            Glide.with(ApplicationWrapperActivity.this).load(user.getProfile_pic()).into(profileImageForMenu);

                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
        }
        return false;
    }
}
