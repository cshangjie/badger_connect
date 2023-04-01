package com.example.badgerconnect;

import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileCreationActivity extends AppCompatActivity {

    private EditText nameTV, majorTV, coursesTV, meeting_typeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeUI();
    }

    private void initializeUI() {
        nameTV = findViewById(R.id.name);
        majorTV = findViewById(R.id.major);
        coursesTV = findViewById(R.id.courses);
        meeting_typeTV = findViewById(R.id.meeting_type);
    }
}
