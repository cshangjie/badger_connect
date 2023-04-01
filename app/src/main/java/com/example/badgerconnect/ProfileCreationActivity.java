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

import androidx.appcompat.app.AppCompatActivity;


public class ProfileCreationActivity extends AppCompatActivity {

    private static final int MAX_COURSES = 4;
    private EditText nameTV, majorTV, coursesTV, meeting_typeTV;
    private Button addCourseFieldButton, removeCourseFieldButton;
    private LinearLayout autocompleteContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        initializeUI();

        addCourseFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autocompleteContainer.getChildCount() < MAX_COURSES) {
                    AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(ProfileCreationActivity.this);
                    autoCompleteTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    String[] options = getResources().getStringArray(R.array.CourseList);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ProfileCreationActivity.this, android.R.layout.simple_dropdown_item_1line, options);
                    autoCompleteTextView.setAdapter(adapter);
                    autocompleteContainer.addView(autoCompleteTextView);
                    if (autocompleteContainer.getChildCount() == MAX_COURSES) {
                        addCourseFieldButton.setEnabled(false);
                    }
                    removeCourseFieldButton.setEnabled(true);
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
    }
}
