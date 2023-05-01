package com.example.badgerconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

public class EditProfileActivity extends AppCompatActivity {
    private boolean editMode = false;
    private ImageView pfp;
    private EditText name_EditText, year_EditText, dob_EditText, major_EditText;
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
        EditText nameField = findViewById(R.id.name_field);
        EditText majorField = findViewById(R.id.major_field);
        EditText birthdateField = findViewById(R.id.birthdate_field);
        if(val){
            nameField.setEnabled(true);
            majorField.setEnabled(true);
            birthdateField.setEnabled(true);
        }
        else{
            nameField.setEnabled(false);
            majorField.setEnabled(false);
            birthdateField.setEnabled(false);
        }
    }

    /*
    reads user data from database and displays it TODO
     */
    public void setUserDataFromDatabase(){
        /* set image*/
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        UserInfo currUser = new UserInfo();
        DatabaseFunctions.downloadPFP(uid, pfp);
        pfp.setVisibility(View.VISIBLE);
        Log.i("","hello i am here");
        CompletableFuture<UserInfo> currUserData = DatabaseFunctions.readUserData(uid, currUser);
        currUserData.thenAccept(user -> {
            name_EditText.setText(user.getUsername());
            name_EditText.setVisibility(View.VISIBLE);
            major_EditText.setText(user.getMajor());
            major_EditText.setVisibility(View.VISIBLE);
            int userYear = user.getYear().getNumVal();
            String yearString;
            switch (userYear) {
                case 1:
                    yearString = "Freshman";
                    break;
                case 2:
                    yearString = "Sophomore";
                    break;
                case 3:
                    yearString = "Junior";
                    break;
                case 4:
                    yearString = "Senior";
                    break;
                default:
                    yearString = "uh oh";
                    break;
            }
            year_EditText.setText(userYear);
            year_EditText.setVisibility(View.VISIBLE);
//            dob_EditText.setText(user.getDateOfBirth());
//            year_EditText.setVisibility(View.VISIBLE);
        });
        // TODO
    }

    private void initializeUI() {
        pfp = findViewById(R.id.profile_picture);
        pfp.setVisibility(View.INVISIBLE);
        name_EditText = findViewById(R.id.name_field);
        name_EditText.setVisibility(View.INVISIBLE);
        major_EditText = findViewById(R.id.major_field);
        major_EditText.setVisibility(View.INVISIBLE);
        dob_EditText = findViewById(R.id.birthdate_field);
        dob_EditText.setVisibility(View.INVISIBLE);
        year_EditText = findViewById(R.id.year_field);
        year_EditText.setVisibility(View.INVISIBLE);
    }
}
