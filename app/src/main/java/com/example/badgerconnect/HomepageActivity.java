package com.example.badgerconnect;

import static com.example.badgerconnect.DatabaseFunctions.algorithmStudyBuddy;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity  extends AppCompatActivity {

    ImageView box1Image, box2Image, box3Image, box4Image, box5Image, box6Image;
    TextView box1Text, box2Text, box3Text, box4Text, box5Text, box6Text;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        initializeUI();

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();

        List<String> foundUsers = new ArrayList<>();

        algorithmStudyBuddy(firebaseUser.getUid());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call your function here
                // This function will be triggered when the user pulls down on the screen
                // to refresh the content

            }
        });
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

}
