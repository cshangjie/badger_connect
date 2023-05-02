package com.example.badgerconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EditProfileActivity extends AppCompatActivity {
    private boolean editMode = false;
    private ImageView pfp;
    private EditText name_EditText, dob_EditText;
    private AutoCompleteTextView major_EditText;
    private EditText bio_EditText;
    private CheckBox mentor_CB, mentee_CB, studybuddy_CB;

    private CheckBox inperson_CB, virtual_CB;
    private Spinner year_Spinner;
    private LinearLayout autocompleteContainer;
    private String yearSelected;
    private Button addCourse, removeCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // initUI
        initializeUI();
        /* set image*/
        setUserDataFromDatabase();
        year_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_edit_menu, menu);
        if (editMode) {
            getSupportActionBar().setTitle("Editing Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            menu.findItem(R.id.menu_save).setVisible(true);
            menu.findItem(R.id.menu_cancel).setVisible(true);
            menu.findItem(R.id.menu_profile_edit).setVisible(false);
            menu.findItem(R.id.logout).setVisible(false);
        } else {
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            menu.findItem(R.id.menu_save).setVisible(false);
            menu.findItem(R.id.logout).setVisible(true);
            menu.findItem(R.id.menu_cancel).setVisible(false);
            menu.findItem(R.id.menu_profile_edit).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_profile_edit) {
            editMode = true;
            // enable text fields
            enableUserFields(true);
            // enable course edit buttons
            addCourse.setEnabled(true);
            addCourse.setVisibility(View.VISIBLE);
            removeCourse.setEnabled(true);
            removeCourse.setVisibility(View.VISIBLE);

            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.menu_save) {
            // TODO validity checks on courses
            /* gathering fields */
            // name
            String newName;
            if(name_EditText.getText().toString().trim().isEmpty() || name_EditText.getText().toString().trim().isBlank()){
                name_EditText.setError("Name cannot be empty.");
                return true;
            }else{
                newName = name_EditText.getText().toString().trim();
            }
            // bio
            String newBio = bio_EditText.getText().toString().trim();
            // major
            String newMajor;
            String[] majorList = getResources().getStringArray(R.array.bs_majors);
            Set<String> majorSet = new HashSet<>(Arrays.asList(majorList));
            if(majorSet.contains(major_EditText.getText().toString().trim())){
                newMajor = major_EditText.getText().toString().trim();

            }else{
                Log.i("MAJOR:", major_EditText.getText().toString().trim());
                major_EditText.setError("Invalid Major");
                major_EditText.requestFocus();
                return true;
            }
            // Note: year is handled by the spinner listener in onCreate

            // dob
            String newDOB;
            if(dob_EditText.getText().toString().trim().isEmpty() || dob_EditText.getText().toString().trim().isBlank()){
                dob_EditText.setError("Date of birth cannot be empty.");
                return true;
            }else{
                newDOB = dob_EditText.getText().toString().trim();
            }

            // meeting pref
            MeetingType newMeetingPref;
            if(inperson_CB.isChecked() && virtual_CB.isChecked()){
                newMeetingPref = MeetingType.BOTH;
            }else if(inperson_CB.isChecked()){
                newMeetingPref = MeetingType.IN_PERSON;
            }else if(virtual_CB.isChecked()){
                newMeetingPref = MeetingType.VIRTUAL;
            }else{
                Toast.makeText(this, "You need to have at least one meeting preference.", Toast.LENGTH_SHORT).show();
                return true;
            }
            /* Connection Types */
            List<String> connectionTypes = new ArrayList<>();
            // mentor
            if(mentor_CB.isChecked()){
                connectionTypes.add("Mentor");
            }
            // mentee
            if(mentee_CB.isChecked()){
                connectionTypes.add("Mentee");
            }
            // studybuddy
            int course_count = autocompleteContainer.getChildCount();

            if(!mentor_CB.isChecked() && !mentee_CB.isChecked() && (course_count == 0)){
                Toast.makeText(this, "You need to be a Mentor, Mentee or StudyBuddy.", Toast.LENGTH_SHORT).show();
                return true;
            }

            // studybuddy courses
            String[] userEntries = new String[course_count];;
            // get course list, convert to set, check that each entry is in the set
            String[] courseList = getResources().getStringArray(R.array.full_courses_array);
            Set<String> courseSet = new HashSet<>(Arrays.asList(courseList));

            if(course_count != 0){
                // set as study buddy
                connectionTypes.add("StudyBuddy");
                // add courses to a course list
                for (int i = 0; i < course_count; i++) {
                    View childView = autocompleteContainer.getChildAt(i);
                    if (childView instanceof AutoCompleteTextView) {
                        AutoCompleteTextView course = (AutoCompleteTextView) childView;
                        if(courseSet.contains(String.valueOf(course.getText()))){
                            userEntries[i] = String.valueOf(course.getText());
                        }
                        else{
                            View invalidView = autocompleteContainer.getChildAt(i);
                            if (invalidView instanceof AutoCompleteTextView) {
                                AutoCompleteTextView invalidTextView = (AutoCompleteTextView) invalidView;
                                invalidTextView.requestFocus();
                                invalidTextView.setError("Invalid course");
                                return true;
                            }
                        }
                    }
                }
            }

            /* reading input & validation compeleted */



            // create a dialog to confirm cancel action
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Apply changes?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // disable the text fields
                    enableUserFields(false);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String uid = currentUser.getUid();
                    String email = currentUser.getEmail();
                    // TODO sahas check studybuddy
                    DatabaseFunctions.updateUserData(uid, newName, email,
                            newMajor, newBio, Year.valueOf(yearSelected),
                            newMeetingPref, connectionTypes, Arrays.asList(userEntries),course_count, newDOB);
                    // display the updated user data
                    setUserDataFromDatabase();
                    editMode = false;
                    invalidateOptionsMenu();
                }
            });
            builder.setNegativeButton("Not yet", null);
            builder.create().show();
            return true;
        } else if (id == R.id.menu_cancel) {
            // create a dialog to confirm cancel action
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Discard changes?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // disable the text fields
                    enableUserFields(false);
                    // overwrite any local changes with data from db
                    setUserDataFromDatabase();
                    editMode = false;
                    invalidateOptionsMenu();
                }
            });
            builder.setNegativeButton("No", null);
            builder.create().show();
            return true;
        } else if (id == android.R.id.home) { // handle back button click
            onBackPressed();
            return true;
        } else if (id == R.id.logout) {
            // create a dialog to confirm sign out action
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to sign out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // sign out the user from Firebase
                    FirebaseAuth.getInstance().signOut();
                    // start the LoginActivity and clear the task stack
                    Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("No", null);
            builder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void enableUserFields(boolean val) {
        if (val) {
            // editing
            name_EditText.setEnabled(true);
            major_EditText.setEnabled(true);
            dob_EditText.setEnabled(true);
            year_Spinner.setEnabled(true);
            bio_EditText.setEnabled(true);
            inperson_CB.setEnabled(true);
            virtual_CB.setEnabled(true);
            mentor_CB.setEnabled(true);
            mentee_CB.setEnabled(true);

            // check if studybuddy container is filled
            int course_count = autocompleteContainer.getChildCount();
            Log.i("Course Number when enabling:", Integer.toString(course_count));
            if(course_count != 0){
                for (int i = 0; i < course_count; i++) {
                    View childView = autocompleteContainer.getChildAt(i);
                    if (childView instanceof AutoCompleteTextView) {
                        AutoCompleteTextView autocompleteTextView = (AutoCompleteTextView) childView;
                        autocompleteTextView.setEnabled(true);
                        autocompleteTextView.setTextColor(Color.BLACK);
                        autocompleteTextView.setBackgroundResource(R.drawable.custom_edit_text_cut);
                    }
                }
            }


            // TODO set colors to black
            name_EditText.setTextColor(Color.BLACK);
            major_EditText.setTextColor(Color.BLACK);
            dob_EditText.setTextColor(Color.BLACK);
            bio_EditText.setTextColor(Color.BLACK);
        } else {
            // no longer editing
            name_EditText.setEnabled(false);
            major_EditText.setEnabled(false);
            dob_EditText.setEnabled(false);
            year_Spinner.setEnabled(false);
            bio_EditText.setEnabled(false);
            inperson_CB.setEnabled(false);
            virtual_CB.setEnabled(false);
            mentor_CB.setEnabled(false);
            mentee_CB.setEnabled(false);

            int course_count = autocompleteContainer.getChildCount();
            Log.i("Course Number after disabling:", Integer.toString(course_count));

            if(course_count != 0){
                for (int i = 0; i < course_count; i++) {
                    View childView = autocompleteContainer.getChildAt(i);
                    if (childView instanceof AutoCompleteTextView) {
                        AutoCompleteTextView autocompleteTextView = (AutoCompleteTextView) childView;
                        autocompleteTextView.setEnabled(false);
                        autocompleteTextView.setTextColor(Color.DKGRAY);
                        autocompleteTextView.setBackgroundResource(R.drawable.custom_edit_text_cut);
                    }
                }
            }

            // TODO set colors back to the original
            name_EditText.setTextColor(Color.DKGRAY);
            major_EditText.setTextColor(Color.DKGRAY);
            dob_EditText.setTextColor(Color.DKGRAY);
            bio_EditText.setTextColor(Color.DKGRAY);
        }
    }

    /*
    reads user data from database and displays it
     */
    public void setUserDataFromDatabase() {
        /* set image*/
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        UserInfo currUser = new UserInfo();
        DatabaseFunctions.downloadPFP(uid, pfp);
        pfp.setVisibility(View.VISIBLE);

        CompletableFuture<UserInfo> currUserData = DatabaseFunctions.readUserData(uid, currUser);
        currUserData.thenAccept(user -> {
            bio_EditText.setText(user.getBio());
            bio_EditText.setVisibility(View.VISIBLE);
            name_EditText.setText(user.getUsername());
            name_EditText.setVisibility(View.VISIBLE);
            major_EditText.setText(user.getMajor());
            major_EditText.setVisibility(View.VISIBLE);

            switch(user.getYear()){
                case Freshman:
                    year_Spinner.setSelection(0);
                    break;
                case Sophomore:
                    year_Spinner.setSelection(1);
                    break;
                case Junior:
                    year_Spinner.setSelection(2);
                    break;
                case Senior:
                    year_Spinner.setSelection(3);
                    break;
            }
            dob_EditText.setText(user.getDateOfBirth());
            dob_EditText.setVisibility(View.VISIBLE);
            if (user.getConnectionType().get("Mentor")) {
                mentor_CB.setChecked(true);
                mentor_CB.setVisibility(View.VISIBLE);
                mentor_CB.setEnabled(false);
            }else{
                mentor_CB.setChecked(false);
                mentor_CB.setVisibility(View.VISIBLE);
                mentor_CB.setEnabled(false);
            }
            if (user.getConnectionType().get("Mentee")) {
                mentee_CB.setChecked(true);
                mentee_CB.setVisibility(View.VISIBLE);
                mentee_CB.setEnabled(false);
            }else{
                mentee_CB.setChecked(false);
                mentee_CB.setVisibility(View.VISIBLE);
                mentee_CB.setEnabled(false);
            }
            if (user.getConnectionType().get("StudyBuddy")) {
                studybuddy_CB.setVisibility(View.GONE);
                studybuddy_CB.setEnabled(false);
                // clear courses in course container
                autocompleteContainer.removeAllViews();
                // iterate over courses
                HashMap<String, String> userCourses = user.getStudyBuddyCourses();
                userCourses.values();
                for (String course : userCourses.values()) {
                    // populate courses as autocompletetextviews within the container and setting them to disabled
                    AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(EditProfileActivity.this);
                    String[] options = getResources().getStringArray(R.array.full_courses_array);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_dropdown_item_1line, options);
                    autoCompleteTextView.setAdapter(adapter);
                    autoCompleteTextView.setGravity(Gravity.CENTER); // center the text
                    autoCompleteTextView.setText(course);
                    autoCompleteTextView.setEnabled(false);
                    autoCompleteTextView.setTextColor(Color.DKGRAY);
                    autoCompleteTextView.setBackgroundResource(R.drawable.custom_edit_text_cut);
                    autoCompleteTextView.setVisibility(View.VISIBLE);
                    autocompleteContainer.addView(autoCompleteTextView);
                }
            } else { // user is not a study buddy so show no courses with a checkbox unchecked
                studybuddy_CB.setChecked(false);
                studybuddy_CB.setVisibility(View.VISIBLE);
                studybuddy_CB.setEnabled(false);
            }

            // find user meeting prefs
            MeetingType userMeetingPref = user.getMeetingType();
            switch (userMeetingPref) {
                case IN_PERSON:
                    inperson_CB.setChecked(true);
                    virtual_CB.setVisibility(View.VISIBLE);
                    inperson_CB.setVisibility(View.VISIBLE);
                    break;
                case VIRTUAL:
                    virtual_CB.setChecked(true);
                    virtual_CB.setVisibility(View.VISIBLE);
                    inperson_CB.setVisibility(View.VISIBLE);
                    break;
                case BOTH:
                    inperson_CB.setChecked(true);
                    virtual_CB.setChecked(true);
                    virtual_CB.setVisibility(View.VISIBLE);
                    inperson_CB.setVisibility(View.VISIBLE);
                    break;
            }


        });
    }

    private void initializeUI() {
        addCourse = findViewById(R.id.add_course_bttn);
        removeCourse = findViewById(R.id.remove_course_bttn);
        addCourse.setEnabled(false);
        removeCourse.setEnabled(false);


        autocompleteContainer = findViewById(R.id.course_container);
        pfp = findViewById(R.id.profile_picture);
        pfp.setVisibility(View.INVISIBLE);
        bio_EditText = findViewById(R.id.bio_field);
        bio_EditText.setVisibility(View.INVISIBLE);
        name_EditText = findViewById(R.id.name_field);
        name_EditText.setVisibility(View.INVISIBLE);
        major_EditText = findViewById(R.id.major_field);
        major_EditText.setVisibility(View.INVISIBLE);
        ArrayAdapter<CharSequence> majorAdapter = ArrayAdapter.createFromResource(this, R.array.bs_majors, android.R.layout.simple_dropdown_item_1line);
        major_EditText.setAdapter(majorAdapter);

        dob_EditText = findViewById(R.id.birthdate_field);
        dob_EditText.setVisibility(View.INVISIBLE);
        year_Spinner = findViewById(R.id.year_spinner);
        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this, R.array.years_array, R.layout.spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_Spinner.setAdapter(spinAdapter);
        year_Spinner.setEnabled(false);
        mentor_CB = findViewById(R.id.mentor_checkbox);
        mentor_CB.setEnabled(false);
        mentor_CB.setVisibility(View.INVISIBLE);
        mentee_CB = findViewById(R.id.mentee_checkbox);
        mentee_CB.setEnabled(false);
        mentee_CB.setVisibility(View.INVISIBLE);
        studybuddy_CB = findViewById(R.id.studybuddy_checkbox);
        studybuddy_CB.setEnabled(false);
        studybuddy_CB.setVisibility(View.INVISIBLE);

        inperson_CB = findViewById(R.id.in_person_cb);
        inperson_CB.setVisibility(View.INVISIBLE);
        inperson_CB.setEnabled(false);
        virtual_CB = findViewById(R.id.virtual_cb);
        virtual_CB.setVisibility(View.INVISIBLE);
        virtual_CB.setEnabled(false);
    }
}