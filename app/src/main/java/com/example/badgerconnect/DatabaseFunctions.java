package com.example.badgerconnect;

import androidx.annotation.NonNull;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatabaseFunctions{

    private static DatabaseReference mDatabase;
    private static StorageReference mStorage;
    private static int batchCount = 0; // Counter to keep track of the batch number
    private static int batchSize = 50; // Batch size for retrieving data

    //Class for testing other methods
    public static void sendMessage() {
        String name = "Sahas Gelli";
        String email = "sahasgelli@gmail.com";
        String userId = "000001";
        String userId2 = "000002";
        String userId3 = "000003";
        String bio = "Hi I am Sahas";
        Year year = Year.Freshman;
        Year year2 = Year.Junior;
        MeetingType meetingType1 = MeetingType.IN_PERSON;
        String major1 = "COMPUTER_ENGINEERING";
        MeetingType meetingType2 = MeetingType.VIRTUAL;
        Major major2 = Major.ELECTRICAL_ENGINERING;
        List<String> courses = new ArrayList<>();
        courses.add("ECE 755");
        courses.add("ECE 454");
        List<String> courses2 = new ArrayList<>();
        courses2.add("ECE 353");
        courses2.add("ECE 454");
        List<String> courses3 = new ArrayList<>();
        courses3.add("ECE 353");
        courses3.add("ECE 553");
        List<String> connectionTypes = new ArrayList<>();
        connectionTypes.add("Mentee");
        connectionTypes.add("StudyBuddy");
        List<String> connectionTypes2 = new ArrayList<>();
        connectionTypes2.add("Mentee");
        connectionTypes2.add("Mentor");
        //writeNewUser(userId, name, email, major1, 2, courses, connectionTypes, bio, year);
        //writeNewUser(userId2, name, email, major1, 2, courses2, connectionTypes, bio, year);
        //writeNewUser(userId3, name, email, major1, 2, courses3, connectionTypes2, bio, year2);
        //updateUser(userId, "", "", major2, courses, meetingType2);
        //readUserData(userId);
        //deleteUser(userId);
        HashMap<String, Integer> result = new HashMap<>();
        List<String> result2 = new ArrayList<>();
        //algorithmMentor(userId2, result2);
        //uploadPFP("000003", BitmapFactory.decodeResource(this.getResources(), R.drawable.badger));
    }
//
//    public void deleteMessage(View view) {
//        String userId = "000001";
//        deleteUser(userId);
//    }

    /**
     * Writes a new user into the database and takes the necessary details
     *
     * @param userId is the UID of the user from the auth
     * @param username is the name of the user
     * @param email is the email of the user
     * @param major is the user's major
     * @param numCourses is the number of courses the user wants a study buddy in
     * @param studyBuddyCourses is a list of courses the user wants a study buddy in
     * @param connectionTypes is a list of types of connections the user is looking for
     * @param bio is a description that the user inputs to talk about themselves
     * @param year the school year of the user
     * @param meetingType is the form of meeting they prefer
     * @param dateOfBirth is the date of birth of the user
     */
    public static void writeNewUser(String userId, String username, String email,
                                    String major, int numCourses, List<String> studyBuddyCourses,
                                    List<String> connectionTypes, String bio, Year year,
                                    MeetingType meetingType, String dateOfBirth) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        String key = userId;
        UserInfo user = new UserInfo(username, email, major, numCourses, studyBuddyCourses, connectionTypes, bio, year, meetingType, dateOfBirth);
        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/UserData/" + key, userValues);

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
     * Writes a new user into the database and takes the necessary details
     *
     * @param userId is the UID of the user from the auth
     * @param username is the name of the user
     * @param email is the email of the user
     * @param major is the user's major
     * @param connectionTypes is a list of types of connections the user is looking for
     * @param bio is a description that the user inputs to talk about themselves
     * @param year the school year of the user
     * @param meetingType is the form of meeting they prefer
     * @param dateOfBirth is the date of birth of the user
     */
    public static void writeNewUser(String userId, String username, String email,
                                    String major, List<String> connectionTypes,
                                    String bio, Year year, MeetingType meetingType,
                                    String dateOfBirth) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        String key = userId;
        UserInfo user = new UserInfo(username, email, major, connectionTypes, bio, year, meetingType, dateOfBirth);
        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/UserData/" + key, userValues);

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
     * @param username is the name of the user
     * @param email is the email of the user
     * @param major is the user's major
     * @param numCourses is the number of courses the user wants a study buddy in
     * @param studyBuddyCourses is a list of courses the user wants a study buddy in
     * @param connectionTypes is a list of types of connections the user is looking for
     * @param bio is a description that the user inputs to talk about themselves
     * @param year the school year of the user
     * @param meetingType is the form of meeting they prefer
     * @param dateOfBirth is the date of birth of the user
     */
    public static void updateUserData(String userId, String username, String email,
                                  String major, String bio, Year year,
                                  MeetingType meetingType, List<String> connectionTypes,
                                      List<String> studyBuddyCourses, int numCourses, String dateOfBirth) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        String key = userId;
        Map<String, Object> childUpdates = new HashMap<>();

        UserInfo user = new UserInfo(username, email, major, numCourses, studyBuddyCourses, connectionTypes, bio, year, meetingType, dateOfBirth);

        if(!user.getUsername().isEmpty()) {
            childUpdates.put("/UserData/" + key + "/Username/", user.getUsername());
        }
        if(!user.getEmail().isEmpty()) {
            childUpdates.put("/UserData/" + key + "/Email/", user.getEmail());
        }
        if(!user.getMajor().isEmpty()) {
            childUpdates.put("/UserData/" + key + "/Major/", user.getMajor());
        }
        if(!user.getBio().isEmpty()) {
            childUpdates.put("/UserData/" + key + "/Bio/", user.getBio());
        }
        if(!user.getYear().toString().isEmpty()) {
            childUpdates.put("/UserData/" + key + "/Year/", user.getYear());
        }
        if(!user.getMeetingType().toString().isEmpty()) {
            childUpdates.put("/UserData/" + key + "/MeetingType/", user.getMeetingType());
        }
        if(!user.getDateOfBirth().isEmpty()) {
            childUpdates.put("/UserData/" + key + "/DateOfBirth/", user.getDateOfBirth());
        }

        childUpdates.put("/UserData/" + key + "/StudyBuddyCourses/", user.getStudyBuddyCourses());

        childUpdates.put("/UserData/" + key + "/ConnectionTypes/", user.getConnectionType());

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
     * @param user the user information returned
     */
    public static CompletableFuture<UserInfo> readUserData(String userId, UserInfo user) {
        CompletableFuture<UserInfo> future = new CompletableFuture<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("UserData").child(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            afterRead(task, user, future);
                        }
                    }
                });
        return future;
    }

    /**
     * A skeleton method for whatever function is called after the read
     *
     * @param task the user information
     */
    private static void afterRead(Task<DataSnapshot> task, UserInfo user, CompletableFuture<UserInfo> future) {
        String name = String.valueOf(task.getResult().child("Username").getValue());
        String email = String.valueOf(task.getResult().child("Email").getValue());
        String major = String.valueOf(task.getResult().child("Major").getValue());
        HashMap<String, String> studyBuddyCoursesHash = (HashMap<String, String>) task.getResult().child("StudyBuddyCourses").getValue(Object.class);
        List<String> studyBuddyCourses = null;
        if(studyBuddyCoursesHash != null) {
            studyBuddyCourses = new ArrayList<String>(studyBuddyCoursesHash.values());
        }
        HashMap<String, Boolean> connectionTypes = (HashMap<String, Boolean>) task.getResult().child("ConnectionTypes").getValue(Object.class);
        List<String> connectTypes = new ArrayList<>();
        for(String connectionType : connectionTypes.keySet()) {
            if(connectionTypes.get(connectionType)) {
                connectTypes.add(connectionType);
            }
        }
        String bio = String.valueOf(task.getResult().child("Bio").getValue());
        Integer yearInt = task.getResult().child("Year").getValue(Integer.class);
        Year year;
        if(yearInt == 1) {
            year = Year.Freshman;
        } else if(yearInt == 2) {
            year = Year.Sophomore;
        } else if(yearInt == 3) {
            year = Year.Junior;
        } else {
            year = Year.Senior;
        }
        MeetingType meetingType = task.getResult().child("MeetingType").getValue(MeetingType.class);
        String dateOfBirth = String.valueOf(task.getResult().child("DateOfBirth").getValue());
        user.setUserInformation(name, email, major, studyBuddyCourses.size(), studyBuddyCourses, connectTypes, bio, year, meetingType, dateOfBirth);
        future.complete(user);
    }

    /**
     * Deletes the user from the database
     *
     * @param userId the userId of the user being deleted
     */
    public static void deleteUser(String userId) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("UserData").child(userId).removeValue();
    }

    /**
     * Reject a user and they will not show up on recommended connections anymore
     *
     * @param userId of the current user
     * @param rejectedUserId of the user who is to be rejected
     */
    public static void addRejection(String userId, String rejectedUserId) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        String key = userId;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/UserData/" + key + "/RejectionList/" + rejectedUserId, true);
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
     * Undo the rejection of a user so they show up on recommended connections
     *
     * @param userId of the current user
     * @param rejectedUserId of the user to be un-rejected
     */
    public static void removeRejection(String userId, String rejectedUserId) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("UserData").child(userId).child("RejectionList").child(rejectedUserId).removeValue();
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
                Log.d("Firebase", uploadTask.getResult().toString());
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

    /**
     * Downloads the profile picture url of the user from firebase
     *
     * @param userId the userId of the pfp you want back
     */
    public static CompletableFuture<String> downloadPFPURL(String userId) {
        CompletableFuture<String> future = new CompletableFuture<>();
        mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference pfpRef = mStorage.child("images").child(userId+"/pfp.jpg");

        // Get the download URL
        pfpRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                future.complete(imageURL);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
                Log.i("Error", "Download URI failed");
            }
        });

        return future;
    }

    /**
     * Algorithm to find a Mentor
     *
     * @param userId the userId of the current user conducting the search
     */
    public static CompletableFuture<List<String>> algorithmMentor(String userId, List<String> results) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("UserData").child(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", "Found current user");
                            findMentor(task, results, future);
                        }
                    }
                });

        return future;
    }

    /**
     * Query to find other users who want to be a mentor with the same major in a higher year.
     *
     * @param task the data snapshot of the user info of the current user.
     */
    private static void findMentor(Task<DataSnapshot> task, List<String> results, CompletableFuture<List<String>> future) {

        mDatabase = FirebaseDatabase.getInstance().getReference("Data/UserData");

        Integer currentSchoolYear = task.getResult().child("Year").getValue(Integer.class);

        HashMap<String, Boolean> rejectList = (HashMap<String, Boolean>) task.getResult().child("RejectionList").getValue(Object.class);

        String major = String.valueOf(task.getResult().child("Major").getValue());

        Query findMentor = mDatabase.orderByChild("Major").equalTo(major);

        Log.d("pre", "got here");

        findMentor.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            public void onComplete(@NonNull Task<DataSnapshot> innerTask) {
                if (!innerTask.isSuccessful()) {
                    Log.e("firebase", "Error getting data", innerTask.getException());
                }
                else {
                    Log.d("firebase", "Looking for mentors");
                    DataSnapshot dataSnapshot = innerTask.getResult();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Retrieve the user data for each user
                            String userId = snapshot.getKey();
                            int schoolYear = snapshot.child("Year").getValue(Integer.class);
                            HashMap<String, Boolean> connectionType = (HashMap<String, Boolean>) snapshot.child("ConnectionTypes").getValue(Object.class);
                            // Perform additional filtering on the client side
                            if ((schoolYear > currentSchoolYear) && connectionType.get("Mentor")) {
                                if(rejectList != null){
                                    if(!rejectList.containsKey(userId)){
                                        Log.d("Users", userId);
                                        results.add(userId);
                                    }
                                }
                                else {
                                    // User has the same major and is in a higher school year
                                    // Handle the userId as needed (e.g. add to a list, display in UI, etc.)
                                    Log.d("Users", userId);
                                    results.add(userId);
                                }

                            }
                        }
                    }
                    future.complete(results);
                }
            }
        });
    }

    /**
     * Algorithm to find a Mentee
     *
     * @param userId the userId of the current user conducting the search
     */
    public static CompletableFuture<List<String>> algorithmMentee(String userId, List<String> results) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("UserData").child(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", "Found current user");
                            findMentee(task, results, future);
                        }
                    }
                });
        return future;
    }

    /**
     * Query to find other users who want to be a mentee with the same major in a lower year.
     *
     * @param task the data snapshot of the user info of the current user.
     */
    private static void findMentee(Task<DataSnapshot> task, List<String> results, CompletableFuture<List<String>> future) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data/UserData");

        Integer currentSchoolYear = task.getResult().child("Year").getValue(Integer.class);

        HashMap<String, Boolean> rejectList = (HashMap<String, Boolean>) task.getResult().child("RejectionList").getValue(Object.class);

        String major = String.valueOf(task.getResult().child("Major").getValue());

        Query findMentor = mDatabase.orderByChild("Major").equalTo(major);

        Log.d("pre", "got here");

        findMentor.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            public void onComplete(@NonNull Task<DataSnapshot> innerTask) {
                if (!innerTask.isSuccessful()) {
                    Log.e("firebase", "Error getting data", innerTask.getException());
                }
                else {
                    Log.d("firebase", "Looking for mentees");
                    DataSnapshot dataSnapshot = innerTask.getResult();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Retrieve the user data for each user
                            String userId = snapshot.getKey();
                            int schoolYear = snapshot.child("Year").getValue(Integer.class);
                            HashMap<String, Boolean> connectionType = (HashMap<String, Boolean>) snapshot.child("ConnectionTypes").getValue(Object.class);
                            // Perform additional filtering on the client side
                            if ((schoolYear < currentSchoolYear) && connectionType.get("Mentee")) {
                                // User has the same major and is in a higher school year
                                // Handle the userId as needed (e.g. add to a list, display in UI, etc.)
                                if(rejectList != null){
                                    if(!rejectList.containsKey(userId)){
                                        Log.d("Users", userId);
                                        results.add(userId);
                                    }
                                }
                                else {
                                    // User has the same major and is in a higher school year
                                    // Handle the userId as needed (e.g. add to a list, display in UI, etc.)
                                    Log.d("Users", userId);
                                    results.add(userId);
                                }
                            }
                        }
                    }
                    future.complete(results);
                }
            }
        });
    }

    /**
     * Algorithm to find a Study Buddy
     *
     * @param userId the userId of the current user conducting the search
     */
    public static CompletableFuture<HashMap<String, Integer>> algorithmStudyBuddy(String userId, HashMap<String, Integer> results) {
        CompletableFuture<HashMap<String, Integer>> future = new CompletableFuture<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("UserData").child(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", "Found current user");
                            findStudyBuddy(task, results, future);
                        }
                    }
                });
        return future;
    }

    /**
     * Query to find other users who want to be a study buddy with the same courses.
     *
     * @param task the data snapshot of the user info of the current user.
     */
    private static void findStudyBuddy(Task<DataSnapshot> task, HashMap<String, Integer> results, CompletableFuture<HashMap<String, Integer>> future) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data/UserData");

        HashMap<String, String> currCourses = (HashMap<String, String>) task.getResult().child("StudyBuddyCourses").getValue(Object.class);

        HashMap<String, Boolean> rejectList = (HashMap<String, Boolean>) task.getResult().child("RejectionList").getValue(Object.class);

        // Query to retrieve data in batches
        Query findStudyBuddy = mDatabase.orderByChild("ConnectionTypes/StudyBuddy")
                .equalTo(true);
                //.startAt(batchCount * batchSize); // Calculate starting point for the batch

        Log.d("pre", currCourses.get("Course1"));

        findStudyBuddy.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            public void onComplete(@NonNull Task<DataSnapshot> innerTask) {
                if (!innerTask.isSuccessful()) {
                    Log.e("firebase", "Error getting data", innerTask.getException());
                }
                else {
                    Log.d("firebase", "Looking for study buddies");
                    Log.d("firebase", String.valueOf(innerTask.getResult().getValue()));
                    DataSnapshot dataSnapshot = innerTask.getResult();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Retrieve the user data for each user
                            String userId = snapshot.getKey();
                            if(!userId.equals(task.getResult().getKey())) {
                                if(rejectList != null){
                                    if(!rejectList.containsKey(userId)){
                                        HashMap<String, String> courses = (HashMap<String, String>) snapshot.child("StudyBuddyCourses").getValue(Object.class);
                                        // Perform additional filtering on the client side
                                        int similarityRating = findSimilarityRating(currCourses, courses);
                                        results.put(userId, similarityRating);
                                    }
                                }
                                else {
                                    // User has the same major and is in a higher school year
                                    // Handle the userId as needed (e.g. add to a list, display in UI, etc.)
                                    HashMap<String, String> courses = (HashMap<String, String>) snapshot.child("StudyBuddyCourses").getValue(Object.class);
                                    // Perform additional filtering on the client side
                                    int similarityRating = findSimilarityRating(currCourses, courses);
                                    results.put(userId, similarityRating);
                                }

                            }
                        }
                        // Convert the HashMap to a List of Map.Entry objects
                        //List<Map.Entry<String, Integer>> entryList = new ArrayList<>(usersBySimilarity.entrySet());

                        // Sort the List in descending order based on similarity rating using a lambda expression
                        //entryList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

                        // Retrieve the sorted user IDs
                        //List<String> sortedUserIds = new ArrayList<>();
                        //for (Map.Entry<String, Integer> entry : entryList) {
                        //    sortedUserIds.add(entry.getKey());
                        //    Log.d("Users", entry.getKey());
                        //}

                    }
                    future.complete(results);
                }
            }
        });
    }

    /**
     * Reads the user data and returns whether the user is a study buddy or not.
     *
     * @param userId the userId of the user we are searching for
     */
    public static CompletableFuture<Boolean> readWhetherStudyBuddy(String userId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("UserData").child(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            HashMap<String, Boolean> connectionType = (HashMap<String, Boolean>) task.getResult().child("ConnectionTypes").getValue(Object.class);
                            future.complete(connectionType.get("StudyBuddy"));
                        }
                    }
                });
        return future;
    }

    /**
     * Reads the user data and returns whether the user is a mentor or not.
     *
     * @param userId the userId of the user we are searching for
     */
    public static CompletableFuture<Boolean> readWhetherMentor(String userId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("UserData").child(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            HashMap<String, Boolean> connectionType = (HashMap<String, Boolean>) task.getResult().child("ConnectionTypes").getValue(Object.class);
                            future.complete(connectionType.get("Mentor"));
                        }
                    }
                });
        return future;
    }

    /**
     * Reads the user data and returns whether the user is a mentee or not.
     *
     * @param userId the userId of the user we are searching for
     */
    public static CompletableFuture<Boolean> readWhetherMentee(String userId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("UserData").child(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            HashMap<String, Boolean> connectionType = (HashMap<String, Boolean>) task.getResult().child("ConnectionTypes").getValue(Object.class);
                            future.complete(connectionType.get("Mentee"));
                        }
                    }
                });
        return future;
    }

    private static int findSimilarityRating(HashMap<String, String> currCourses, HashMap<String, String> courses) {
        int similarityRating = 0;
        Set<String> currCoursesSet = new HashSet<>(currCourses.values());
        Set<String> coursesSet = new HashSet<>(courses.values());

        for (String currCourse : currCoursesSet) {
            if (coursesSet.contains(currCourse)) {
                similarityRating++;
            }
        }

        return similarityRating;
    }



}

