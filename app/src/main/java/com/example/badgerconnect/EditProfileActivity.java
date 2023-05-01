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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class EditProfileActivity extends AppCompatActivity {
    private boolean editMode = false;
    private ImageView pfp;
    private EditText name_EditText, dob_EditText, major_EditText;
    private EditText bio_EditText;
    private CheckBox mentor_CB, mentee_CB, studybuddy_CB;
    private Spinner year_Spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // initUI
        Log.i("","INIT UI");
        initializeUI();
        Log.i("","CALLING SET USER");
        /* set image*/
        setUserDataFromDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_edit_menu, menu);
        if(editMode){
            getSupportActionBar().setTitle("Editing Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            menu.findItem(R.id.menu_save).setVisible(true);
            menu.findItem(R.id.menu_cancel).setVisible(true);
            menu.findItem(R.id.menu_profile_edit).setVisible(false);
            menu.findItem(R.id.logout).setVisible(false);
        }else{
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
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.menu_save) {
            // create a dialog to confirm cancel action
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Apply changes?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // disable the text fields
                    enableUserFields(false);
                    // write changes to the user in the database TODO
                    // display the updated user data TODO
                    setUserDataFromDatabase();
                    editMode = false;
                    invalidateOptionsMenu();
                }
            });
            builder.setNegativeButton("No", null);
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
        } else if(id == R.id.logout){
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

    public void enableUserFields(boolean val){
        if(val){
            // editing
            name_EditText.setEnabled(true);
            major_EditText.setEnabled(true);
            dob_EditText.setEnabled(true);
            year_Spinner.setEnabled(true);
            bio_EditText.setEnabled(true);

            // TODO set colors to black
            name_EditText.setTextColor(Color.BLACK);
            major_EditText.setTextColor(Color.BLACK);
            dob_EditText.setTextColor(Color.BLACK);
            bio_EditText.setTextColor(Color.BLACK);
        }
        else{
            // no longer editing
            name_EditText.setEnabled(false);
            major_EditText.setEnabled(false);
            dob_EditText.setEnabled(false);
            year_Spinner.setEnabled(false);
            bio_EditText.setEnabled(false);
            // TODO set colors back to the original
            name_EditText.setTextColor(Color.LTGRAY);
            major_EditText.setTextColor(Color.LTGRAY);
            dob_EditText.setTextColor(Color.LTGRAY);
            bio_EditText.setTextColor(Color.LTGRAY);
        }
    }

    /*
    reads user data from database and displays it
     */
    public void setUserDataFromDatabase(){
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
            dob_EditText.setText(user.getDateOfBirth());
            dob_EditText.setVisibility(View.VISIBLE);
            if(user.getConnectionType().get("Mentor")){
                mentor_CB.setChecked(true);
                mentor_CB.setVisibility(View.VISIBLE);
                mentor_CB.setEnabled(false);
            }
            if(user.getConnectionType().get("Mentee")){
                mentee_CB.setChecked(true);
                mentee_CB.setVisibility(View.VISIBLE);
                mentee_CB.setEnabled(false);
            }
            if(user.getConnectionType().get("StudyBuddy")){
                studybuddy_CB.setVisibility(View.GONE);
                studybuddy_CB.setEnabled(false);
                // iterate over courses
                HashMap<String, String> userCourses = user.getStudyBuddyCourses();

            }else{
                studybuddy_CB.setChecked(false);
                studybuddy_CB.setVisibility(View.VISIBLE);
                studybuddy_CB.setEnabled(false);
            }
        });
        // TODO
    }

    private void initializeUI() {
        pfp = findViewById(R.id.profile_picture);
        pfp.setVisibility(View.INVISIBLE);
        bio_EditText = findViewById(R.id.bio_field);
        bio_EditText.setVisibility(View.INVISIBLE);
        name_EditText = findViewById(R.id.name_field);
        name_EditText.setVisibility(View.INVISIBLE);
        major_EditText = findViewById(R.id.major_field);
        major_EditText.setVisibility(View.INVISIBLE);
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
    }
}
