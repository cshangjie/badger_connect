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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

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

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Data").child("Users");
        //System.out.println("here is the reference" + reference);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //System.out.println("here is the currentUser" + firebaseUser);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mUsers.clear();
                int i=0;
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    GenericTypeIndicator<List<User>> t = new GenericTypeIndicator<List<User>>() {};
                    List<User> userList = datasnapshot.getValue(t);
                    User user=userList.get(i);

                    //System.out.println("USER IS:" + user);

                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getUid().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                    i++;
                }

                userAdapter = new UserAdapter(getContext(), mUsers);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}