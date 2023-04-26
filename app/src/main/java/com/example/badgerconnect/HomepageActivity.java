package com.example.badgerconnect;

import static com.example.badgerconnect.DatabaseFunctions.algorithmMentee;
import static com.example.badgerconnect.DatabaseFunctions.algorithmMentor;
import static com.example.badgerconnect.DatabaseFunctions.algorithmStudyBuddy;
import static com.example.badgerconnect.DatabaseFunctions.downloadPFP;
import static com.example.badgerconnect.DatabaseFunctions.readUserData;
import static com.example.badgerconnect.DatabaseFunctions.sendMessage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HomepageActivity  extends AppCompatActivity {

    ImageView box1Image, box2Image, box3Image, box4Image, box5Image, box6Image;
    TextView box1Text, box2Text, box3Text, box4Text, box5Text, box6Text;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        initializeUI();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String currUserId = "000001";

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
                sortMap.put("ConnectionType", "Mentor");
                if((sortMap.get("ConnectionType").equals("Mentor")) && (!prevOp.get("ConnectionType").equals("Mentor"))) {
                    algorithmMentor(currUserId, foundUsers);
                    populateSquares(foundUsers);
                }
                else if((sortMap.get("ConnectionType").equals("Mentee")) && (!prevOp.get("ConnectionType").equals("Mentee"))) {
                    algorithmMentee(currUserId, foundUsers);
                    populateSquares(foundUsers);
                }
                else if((sortMap.get("ConnectionType").equals("StudyBuddy")) && (!prevOp.get("ConnectionType").equals("StudyBuddy"))) {
                    algorithmStudyBuddy(currUserId, foundUsersStudyBuddy);
                    populateSquaresStudyBuddy(foundUsersStudyBuddy);
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
        List<String> users = (List<String>) foundUsers.keySet();
        Collections.shuffle(users);
        ImageView[] imageViews = {box1Image, box2Image, box3Image, box4Image, box5Image, box6Image};
        TextView[] textViews = {box1Text, box2Text, box3Text, box4Text, box5Text, box6Text};
        int size = (users.size() < 6) ? users.size() : 6;
        for(int i = 0 ; i < size ; i++) {
            UserInfo currUser = new UserInfo();
            String currUserId = users.get(i);
            readUserData(currUserId, currUser);
            textViews[i].setText(currUser.getUsername());
            downloadPFP(currUserId, imageViews[i]);
        }

    }

    private void populateSquares(List<String> foundUsers) {
        Collections.shuffle(foundUsers);
        ImageView[] imageViews = {box1Image, box2Image, box3Image, box4Image, box5Image, box6Image};
        TextView[] textViews = {box1Text, box2Text, box3Text, box4Text, box5Text, box6Text};
        Log.d("Middle of populate squares", String.valueOf(foundUsers));
        int size = (foundUsers.size() < 6) ? foundUsers.size() : 6;
        for(int i = 0 ; i < size ; i++) {
            UserInfo currUser = new UserInfo();
            String currUserId = foundUsers.get(i);
            readUserData(currUserId, currUser);
            textViews[i].setText(currUser.getUsername());
            downloadPFP(currUserId, imageViews[i]);
        }
        Log.d("End of populate squares", String.valueOf(foundUsers));

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
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }


    public void algoTest(View v) {
        sendMessage();
    }
}
