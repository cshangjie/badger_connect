package com.example.badgerconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if user is logged in
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent;
        if (currentUser == null) {
            // No user is logged in, redirect to login activity
            intent = new Intent(this, LoginActivity.class);
        } else {
            // User is already logged in, continue with app launch
            // ...
            // TODO need to check if the user has fully registered or not before sending to homepage
            intent = new Intent(this, ApplicationWrapperActivity.class);
        }
        startActivity(intent);
    }
}