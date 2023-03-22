package com.example.badgerconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if user is logged in

        //  TODO flow should be if not logged in -> send to a page with a login field + a sign-up button
        Intent i = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(i);
    }
}