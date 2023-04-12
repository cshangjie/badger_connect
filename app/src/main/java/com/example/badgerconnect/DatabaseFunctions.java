package com.example.badgerconnect;

import androidx.annotation.NonNull;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseFunctions{

    private static DatabaseReference mDatabase;
    private static StorageReference mStorage;

    //Class for testing other methods
    public static void sendMessage() {
        String name = "Sahas Gelli";
        String email = "sahasgelli@gmail.com";
        String userId = "000001";
        MeetingType meetingType1 = MeetingType.IN_PERSON;
        Major major1 = Major.COMPUTER_ENGINEERING;
        MeetingType meetingType2 = MeetingType.VIRTUAL;
        Major major2 = Major.ELECTRICAL_ENGINERING;
        List<Courses> courses = new ArrayList<>();
        courses.add(Courses.ECE_454);
        courses.add(Courses.ECE_755);
        writeNewUser(userId, name, email, major1, courses, meetingType1);
        //updateUser(userId, "", "", major2, courses, meetingType2);
        //readUserData(userId);
        //deleteUser(userId);
    }

    public void deleteMessage(View view) {
        String userId = "000001";
        deleteUser(userId);
    }

    /**
     * Writes a new user into the database and takes the necessary details
     *
     * @param userId is the UID of the user from the auth
     * @param name is the name of the user
     * @param email is the email of the user
     * @param major is the user's major
     * @param courses is a list of courses of the user
     * @param meetingType is the users preferred meeting type
     */
    public static void writeNewUser(String userId, String name, String email, Major major, List<Courses> courses, MeetingType meetingType) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        String key = userId;
        UserInfo user = new UserInfo(name, email, major, courses, meetingType);
        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user_info/" + key, userValues);

        mDatabase.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firebase", "Data updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("firebase", "Error getting data");
                    }
                });

    }

    /**
     * Updates the user information in the database. ALl the fields are optional and only specified fields will be updated
     *
     * @param userId is the UID of the user from the auth
     * @param name is the name of the user
     * @param email is the email of the user
     * @param major is the user's major
     * @param courses is a list of courses of the user
     * @param meetingType is the users preferred meeting type
     */
    public static void updateUser(String userId, String name, String email, Major major, List<Courses> courses, MeetingType meetingType) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        String key = userId;
        Map<String, Object> childUpdates = new HashMap<>();

        UserInfo user = new UserInfo(name, email, major, courses, meetingType);

        if(!user.getUsername().isEmpty()) {
            childUpdates.put("/user_info/" + key + "/username/", user.getUsername());
        }
        if(!user.getEmail().isEmpty()) {
            childUpdates.put("/user_info/" + key + "/email/", user.getEmail());
        }
        if(!user.getMajor().isEmpty()) {
            childUpdates.put("/user_info/" + key + "/major/", user.getMajor());
        }
        if(!user.getCourses().isEmpty()) {
            childUpdates.put("/user_info/" + key + "/courses/", user.getCourses());
        }
        if(!user.getMeetingType().isEmpty()) {
            childUpdates.put("/user_info/" + key + "/meeting type/", user.getMeetingType());
        }

        mDatabase.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)    {
                        Log.d("firebase", "Data updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("firebase", "Error getting data");
                    }
                });

    }

    /**
     * Reads the user data and can perform a function within the onComplete method
     *
     * @param userId the userId of the user we are searching for
     */
    public static void readUserData(String userId) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("user_info").child(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            afterRead(task);
                        }
                    }
                });
    }

    /**
     * A skeleton method for whatever function is called after the read
     *
     * @param task the user information
     */
    private static void afterRead(Task<DataSnapshot> task) {
        String name = String.valueOf(task.getResult().child("username").getValue());
        String email = String.valueOf(task.getResult().child("email").getValue());
        Major major = Major.valueOf(String.valueOf(task.getResult().child("major").getValue()));
        MeetingType meetingType = MeetingType.valueOf(String.valueOf(task.getResult().child("meeting type").getValue()));
        String[] list_courses = String.valueOf(task.getResult().child("courses").getValue()).split(",");
        List<Courses> courses = new ArrayList<>();
        for(String course : list_courses) {
            courses.add(Courses.valueOf(course));
        }
        UserInfo user = new UserInfo(name, email, major, courses, meetingType);
    }

    /**
     * Deletes the user from the database
     *
     * @param userId the userId of the user being deleted
     */
    public static void deleteUser(String userId) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("user_info").child(userId).removeValue();
    }

    /**
     * Uploads a picture to the database as a profile picture
     *
     * @param bitmap the bitmap of the image being uploaded
     */
    public static void uploadPFP(String userId, Bitmap bitmap) {
        // create refs
        mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference pfpRef = mStorage.child("images").child(userId+"/pfp.jpg");
        // bitmaps
        //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.monke);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] pfpByteStream = baos.toByteArray();
        // upload
        UploadTask uploadTask = pfpRef.putBytes(pfpByteStream);
        uploadTask.addOnFailureListener((exception) -> {
            // Handle unsuccessful uploads
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("ImageUpload", "Image successfully uploaded to Firebase.");
            }
        });
    }

    /**
     * Downloads the profile picture of the user from firebase
     *
     * @param imageView area to display the image downloaded
     */
    public static void downloadPFP(String userId, ImageView imageView) {
        mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference pfpRef = mStorage.child("images").child(userId+"/pfp.jpg");

        // get image view obj
        //final ImageView imageView = findViewById(R.id.monke);
        final long ONE_MEGABYTE = 1024 * 1024;
        final long FIVE_MEGABYTE = 5 * ONE_MEGABYTE;

        // download img into a byte stream
        pfpRef.getBytes(FIVE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.i("Error", "Image Download Failed.");
            }
        });
    }

    public List<UserInfo> algorithm() {

        mDatabase = FirebaseDatabase.getInstance().getReference("Data");

        Query findMajor = mDatabase.orderByChild("major").equalTo("COMPUTER_ENGINEERING");

        findMajor.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    postAlgorithm(task);
                }
            }
        });

        return null;

    }

    private void postAlgorithm(Task<DataSnapshot> task) {
        Log.d("Result", String.valueOf(task.getResult()));
    }
}

