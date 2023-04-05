package com.example.badgerconnect;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class ProfileCreationGeneraInfoActivity extends AppCompatActivity {
    // declarations
    private EditText nameField;
    private AutoCompleteTextView majorField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation_general_info);

    }

    private void initializeUI() {

    }
}