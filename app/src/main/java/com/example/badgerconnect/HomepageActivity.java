package com.example.badgerconnect;

import static com.example.badgerconnect.DatabaseFunctions.algorithmMentee;
import static com.example.badgerconnect.DatabaseFunctions.algorithmMentor;
import static com.example.badgerconnect.DatabaseFunctions.algorithmStudyBuddy;
import static com.example.badgerconnect.DatabaseFunctions.downloadPFP;
import static com.example.badgerconnect.DatabaseFunctions.readUserData;
import static com.example.badgerconnect.DatabaseFunctions.sendMessage;

import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HomepageActivity  extends AppCompatActivity {

    ImageView box1Image, box2Image, box3Image, box4Image, box5Image, box6Image;
    TextView box1Text, box2Text, box3Text, box4Text, box5Text, box6Text;
    CardView box1Card, box2Card, box3Card, box4Card, box5Card, box6Card;
    ArrayList<UserInfo> userInfos = new ArrayList<>(6);
    ArrayList<String> userIds = new ArrayList<>(6);
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        initializeUI();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String currUserId = "000003";

        HashMap<String, String> sortMap = new HashMap<>();

        HashMap<String, String> prevOp = new HashMap<String, String>() {{
                put("ConnectionType", "null");
            }};

        List<String> foundUsers = new ArrayList<>();

        HashMap<String, Integer> foundUsersStudyBuddy= new HashMap<>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call your function here
                // This function will be triggered when the user pulls down on the screen
                // to refresh the content
                //TODO: Set the sortMap with the user selections
                setBoxesInvisible();
                sortMap.put("ConnectionType", "StudyBuddy");
                if((sortMap.get("ConnectionType").equals("Mentor")) && (!prevOp.get("ConnectionType").equals("Mentor"))) {
                    CompletableFuture<List<String>> futureUsers = algorithmMentor(currUserId, foundUsers);
                    futureUsers.thenAccept(users -> {
                        populateSquares(users);
                    });
                }
                else if((sortMap.get("ConnectionType").equals("Mentee")) && (!prevOp.get("ConnectionType").equals("Mentee"))) {
                    CompletableFuture<List<String>> futureUsers = algorithmMentor(currUserId, foundUsers);
                    futureUsers.thenAccept(users -> {
                        populateSquares(users);
                    });
                }
                else if((sortMap.get("ConnectionType").equals("StudyBuddy")) && (!prevOp.get("ConnectionType").equals("StudyBuddy"))) {
                    CompletableFuture<HashMap<String, Integer>> futureUsers = algorithmStudyBuddy(currUserId, foundUsersStudyBuddy);
                    futureUsers.thenAccept(users -> {
                        populateSquaresStudyBuddy(users);
                    });
                }
                else if((sortMap.get("ConnectionType").equals("Mentor")) && (prevOp.get("ConnectionType").equals("Mentor"))) {
                    populateSquares(foundUsers);
                }
                else if((sortMap.get("ConnectionType").equals("Mentee")) && (prevOp.get("ConnectionType").equals("Mentee"))) {
                    populateSquares(foundUsers);
                }
                else if((sortMap.get("ConnectionType").equals("StudyBuddy")) && (prevOp.get("ConnectionType").equals("StudyBuddy"))) {
                    populateSquaresStudyBuddy(foundUsersStudyBuddy);
                }
                prevOp.put("ConnectionType", sortMap.get("ConnectionType"));
            }
        });
    }

    private void populateSquaresStudyBuddy(HashMap<String, Integer> foundUsers) {
        List<String> users = new ArrayList<>();
        users.addAll(foundUsers.keySet());
        Collections.shuffle(users);
        ImageView[] imageViews = {box1Image, box2Image, box3Image, box4Image, box5Image, box6Image};
        TextView[] textViews = {box1Text, box2Text, box3Text, box4Text, box5Text, box6Text};
        CardView[] cardViews = {box1Card, box2Card, box3Card, box4Card, box5Card, box6Card};
        int size = (users.size() < 6) ? users.size() : 6;
        Log.d("firebase", "i got here in the middle of populate");
        for(int i = 0 ; i < size ; i++) {
            UserInfo currUser = new UserInfo();
            String currUserId = users.get(i);
            CompletableFuture<UserInfo> currUserData = readUserData(currUserId, currUser);
            int currIndex = i;
            TextView currTextView = textViews[i];
            ImageView currImageView = imageViews[i];
            CardView currCardView = cardViews[i];
            currUserData.thenAccept(user -> {
                userInfos.add(currIndex, user);
                currCardView.setVisibility(View.VISIBLE);
                currTextView.setText(user.getUsername());
                currImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.badger));
                //downloadPFP(currUserId, currImageView);
            });
        }
        swipeRefreshLayout.setRefreshing(false);

    }

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
            currUserData.thenAccept(user -> {
                userInfos.add(currIndex, user);
                currCardView.setVisibility(View.VISIBLE);
                currTextView.setText(user.getUsername());
                currImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.badger));
                //downloadPFP(currUserId, currImageView);
                    });
        }
        swipeRefreshLayout.setRefreshing(false);

    }

    private void initializeUI() {
        box1Image = findViewById(R.id.profile_picture1);
        box2Image = findViewById(R.id.profile_picture2);
        box3Image = findViewById(R.id.profile_picture3);
        box4Image = findViewById(R.id.profile_picture4);
        box5Image = findViewById(R.id.profile_picture5);
        box6Image = findViewById(R.id.profile_picture6);
        box1Text = findViewById(R.id.name1);
        box2Text = findViewById(R.id.name2);
        box3Text = findViewById(R.id.name3);
        box4Text = findViewById(R.id.name4);
        box5Text = findViewById(R.id.name5);
        box6Text = findViewById(R.id.name6);
        box1Card = findViewById(R.id.card1);
        box2Card = findViewById(R.id.card2);
        box3Card = findViewById(R.id.card3);
        box4Card = findViewById(R.id.card4);
        box5Card = findViewById(R.id.card5);
        box6Card = findViewById(R.id.card6);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    public void pullUserDataBox1(View v) {
        if(userInfos.size() >= 1) {
            // Create an AlertDialog.Builder to display the dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(HomepageActivity.this);
            builder.setTitle("User Info");

            // Inflate the custom layout for the dialog box
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_user_info, null);
            builder.setView(dialogView);

            // Get references to the views in the dialog layout
            ImageView profilePictureImageView = dialogView.findViewById(R.id.user_profile_picture);
            TextView userNameTextView = dialogView.findViewById(R.id.user_name);
            Button closeButton = dialogView.findViewById(R.id.close_button);

            // Set the views' content based on the selected user
            profilePictureImageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.badger));
            //TODO: replace the set image view with the actual user's image
            //downloadPFP(userIds.get(0), profilePictureImageView);
            userNameTextView.setText(userInfos.get(0).getUsername());

            // Create the dialog and show it
            AlertDialog dialog = builder.create();
            dialog.show();

            // Set an OnClickListener for the close button to dismiss the dialog box
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(this, "No User In This Box", Toast.LENGTH_SHORT).show();
        }
    }

    public void setBoxesInvisible() {
        box1Card.setVisibility(View.INVISIBLE);
        box2Card.setVisibility(View.INVISIBLE);
        box3Card.setVisibility(View.INVISIBLE);
        box4Card.setVisibility(View.INVISIBLE);
        box5Card.setVisibility(View.INVISIBLE);
        box6Card.setVisibility(View.INVISIBLE);
    }

    //public void algoTest(View v) {
    //    sendMessage();
    //}
}
