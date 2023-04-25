package com.example.badgerconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.badgerconnect.Fragments.UsersFragment;
import com.example.badgerconnect.Model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_wrapper);

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
   // ConnectionsFragment connectionsFragment = new ConnectionsFragment();
    //MainActivity_msg mainActivity_msg= new MainActivity_msg();
    UsersFragment usersFragment= new UsersFragment();

    HomepageFragment homepageFragment = new HomepageFragment();
    MapFragment mapFragment = new MapFragment();

    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {

        switch (item.getItemId()) {
            case R.id.navigation_people:
               //getSupportActionBar().setTitle("Messages & Connections");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, usersFragment)
                        .commit();
                return true;

            case R.id.navigation_home:
                getSupportActionBar().setTitle("BadgerConnect");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, homepageFragment)
                        .commit();
                return true;

            case R.id.navigation_map:
                getSupportActionBar().setTitle("Map");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, mapFragment)
                        .commit();
                return true;
        }
        return false;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.profile_menu, menu);
//        getSupportActionBar().setTitle("BadgerConnect");
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.menu_profile) {
//            // Handle profile icon click
//            Intent intent = new Intent(ApplicationWrapperActivity.this, EditProfileActivity.class);
//            startActivity(intent);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    //////////////////////////ADDED From MainActivity_msg /////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Data").child("Users");
        Query query= reference;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                // profile_image_menu= menu.findItem(R.id.profile_image_menu);


                User user = new User();
                for (DataSnapshot userinfo : datasnapshot.getChildren()) {

                    user = userinfo.getValue(User.class);
                    if (user.getUid().equals(firebaseUser.getUid())) {
                        if (user.getProfile_pic().equals("default")) {
                            ImageView profile_image = null; //not used..?
                            profile_image.setImageResource(R.mipmap.ic_launcher);
                        } else {

                            CircleImageView profileImageForMenu = findViewById(R.id.profile_image_icon);
                            TextView profile_username = findViewById(R.id.profile_username);
                            MenuItem profileImageMenuItem=menu.findItem(R.id.profile_image_menu);
                            MenuItem username_menu=menu.findItem(R.id.username_menu);
//                            username_menu.setTitle(user.getName());
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
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()){

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////
}
