package com.example.badgerconnect.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.badgerconnect.Adapter.RequestAdapter;
import com.example.badgerconnect.Model.Request;
import com.example.badgerconnect.Model.User;
import com.example.badgerconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
Essentially, parse the pending_requests json structure on firebase for list of sender_ids
use those ids to display sent requests

TESTING THE BUTTONS BEFORE FIREBASE CRASHED. cANNOT READ DATA FROM MAIN MENU
REFRESH ONCE ACCEPT DECLINE IN PCLICKED
WRTIRE SEND REQUEST METHOD

 */
public class PendingRequestsFragment extends Fragment {

    private RecyclerView recyclerView;
    FirebaseAuth auth;
    private RequestAdapter requestAdapter;
    private List<User> mUsers;
    private ArrayList<String> sender_ids = new ArrayList<>();
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       // System.out.println("r keys " + FirebaseAuth.getInstance().getCurrentUser());
        View view = inflater.inflate(R.layout.fragment_pending_requests, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_requests);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        readUsers();

        //TESTING SENDREQUEST
        Request request= new Request();
        request.SendRequest( "vvv", firebaseUser.getUid());

        return view;
    }

    private void readUsers() {
        DatabaseReference DataRef = FirebaseDatabase.getInstance().getReference("Data");

        DataRef.addValueEventListener(new ValueEventListener() {
            DataSnapshot requestData = null;
            DataSnapshot userData = null;

            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                for (DataSnapshot data : datasnapshot.getChildren()) {
                    if (data.getKey().equals("Pending_Connection_Requests")) {
                        requestData = data;
                        //System.out.println("Conv " + convData.getValue());
                    } else if (data.getKey().equals("Users")) {
                        userData = data;
                        //System.out.println("Usss " + data.getValue());
                    }
                }

                {//get the list of sender_ids
                    for (DataSnapshot reqSnapshot : requestData.getChildren()) {
                        GenericTypeIndicator<HashMap<String, Object>> p2 = new GenericTypeIndicator<HashMap<String, Object>>() {
                        };
                        //System.out.println("r keys " + firebaseUser.getUid());
                        if (reqSnapshot.getKey().equals(firebaseUser.getUid())){
                            for (DataSnapshot senderID: reqSnapshot.getChildren()){
                               // System.out.println("r senderids " + senderID.getValue());
                                sender_ids.add(senderID.getKey().toString());
                            }
                        }
                    }
                    //System.out.println("ppp111 " + sender_ids);
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
                           //no need for list of chat ids
                        });
                        //check to see if the users we're parsing are in the list
                        assert user != null;
                        assert firebaseUser != null;
                        // System.out.println("ppp  " + sender_ids); SEND IS CRASHING!
                        if (!user.getUid().equals(firebaseUser.getUid()) && sender_ids.contains(user.getUid()) && !mUsers.contains(user.getUid())) {
                            mUsers.add(user);
                        }
                        i++;
                    }
                    requestAdapter = new RequestAdapter(getContext(), mUsers);
                    requestAdapter.notifyDataSetChanged(); //how to update fragment upon accept/decline
                    recyclerView.setAdapter(requestAdapter);

                    mUsers=new ArrayList<User>(); //empty the list for next reload!
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
}