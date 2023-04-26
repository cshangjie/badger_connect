package com.example.badgerconnect;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProfileCreationCourseInfoActivity extends AppCompatActivity {
    private String name, major, dob;
    private MeetingType userMeetingPreference = null;
    //declarations
    private Button addCourseFieldButton, removeCourseFieldButton, continueButton;
    private LinearLayout autocompleteContainer;
    private static final int MAX_COURSES = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_creation_courses);
        // Get the ActionBar instance
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Enable the back button
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initializeUI();
        // Retrieve the extras from the intent
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            // Retrieve the name, major, and date of birth from the extras
            name = extras.getString("name");
            major = extras.getString("major");
            dob = extras.getString("dob");
        }

        // Add Course Text Field On-Click Listener
        addCourseFieldButton.setOnClickListener(v -> {
            if (autocompleteContainer.getChildCount() < MAX_COURSES) {
                AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(ProfileCreationCourseInfoActivity.this);
                autoCompleteTextView.setHint("Enter Course ID");
                autoCompleteTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                String[] options = getResources().getStringArray(R.array.full_courses_array);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ProfileCreationCourseInfoActivity.this, android.R.layout.simple_dropdown_item_1line, options);
                autoCompleteTextView.setAdapter(adapter);
                autocompleteContainer.addView(autoCompleteTextView);
                if (autocompleteContainer.getChildCount() == MAX_COURSES) {
                    addCourseFieldButton.setEnabled(false);
                }
                if (autocompleteContainer.getChildCount() == 1) {
                    removeCourseFieldButton.setEnabled(false);
                } else {
                    removeCourseFieldButton.setEnabled(true);
                }
            }
        });

        // Remove Course Text Field On-Click Listener
        removeCourseFieldButton.setOnClickListener(v -> {
            int childCount = autocompleteContainer.getChildCount();
            if (childCount > 0) {
                autocompleteContainer.removeViewAt(childCount - 1);
                if (childCount == 2) {
                    removeCourseFieldButton.setEnabled(false);
                }
                addCourseFieldButton.setEnabled(true);
            }
        });

        // Continue Button On-Click Listener
        continueButton.setOnClickListener(v -> {
            // iterate over the autoCompleteContainer to add user entries
            int course_count = autocompleteContainer.getChildCount();
            String[] userEntries = new String[course_count];
            for(int i = 0; i < course_count; i++){
                View childView  = autocompleteContainer.getChildAt(i);
                if (childView instanceof AutoCompleteTextView) {
                    AutoCompleteTextView autocompleteTextView = (AutoCompleteTextView) childView;
                    String userText = autocompleteTextView.getText().toString();
                    // add user text to userEntries
                    userEntries[i] = userText;
                }
            }
            // get course list, convert to set, check that each entry is in the set
            String[] courseList = getResources().getStringArray(R.array.full_courses_array);
            Set<String> courseSet = new HashSet<>(Arrays.asList(courseList));

            for (int i = 0; i < userEntries.length; i++) {
//                Log.i("0", userEntries[i]);
//                Log.i("1", String.valueOf(courseSet.contains(userEntries[i])));
                if(!courseSet.contains(userEntries[i])){
                    // Request focus on the corresponding AutoCompleteTextView
                    View invalidView = autocompleteContainer.getChildAt(i);
                    if (invalidView instanceof AutoCompleteTextView) {
                        AutoCompleteTextView invalidTextView = (AutoCompleteTextView) invalidView;
                        invalidTextView.requestFocus();
                        invalidTextView.setError("Invalid course");
                        return;
                    }
                }
            }

            // user entries are in the set (aka valid)
            // TODO send to db

        });


    }


    private void initializeUI() {
        // Course buttons
        addCourseFieldButton = findViewById(R.id.addButton);
        removeCourseFieldButton = findViewById(R.id.removeButton);
        removeCourseFieldButton.setEnabled(false);
        continueButton = findViewById(R.id.continueButton);

        // course entry stuff
        autocompleteContainer = findViewById(R.id.autocomplete_container);

        // make the starting course entry box
        AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(ProfileCreationCourseInfoActivity.this);
        autoCompleteTextView.setHint("Enter Course ID");
        autoCompleteTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        String[] options = getResources().getStringArray(R.array.full_courses_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ProfileCreationCourseInfoActivity.this, android.R.layout.simple_dropdown_item_1line, options);
        autoCompleteTextView.setAdapter(adapter);
        autocompleteContainer.addView(autoCompleteTextView);
        if (autocompleteContainer.getChildCount() == MAX_COURSES) {
            addCourseFieldButton.setEnabled(false);
        }
        if (autocompleteContainer.getChildCount() == 1) {
            removeCourseFieldButton.setEnabled(false);
        } else {
            removeCourseFieldButton.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button press
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}