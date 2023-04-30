package com.example.badgerconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ApplicationWrapperActivity extends AppCompatActivity
        implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

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
    ConnectionsFragment connectionsFragment = new ConnectionsFragment();
    HomepageFragment homepageFragment = new HomepageFragment();
    MapFragment mapFragment = new MapFragment();

    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {

        switch (item.getItemId()) {
            case R.id.navigation_people:
                getSupportActionBar().setTitle("Messages & Connections");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, connectionsFragment)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        getSupportActionBar().setTitle("BadgerConnect");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_profile) {
            // Handle profile icon click
            Intent intent = new Intent(ApplicationWrapperActivity.this, EditProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
