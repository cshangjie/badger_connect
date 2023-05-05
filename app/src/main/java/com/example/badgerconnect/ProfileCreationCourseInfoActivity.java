package com.example.badgerconnect;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.badgerconnect.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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

        // retrieve extras
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String major = intent.getStringExtra("major");
        String year = intent.getStringExtra("year");
        String dob = intent.getStringExtra("dob");
        String bio = intent.getStringExtra("bio");
//        Bitmap imagePfp = (Bitmap) intent.getParcelableExtra("image_pfp");
        List<String> connectionTypes = getIntent().getStringArrayListExtra("connectionType");
        boolean physical = getIntent().getBooleanExtra("physical", false);
        boolean virtual = getIntent().getBooleanExtra("virtual", false);

        if (!physical && !virtual) {
            Toast.makeText(this, "Both In-Person or Virtual meeting preferences were unselected.", Toast.LENGTH_SHORT).show();
            finish(); // Finish the activity and return to the previous one
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
//            String[] userEntries = new String[course_count];
            Set<String> userEntries = new HashSet<String>();

            for (int i = 0; i < course_count; i++) {
                View childView = autocompleteContainer.getChildAt(i);
                if (childView instanceof AutoCompleteTextView) {
                    AutoCompleteTextView autocompleteTextView = (AutoCompleteTextView) childView;
                    String userText = autocompleteTextView.getText().toString();
                    // add user text to userEntries
                    userEntries.add(userText);
                }
            }
            // get course list, convert to set, check that each entry is in the set
            String[] courseList = getResources().getStringArray(R.array.full_courses_array);
            Set<String> courseSet = new HashSet<>(Arrays.asList(courseList));
            int i = 0;
            for (String course : userEntries) {
                if(!courseSet.contains(course)){
                    // Request focus on the corresponding AutoCompleteTextView
                    View invalidView = autocompleteContainer.getChildAt(i);
                    if (invalidView instanceof AutoCompleteTextView) {
                        AutoCompleteTextView invalidTextView = (AutoCompleteTextView) invalidView;
                        invalidTextView.requestFocus();
                        invalidTextView.setError("Invalid course");
                        return;
                    }
                }
                i++;
            }
            // user entries are in the set (aka valid)
            List<String> list = new ArrayList<>(userEntries);
            // create user in sahas DB
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String uid = currentUser.getUid();
            String email = currentUser.getEmail();

            MeetingType meetingType;
            if(physical && virtual){
                meetingType = MeetingType.BOTH;
            }else if(physical){
                meetingType = MeetingType.IN_PERSON;
            }else{
                meetingType = MeetingType.VIRTUAL;
            }

            DatabaseFunctions.writeNewUser(uid, name, email,major, i, list, connectionTypes, bio, Year.valueOf(year), meetingType, dob);
//            DatabaseFunctions.uploadPFP(uid, imagePfp);

            //create user for CJ
            CompletableFuture<String> downloadedURL = DatabaseFunctions.downloadPFPURL(uid);
            downloadedURL.thenAccept(url -> {
                User user = new User(uid, name, url, null);
                user.addUser();
//                Log.d("ImageURL", url);
            });


            Intent myIntent = new Intent(ProfileCreationCourseInfoActivity.this, ApplicationWrapperActivity.class);
            startActivity(myIntent);
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