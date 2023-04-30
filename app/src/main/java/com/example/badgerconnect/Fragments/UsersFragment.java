package com.example.badgerconnect.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.badgerconnect.Adapter.UserAdapter;
import com.example.badgerconnect.MainActivity_msg;
import com.example.badgerconnect.Model.User;
import com.example.badgerconnect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


///BIG changes made, in order to integrate chat feature to nav bar, we'll be bypassong the mainActivity_msg class
//I have adapted and integrated the important sections of that class directly into userFragments
public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers;
    private ArrayList<String> participants = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        readUsers();

        //TEST the ADDUSER method
        User user= new User("USERX",
                "John Doe",
                "https://www.google.com/imgres?imgurl=https%3A%2F%2Fd2jyir0m79gs60.cloudfront.net%2Fnews%2Fimages%2Fsuccessful-college-student-lg.png&tbnid=NsNZ54F8DxDgSM&vet=12ahUKEwivprXJwdL-AhW2E94AHafxCxUQMygAegUIARDkAQ..i&imgrefurl=https%3A%2F%2Fplexuss.com%2Fnews%2Farticle%2Fhow-to-be-a-successful-college-student&docid=4xUYG0LqyYhxbM&w=700&h=500&q=college%20student&ved=2ahUKEwivprXJwdL-AhW2E94AHafxCxUQMygAegUIARDkAQ",
                null);
        user.addUser();


        return view;
    }

    private void readUsers() {
        DatabaseReference DataRef = FirebaseDatabase.getInstance().getReference("Data");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            DataSnapshot convData = null;
            DataSnapshot userData = null;

            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                for (DataSnapshot data : datasnapshot.getChildren()) {
                    if (data.getKey().equals("Conversations")) {
                        convData = data;
                        //System.out.println("Conv " + convData.getValue());
                    } else if (data.getKey().equals("Users")) {
                        userData = data;
                        //System.out.println("Usss " + data.getValue());
                    }
                }

                {
                    for (DataSnapshot convSnapshot : convData.getChildren()) {
                        GenericTypeIndicator<HashMap<String, Object>> p2 = new GenericTypeIndicator<HashMap<String, Object>>() {
                        };
                       // System.out.println("ppp111 " + convSnapshot.child("Participants").getValue());
                        HashMap<String, Object> pMap = convSnapshot.child("Participants").getValue(p2);
                        String curr_userId = FirebaseAuth.getInstance().getUid();
                        //Add participants from conversations in which current user is a member of
                        if (pMap.values().contains(curr_userId)) {
                            pMap.forEach((key, value) -> participants.add(value.toString()));
                        }
                    }
                    //System.out.println("ppp111 " + participants);
                }

                ///////USER////////
                {
                    int i = 0;
                    for (DataSnapshot usersnapshot : userData.getChildren()) {
                        User user = new User();
                        GenericTypeIndicator<HashMap<String, Object>> UM = new GenericTypeIndicator<HashMap<String, Object>>() {
                        };
                        HashMap<String, Object> uMap = usersnapshot.getValue(UM);
                        uMap.forEach((key, value) -> {
                            if (key.equals("Name")) user.setName(value.toString());
                            if (key.equals("Uid")) user.setUid(value.toString());
                            if (key.equals("profile_pic")) user.setProfile_pic(value.toString());
                            if (key.equals("Chat_ids")) {
                                String str = value.toString();
                                ArrayList<String> c_ids = new ArrayList<String>(Arrays.asList(str.substring(1, str.length() - 1).split(",")));

                                user.setChat_ids((ArrayList<String>) c_ids);
                            }
                        });
                        //check to see if the users we're parsing are in the list
                        assert user != null;
                        assert firebaseUser != null;
                       // System.out.println("ppp  " + participants); SEND IS CRASHING!
                        if (!user.getUid().equals(firebaseUser.getUid()) && participants.contains(user.getUid()) && !mUsers.contains(user.getUid())) {
                            mUsers.add(user);
                        }
                        i++;
                    }
                    userAdapter = new UserAdapter(getContext(), mUsers);
                    recyclerView.setAdapter(userAdapter);

                    mUsers=new ArrayList<User>(); //empty the list for next reload!
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}