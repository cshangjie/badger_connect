package com.example.badgerconnect.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.badgerconnect.Adapter.UserAdapter;
import com.example.badgerconnect.Model.User;
import com.example.badgerconnect.R;
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

        return view;
    }

    // Define a callback interface for data retrieval
    public interface DataCallback {
        void onDataReceived(DataSnapshot dataSnapshot);
    }

    // Define a callback interface for data retrieval errors
    public interface DataErrorCallback {
        void onDataError(DatabaseError databaseError);
    }

    // Define a method for retrieving data with a callback
    public static void retrieveData(DatabaseReference ref, final DataCallback callback, final DataErrorCallback errorCallback) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Call the callback with the data
                callback.onDataReceived(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Call the error callback with the error
                errorCallback.onDataError(databaseError);
            }
        });
    }


    private void readUsers() {
        DatabaseReference DataRef = FirebaseDatabase.getInstance().getReference("Data");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DataRef.addValueEventListener(new ValueEventListener() {
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

                    mUsers=null; //empty the list for next reload!
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}