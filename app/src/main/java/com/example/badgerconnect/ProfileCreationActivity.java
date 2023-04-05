package com.example.badgerconnect;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ProfileCreationActivity extends AppCompatActivity {

    private static final int MAX_COURSES = 6;
    private EditText nameTV, majorTV, coursesTV, meeting_typeTV;
    private Button addCourseFieldButton, removeCourseFieldButton, continueButton;
    private LinearLayout autocompleteContainer;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        initializeUI();
        mAuth = FirebaseAuth.getInstance();

        addCourseFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autocompleteContainer.getChildCount() < MAX_COURSES) {
                    AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(ProfileCreationActivity.this);
                    autoCompleteTextView.setHint("Enter Course Name");
                    autoCompleteTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    String[] options = getResources().getStringArray(R.array.CourseList);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ProfileCreationActivity.this, android.R.layout.simple_dropdown_item_1line, options);
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
            }
        });
        removeCourseFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int childCount = autocompleteContainer.getChildCount();
                if (childCount > 0) {
                    autocompleteContainer.removeViewAt(childCount - 1);
                    if (childCount == 2) {
                        removeCourseFieldButton.setEnabled(false);
                    }
                    addCourseFieldButton.setEnabled(true);
                }
            }
        });

        // On-click, need to verify that the user has at least entered in one course, and then continue
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<Courses> coursesSet = new HashSet<Courses>();
                for (int i = 0; i < autocompleteContainer.getChildCount(); i++) {
                    View child = autocompleteContainer.getChildAt(i);
                    if (child instanceof AutoCompleteTextView) {
                        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) child;
                        String enteredText = autoCompleteTextView.getText().toString();
                        if (!enteredText.isEmpty()) {
                            coursesSet.add(Courses.valueOf(enteredText));
                        }
                    }
                }
                if (coursesSet.size() < 1){
                    Toast.makeText(getApplicationContext(), "Please enter at least one course.", Toast.LENGTH_LONG).show();
                    return;
                }

                // create a list from the set and then update the user
                List<Courses> userCourses = new ArrayList<>(coursesSet);

            }
        });
    }

    private void initializeUI() {
//        nameTV = findViewById(R.id.name);
//        majorTV = findViewById(R.id.major);
//        coursesTV = findViewById(R.id.courses);
//        meeting_typeTV = findViewById(R.id.meeting_type);
        addCourseFieldButton = findViewById(R.id.addButton);
        removeCourseFieldButton = findViewById(R.id.removeButton);
        removeCourseFieldButton.setEnabled(false);
        autocompleteContainer = findViewById(R.id.autocomplete_container);
        continueButton = findViewById(R.id.continueButton);

        // make the starting course entry box
        AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(ProfileCreationActivity.this);
        autoCompleteTextView.setHint("Enter Course Name");
        autoCompleteTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        String[] options = getResources().getStringArray(R.array.CourseList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ProfileCreationActivity.this, android.R.layout.simple_dropdown_item_1line, options);
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
}
