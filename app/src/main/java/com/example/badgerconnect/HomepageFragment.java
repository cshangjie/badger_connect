package com.example.badgerconnect;

import static com.example.badgerconnect.DatabaseFunctions.addRejection;
import static com.example.badgerconnect.DatabaseFunctions.algorithmMentee;
import static com.example.badgerconnect.DatabaseFunctions.algorithmMentor;
import static com.example.badgerconnect.DatabaseFunctions.algorithmStudyBuddy;
import static com.example.badgerconnect.DatabaseFunctions.downloadPFP;
import static com.example.badgerconnect.DatabaseFunctions.readUserData;
import static com.example.badgerconnect.DatabaseFunctions.readWhetherMentee;
import static com.example.badgerconnect.DatabaseFunctions.readWhetherMentor;
import static com.example.badgerconnect.DatabaseFunctions.readWhetherStudyBuddy;

import com.example.badgerconnect.Model.Request;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HomepageFragment extends Fragment {

    ImageView box1Image, box2Image, box3Image, box4Image, box5Image, box6Image;
    TextView box1Text, box2Text, box3Text, box4Text, box5Text, box6Text;
    CardView box1Card, box2Card, box3Card, box4Card, box5Card, box6Card;
    TextView ratingNumber1, ratingNumber2, ratingNumber3, ratingNumber4, ratingNumber5, ratingNumber6;
    ImageView filterButton;
    TextView lookingForText;
    ArrayList<UserInfo> userInfos = new ArrayList<>(6);
    ArrayList<String> userIds = new ArrayList<>(6);
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout linearLayout;
    HashMap<String, String> sortMap = new HashMap<>() {{
        put("ConnectionType", "none");
    }};
    HashMap<String, String> prevOp = new HashMap<String, String>() {{
        put("ConnectionType", "none");
    }};
    List<String> foundUsers = new ArrayList<>();
    HashMap<String, Integer> foundUsersStudyBuddy= new HashMap<>();

    //TODO:Add actual firebase userId
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String currUserId = firebaseUser.getUid();
    Request request = new Request();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        initializeUI(view);

        return view;
    }

    /**
     * Sets the onClickListeners for the interactable components of the page
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        filterButton.setOnClickListener(this::pullFilterDialog);
        box1Card.setOnClickListener(this::pullUserDataBox1);
        box2Card.setOnClickListener(this::pullUserDataBox2);
        box3Card.setOnClickListener(this::pullUserDataBox3);
        box4Card.setOnClickListener(this::pullUserDataBox4);
        box5Card.setOnClickListener(this::pullUserDataBox5);
        box6Card.setOnClickListener(this::pullUserDataBox6);
    }

    /**
     * When the page is pulled down and refreshed this function updates the users shown
     */
    private void onRefresh() {
        // Call your function here
        // This function will be triggered when the user pulls down on the screen
        // to refresh the content
        //TODO: Set the sortMap with the user selections
        setBoxesInvisible();
        setRatingInvisible();
        userIds.clear();
        userInfos.clear();
        if((sortMap.get("ConnectionType").equals("Mentor")) && (!prevOp.get("ConnectionType").equals("Mentor"))) {
            lookingForText.setText("Looking for Mentors");
            CompletableFuture<List<String>> futureUsers = algorithmMentor(currUserId, foundUsers);
            futureUsers.thenAccept(users -> {
                populateSquares(users);
            });
            linearLayout.setVisibility(View.GONE);
        }
        else if((sortMap.get("ConnectionType").equals("Mentee")) && (!prevOp.get("ConnectionType").equals("Mentee"))) {
            lookingForText.setText("Looking for Mentees");
            CompletableFuture<List<String>> futureUsers = algorithmMentee(currUserId, foundUsers);
            futureUsers.thenAccept(users -> {
                populateSquares(users);
            });
            linearLayout.setVisibility(View.GONE);
        }
        else if((sortMap.get("ConnectionType").equals("StudyBuddy")) && (!prevOp.get("ConnectionType").equals("StudyBuddy"))) {
            lookingForText.setText("Looking for Study Buddies");
            CompletableFuture<HashMap<String, Integer>> futureUsers = algorithmStudyBuddy(currUserId, foundUsersStudyBuddy);
            futureUsers.thenAccept(users -> {
                populateSquaresStudyBuddy(users);
            });
            linearLayout.setVisibility(View.GONE);
        }
        else if((sortMap.get("ConnectionType").equals("Mentor")) && (prevOp.get("ConnectionType").equals("Mentor"))) {
            lookingForText.setText("Looking for Mentors");
            populateSquares(foundUsers);
            linearLayout.setVisibility(View.GONE);
        }
        else if((sortMap.get("ConnectionType").equals("Mentee")) && (prevOp.get("ConnectionType").equals("Mentee"))) {
            lookingForText.setText("Looking for Mentees");
            populateSquares(foundUsers);
            linearLayout.setVisibility(View.GONE);
        }
        else if((sortMap.get("ConnectionType").equals("StudyBuddy")) && (prevOp.get("ConnectionType").equals("StudyBuddy"))) {
            lookingForText.setText("Looking for Study Buddies");
            populateSquaresStudyBuddy(foundUsersStudyBuddy);
            linearLayout.setVisibility(View.GONE);
        }
        else {
            Toast.makeText(requireContext(), "Please select a type of connection to search for", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
        prevOp.put("ConnectionType", sortMap.get("ConnectionType"));
    }


    /**
     * Initializes the UI components
     *
     * @param view the current view of the fragment
     */
    private void initializeUI(View view) {
        box1Image = view.findViewById(R.id.profile_picture1);
        box2Image = view.findViewById(R.id.profile_picture2);
        box3Image = view.findViewById(R.id.profile_picture3);
        box4Image = view.findViewById(R.id.profile_picture4);
        box5Image = view.findViewById(R.id.profile_picture5);
        box6Image = view.findViewById(R.id.profile_picture6);
        box1Text = view.findViewById(R.id.name1);
        box2Text = view.findViewById(R.id.name2);
        box3Text = view.findViewById(R.id.name3);
        box4Text = view.findViewById(R.id.name4);
        box5Text = view.findViewById(R.id.name5);
        box6Text = view.findViewById(R.id.name6);
        box1Card = view.findViewById(R.id.card1);
        box2Card = view.findViewById(R.id.card2);
        box3Card = view.findViewById(R.id.card3);
        box4Card = view.findViewById(R.id.card4);
        box5Card = view.findViewById(R.id.card5);
        box6Card = view.findViewById(R.id.card6);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        filterButton = view.findViewById(R.id.filter_button);
        linearLayout = view.findViewById(R.id.linearLayout);
        lookingForText = view.findViewById(R.id.looking_for);
        ratingNumber1 = view.findViewById(R.id.rating_number1);
        ratingNumber2 = view.findViewById(R.id.rating_number2);
        ratingNumber3 = view.findViewById(R.id.rating_number3);
        ratingNumber4 = view.findViewById(R.id.rating_number4);
        ratingNumber5 = view.findViewById(R.id.rating_number5);
        ratingNumber6 = view.findViewById(R.id.rating_number6);
    }

    /**
     * Populates the boxes on screen with user information and their profile picture
     *
     * @param foundUsers a hashmap of found users
     */
    private void populateSquaresStudyBuddy(HashMap<String, Integer> foundUsers) {
        List<String> users = new ArrayList<>();
        users.addAll(foundUsers.keySet());
        Collections.shuffle(users);
        ImageView[] imageViews = {box1Image, box2Image, box3Image, box4Image, box5Image, box6Image};
        TextView[] textViews = {box1Text, box2Text, box3Text, box4Text, box5Text, box6Text};
        CardView[] cardViews = {box1Card, box2Card, box3Card, box4Card, box5Card, box6Card};
        TextView[] ratingViews = {ratingNumber1, ratingNumber2, ratingNumber3, ratingNumber4, ratingNumber5, ratingNumber6};
        int size = (users.size() < 6) ? users.size() : 6;
        for(int i = 0 ; i < size ; i++) {
            UserInfo currUser = new UserInfo();
            String currUserId = users.get(i);
            CompletableFuture<UserInfo> currUserData = readUserData(currUserId, currUser);
            int currIndex = i;
            TextView currTextView = textViews[i];
            ImageView currImageView = imageViews[i];
            CardView currCardView = cardViews[i];
            userIds.add(i, currUserId);
            currUserData.thenAccept(user -> {
                userInfos.add(currIndex, user);
                currCardView.setVisibility(View.VISIBLE);
                currTextView.setText(user.getUsername());
                downloadPFP(currUserId, currImageView);
            });
            ratingViews[i].setVisibility(View.VISIBLE);
            ratingViews[i].setText("Course Similarity: "+String.valueOf(foundUsers.get(currUserId)));
        }
        swipeRefreshLayout.setRefreshing(false);

    }

    /**
     * Populates the boxes on screen with user information and their profile picture
     *
     * @param foundUsers a list of found users
     */
    private void populateSquares(List<String> foundUsers) {
        Collections.shuffle(foundUsers);
        ImageView[] imageViews = {box1Image, box2Image, box3Image, box4Image, box5Image, box6Image};
        TextView[] textViews = {box1Text, box2Text, box3Text, box4Text, box5Text, box6Text};
        CardView[] cardViews = {box1Card, box2Card, box3Card, box4Card, box5Card, box6Card};
        int size = (foundUsers.size() < 6) ? foundUsers.size() : 6;
        for(int i = 0 ; i < size ; i++) {
            UserInfo currUser = new UserInfo();
            String currUserId = foundUsers.get(i);
            int currIndex = i;
            TextView currTextView = textViews[i];
            ImageView currImageView = imageViews[i];
            CardView currCardView = cardViews[i];
            CompletableFuture<UserInfo> currUserData = readUserData(currUserId, currUser);
            userIds.add(i, currUserId);
            currUserData.thenAccept(user -> {
                userInfos.add(currIndex, user);
                currCardView.setVisibility(View.VISIBLE);
                currTextView.setText(user.getUsername());
                downloadPFP(currUserId, currImageView);
            });
        }
        swipeRefreshLayout.setRefreshing(false);

    }

    /**
     * Pulls up a dialog box to display more user information and give
     * the user the ability to connect with them
     *
     * @param v the current view
     */
    private void pullUserDataBox1(View v) {
        if(userInfos.size() >= 1) {
            // Create an AlertDialog.Builder to display the dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("User Info");

            // Inflate the custom layout for the dialog box
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_user_info, null);
            builder.setView(dialogView);

            // Get references to the views in the dialog layout
            ImageView profilePictureImageView = dialogView.findViewById(R.id.user_profile_picture);
            TextView userNameTextView = dialogView.findViewById(R.id.name_field);
            TextView userMajorTextView = dialogView.findViewById(R.id.major_field);
            TextView userYearTextView = dialogView.findViewById(R.id.year_field);
            LinearLayout studyBuddyCoursesView = dialogView.findViewById(R.id.course_container);
            // TextView userBirthdateTextView = dialogView.findViewById(R.id.birthdate_field);

            Button closeButton = dialogView.findViewById(R.id.close_button);
            Button connectButton = dialogView.findViewById(R.id.connect_button);

            // Set the views' content based on the selected user
            // profilePictureImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.badger));
            downloadPFP(userIds.get(0), profilePictureImageView);
            userNameTextView.setText(userInfos.get(0).getUsername());
            userMajorTextView.setText(userInfos.get(0).getMajor());
            userYearTextView.setText(String.valueOf(userInfos.get(0).getYear()));
            // userBirthdateTextView.setText(userInfos.get(0).getDateOfBirth());
            Log.i("Birthday",userInfos.get(0).getDateOfBirth());
            HashMap<String, String> userCourses = userInfos.get(0).getStudyBuddyCourses();
            for (String course : userCourses.values()) {
                // populate courses as autocompletetextviews within the container and setting them to disabled
                TextView autoCompleteTextView = new TextView(requireContext());
                autoCompleteTextView.setGravity(Gravity.CENTER); // center the text
                autoCompleteTextView.setText(course);
                autoCompleteTextView.setEnabled(false);
                autoCompleteTextView.setTextSize(13);
                autoCompleteTextView.setTextColor(Color.DKGRAY);
                autoCompleteTextView.setBackgroundResource(R.drawable.custom_edit_text_cut);
                autoCompleteTextView.setVisibility(View.VISIBLE);
                studyBuddyCoursesView.addView(autoCompleteTextView);
            }

            // Create the dialog and show it
            AlertDialog dialog = builder.create();
            dialog.show();

            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    request.SendRequest(currUserId, userIds.get(0));
                    addRejection(currUserId, userIds.get(0));
                    Toast.makeText(requireContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            // Set an OnClickListener for the close button to dismiss the dialog box
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(requireContext(), "No User In This Box", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Pulls up a dialog box to display more user information and give
     * the user the ability to connect with them
     *
     * @param view the current view
     */
    private void pullUserDataBox2(View view) {
        if(userInfos.size() >= 2) {
            // Create an AlertDialog.Builder to display the dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("User Info");

            // Inflate the custom layout for the dialog box
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_user_info, null);
            builder.setView(dialogView);

            // Get references to the views in the dialog layout
            ImageView profilePictureImageView = dialogView.findViewById(R.id.user_profile_picture);
            TextView userNameTextView = dialogView.findViewById(R.id.name_field);
            TextView userMajorTextView = dialogView.findViewById(R.id.major_field);
            TextView userYearTextView = dialogView.findViewById(R.id.year_field);
            LinearLayout studyBuddyCoursesView = dialogView.findViewById(R.id.course_container);
            // TextView userBirthdateTextView = dialogView.findViewById(R.id.birthdate_field);

            Button closeButton = dialogView.findViewById(R.id.close_button);
            Button connectButton = dialogView.findViewById(R.id.connect_button);

            // Set the views' content based on the selected user
            //profilePictureImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.badger));
            downloadPFP(userIds.get(1), profilePictureImageView);
            userNameTextView.setText(userInfos.get(1).getUsername());
            userMajorTextView.setText(userInfos.get(1).getMajor());
            userYearTextView.setText(String.valueOf(userInfos.get(1).getYear()));
            // userBirthdateTextView.setText(userInfos.get(1).getDateOfBirth());
            HashMap<String, String> userCourses = userInfos.get(1).getStudyBuddyCourses();
            for (String course : userCourses.values()) {
                // populate courses as autocompletetextviews within the container and setting them to disabled
                TextView autoCompleteTextView = new TextView(requireContext());
                autoCompleteTextView.setGravity(Gravity.CENTER); // center the text
                autoCompleteTextView.setText(course);
                autoCompleteTextView.setEnabled(false);
                autoCompleteTextView.setTextSize(13);
                autoCompleteTextView.setTextColor(Color.DKGRAY);
                autoCompleteTextView.setBackgroundResource(R.drawable.custom_edit_text_cut);
                autoCompleteTextView.setVisibility(View.VISIBLE);
                studyBuddyCoursesView.addView(autoCompleteTextView);
            }
            // Create the dialog and show it
            AlertDialog dialog = builder.create();
            dialog.show();

            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    request.SendRequest(currUserId, userIds.get(1));
                    addRejection(currUserId, userIds.get(1));
                    Toast.makeText(requireContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            // Set an OnClickListener for the close button to dismiss the dialog box
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(requireContext(), "No User In This Box", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Pulls up a dialog box to display more user information and give
     * the user the ability to connect with them
     *
     * @param view the current view
     */
    private void pullUserDataBox3(View view) {
        if(userInfos.size() >= 3) {
            // Create an AlertDialog.Builder to display the dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("User Info");

            // Inflate the custom layout for the dialog box
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_user_info, null);
            builder.setView(dialogView);

            // Get references to the views in the dialog layout
            ImageView profilePictureImageView = dialogView.findViewById(R.id.user_profile_picture);
            TextView userNameTextView = dialogView.findViewById(R.id.name_field);
            TextView userMajorTextView = dialogView.findViewById(R.id.major_field);
            TextView userYearTextView = dialogView.findViewById(R.id.year_field);
            LinearLayout studyBuddyCoursesView = dialogView.findViewById(R.id.course_container);

            // TextView userBirthdateTextView = dialogView.findViewById(R.id.birthdate_field);

            Button closeButton = dialogView.findViewById(R.id.close_button);
            Button connectButton = dialogView.findViewById(R.id.connect_button);

            // Set the views' content based on the selected user
            //profilePictureImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.badger));
            downloadPFP(userIds.get(2), profilePictureImageView);
            userNameTextView.setText(userInfos.get(2).getUsername());
            userMajorTextView.setText(userInfos.get(2).getMajor());
            userYearTextView.setText(String.valueOf(userInfos.get(2).getYear()));
            // userBirthdateTextView.setText(userInfos.get(2).getDateOfBirth());
            HashMap<String, String> userCourses = userInfos.get(2).getStudyBuddyCourses();
            for (String course : userCourses.values()) {
                // populate courses as autocompletetextviews within the container and setting them to disabled
                TextView autoCompleteTextView = new TextView(requireContext());
                autoCompleteTextView.setGravity(Gravity.CENTER); // center the text
                autoCompleteTextView.setText(course);
                autoCompleteTextView.setTextSize(13);
                autoCompleteTextView.setEnabled(false);
                autoCompleteTextView.setTextColor(Color.DKGRAY);
                autoCompleteTextView.setBackgroundResource(R.drawable.custom_edit_text_cut);
                autoCompleteTextView.setVisibility(View.VISIBLE);
                studyBuddyCoursesView.addView(autoCompleteTextView);
            }
            // Create the dialog and show it
            AlertDialog dialog = builder.create();
            dialog.show();

            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    request.SendRequest(currUserId, userIds.get(2));
                    addRejection(currUserId, userIds.get(2));
                    Toast.makeText(requireContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            // Set an OnClickListener for the close button to dismiss the dialog box
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(requireContext(), "No User In This Box", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Pulls up a dialog box to display more user information and give
     * the user the ability to connect with them
     *
     * @param view the current view
     */
    private void pullUserDataBox4(View view) {
        if(userInfos.size() >= 4) {
            // Create an AlertDialog.Builder to display the dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("User Info");

            // Inflate the custom layout for the dialog box
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_user_info, null);
            builder.setView(dialogView);

            // Get references to the views in the dialog layout
            ImageView profilePictureImageView = dialogView.findViewById(R.id.user_profile_picture);
            TextView userNameTextView = dialogView.findViewById(R.id.name_field);
            TextView userMajorTextView = dialogView.findViewById(R.id.major_field);
            TextView userYearTextView = dialogView.findViewById(R.id.year_field);
            LinearLayout studyBuddyCoursesView = dialogView.findViewById(R.id.course_container);
            // TextView userBirthdateTextView = dialogView.findViewById(R.id.birthdate_field);

            Button closeButton = dialogView.findViewById(R.id.close_button);
            Button connectButton = dialogView.findViewById(R.id.connect_button);

            // Set the views' content based on the selected user
            //profilePictureImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.badger));
            downloadPFP(userIds.get(3), profilePictureImageView);
            userNameTextView.setText(userInfos.get(3).getUsername());
            userMajorTextView.setText(userInfos.get(3).getMajor());
            userYearTextView.setText(String.valueOf(userInfos.get(3).getYear()));
            // userBirthdateTextView.setText(userInfos.get(3).getDateOfBirth());
            HashMap<String, String> userCourses = userInfos.get(3).getStudyBuddyCourses();
            for (String course : userCourses.values()) {
                // populate courses as autocompletetextviews within the container and setting them to disabled
                TextView autoCompleteTextView = new TextView(requireContext());
                autoCompleteTextView.setGravity(Gravity.CENTER); // center the text
                autoCompleteTextView.setText(course);
                autoCompleteTextView.setEnabled(false);
                autoCompleteTextView.setTextSize(13);
                autoCompleteTextView.setTextColor(Color.DKGRAY);
                autoCompleteTextView.setBackgroundResource(R.drawable.custom_edit_text_cut);
                autoCompleteTextView.setVisibility(View.VISIBLE);
                studyBuddyCoursesView.addView(autoCompleteTextView);
            }

            // Create the dialog and show it
            AlertDialog dialog = builder.create();
            dialog.show();

            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    request.SendRequest(currUserId, userIds.get(3));
                    addRejection(currUserId, userIds.get(3));
                    Toast.makeText(requireContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            // Set an OnClickListener for the close button to dismiss the dialog box
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(requireContext(), "No User In This Box", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Pulls up a dialog box to display more user information and give
     * the user the ability to connect with them
     *
     * @param view the current view
     */
    private void pullUserDataBox5(View view) {
        if(userInfos.size() >= 5) {
            // Create an AlertDialog.Builder to display the dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("User Info");

            // Inflate the custom layout for the dialog box
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_user_info, null);
            builder.setView(dialogView);

            // Get references to the views in the dialog layout
            ImageView profilePictureImageView = dialogView.findViewById(R.id.user_profile_picture);
            TextView userNameTextView = dialogView.findViewById(R.id.name_field);
            TextView userMajorTextView = dialogView.findViewById(R.id.major_field);
            TextView userYearTextView = dialogView.findViewById(R.id.year_field);
            LinearLayout studyBuddyCoursesView = dialogView.findViewById(R.id.course_container);
            // TextView userBirthdateTextView = dialogView.findViewById(R.id.birthdate_field);

            Button closeButton = dialogView.findViewById(R.id.close_button);
            Button connectButton = dialogView.findViewById(R.id.connect_button);

            // Set the views' content based on the selected user
            //profilePictureImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.badger));
            downloadPFP(userIds.get(4), profilePictureImageView);
            userNameTextView.setText(userInfos.get(4).getUsername());
            userMajorTextView.setText(userInfos.get(4).getMajor());
            userYearTextView.setText(String.valueOf(userInfos.get(4).getYear()));
            // userBirthdateTextView.setText(userInfos.get(4).getDateOfBirth());
            HashMap<String, String> userCourses = userInfos.get(4).getStudyBuddyCourses();
            for (String course : userCourses.values()) {
                // populate courses as autocompletetextviews within the container and setting them to disabled
                TextView autoCompleteTextView = new TextView(requireContext());
                autoCompleteTextView.setGravity(Gravity.CENTER); // center the text
                autoCompleteTextView.setText(course);
                autoCompleteTextView.setTextSize(13);
                autoCompleteTextView.setEnabled(false);
                autoCompleteTextView.setTextColor(Color.DKGRAY);
                autoCompleteTextView.setBackgroundResource(R.drawable.custom_edit_text_cut);
                autoCompleteTextView.setVisibility(View.VISIBLE);
                studyBuddyCoursesView.addView(autoCompleteTextView);
            }

            // Create the dialog and show it
            AlertDialog dialog = builder.create();
            dialog.show();

            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    request.SendRequest(currUserId, userIds.get(4));
                    addRejection(currUserId, userIds.get(4));
                    Toast.makeText(requireContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            // Set an OnClickListener for the close button to dismiss the dialog box
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(requireContext(), "No User In This Box", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Pulls up a dialog box to display more user information and give
     * the user the ability to connect with them
     *
     * @param view the current view
     */
    private void pullUserDataBox6(View view) {
        if(userInfos.size() >= 6) {
            // Create an AlertDialog.Builder to display the dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("User Info");

            // Inflate the custom layout for the dialog box
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_user_info, null);
            builder.setView(dialogView);

            // Get references to the views in the dialog layout
            ImageView profilePictureImageView = dialogView.findViewById(R.id.user_profile_picture);
            TextView userNameTextView = dialogView.findViewById(R.id.name_field);
            TextView userMajorTextView = dialogView.findViewById(R.id.major_field);
            TextView userYearTextView = dialogView.findViewById(R.id.year_field);
            LinearLayout studyBuddyCoursesView = dialogView.findViewById(R.id.course_container);
            // TextView userBirthdateTextView = dialogView.findViewById(R.id.birthdate_field);

            Button closeButton = dialogView.findViewById(R.id.close_button);
            Button connectButton = dialogView.findViewById(R.id.connect_button);

            // Set the views' content based on the selected user
            //profilePictureImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.badger));
            downloadPFP(userIds.get(5), profilePictureImageView);
            userNameTextView.setText(userInfos.get(5).getUsername());
            userMajorTextView.setText(userInfos.get(5).getMajor());
            userYearTextView.setText(String.valueOf(userInfos.get(5).getYear()));
            // userBirthdateTextView.setText(userInfos.get(5).getDateOfBirth());
            HashMap<String, String> userCourses = userInfos.get(5).getStudyBuddyCourses();
            for (String course : userCourses.values()) {
                // populate courses as autocompletetextviews within the container and setting them to disabled
                TextView autoCompleteTextView = new TextView(requireContext());
                autoCompleteTextView.setGravity(Gravity.CENTER); // center the text
                autoCompleteTextView.setText(course);
                autoCompleteTextView.setTextSize(13);
                autoCompleteTextView.setEnabled(false);
                autoCompleteTextView.setTextColor(Color.DKGRAY);
                autoCompleteTextView.setBackgroundResource(R.drawable.custom_edit_text_cut);
                autoCompleteTextView.setVisibility(View.VISIBLE);
                studyBuddyCoursesView.addView(autoCompleteTextView);
            }


            // Create the dialog and show it
            AlertDialog dialog = builder.create();
            dialog.show();

            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    request.SendRequest(currUserId, userIds.get(5));
                    addRejection(currUserId, userIds.get(5));
                    Toast.makeText(requireContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            // Set an OnClickListener for the close button to dismiss the dialog box
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(requireContext(), "No User In This Box", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Pulls up a dialog box to allow the user to choose what kind
     * of connections they are looking for
     *
     * @param view the current view
     */
    private void pullFilterDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the custom layout for the dialog box
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_filter, null);

        CheckBox mentorCheckBox = dialogView.findViewById(R.id.mentor_checkbox);
        CheckBox menteeCheckBox = dialogView.findViewById(R.id.mentee_checkbox);
        CheckBox studyBuddyCheckBox = dialogView.findViewById(R.id.study_buddy_checkbox);

        CompletableFuture<Boolean> whetherStudyBuddy = readWhetherStudyBuddy(currUserId);
        CompletableFuture<Boolean> whetherMentor = readWhetherMentor(currUserId);
        CompletableFuture<Boolean> whetherMentee = readWhetherMentee(currUserId);
        // Set up the checkboxes to allow only one selection at a time
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mentorCheckBox.setChecked(buttonView == mentorCheckBox);
                    menteeCheckBox.setChecked(buttonView == menteeCheckBox);
                    studyBuddyCheckBox.setChecked(buttonView == studyBuddyCheckBox);
                }
            }
        };

        mentorCheckBox.setOnCheckedChangeListener(listener);
        menteeCheckBox.setOnCheckedChangeListener(listener);
        studyBuddyCheckBox.setOnCheckedChangeListener(listener);

        builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the selected checkbox here
                        if (mentorCheckBox.isChecked()) {
                            whetherMentee.thenAccept(bool -> {
                                if(!bool) {
                                    Toast.makeText(requireContext(), "You have not opted to be a mentee", Toast.LENGTH_SHORT).show();
                                    sortMap.put("ConnectionType", "none");
                                }
                                else {
                                    sortMap.put("ConnectionType", "Mentor");
                                }
                            });
                        } else if (menteeCheckBox.isChecked()) {
                            whetherMentor.thenAccept(bool -> {
                                if(!bool) {
                                    Toast.makeText(requireContext(), "You have not opted to be a mentor", Toast.LENGTH_SHORT).show();
                                    sortMap.put("ConnectionType", "none");
                                }
                                else {
                                    sortMap.put("ConnectionType", "Mentee");
                                }
                            });
                        } else if (studyBuddyCheckBox.isChecked()) {
                            whetherStudyBuddy.thenAccept(bool -> {
                                if(!bool) {
                                    Toast.makeText(requireContext(), "You have not opted to be a study buddy", Toast.LENGTH_SHORT).show();
                                    sortMap.put("ConnectionType", "none");
                                }
                                else {
                                    sortMap.put("ConnectionType", "StudyBuddy");
                                }
                            });
                        }
                        else {
                            sortMap.put("ConnectionType", "none");
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Simple function to set the boxes invisible and only making those with user
     * information visible after.
     */
    public void setBoxesInvisible() {
        box1Card.setVisibility(View.INVISIBLE);
        box2Card.setVisibility(View.INVISIBLE);
        box3Card.setVisibility(View.INVISIBLE);
        box4Card.setVisibility(View.INVISIBLE);
        box5Card.setVisibility(View.INVISIBLE);
        box6Card.setVisibility(View.INVISIBLE);
    }


    private void setRatingInvisible() {
        ratingNumber1.setVisibility(View.INVISIBLE);
        ratingNumber2.setVisibility(View.INVISIBLE);
        ratingNumber3.setVisibility(View.INVISIBLE);
        ratingNumber4.setVisibility(View.INVISIBLE);
        ratingNumber5.setVisibility(View.INVISIBLE);
        ratingNumber6.setVisibility(View.INVISIBLE);
    }
}