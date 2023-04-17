package com.example.badgerconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.badgerconnect.Fragments.UsersFragment;
import com.example.badgerconnect.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity_msg extends AppCompatActivity {
    MaterialEditText username, email, password;
    Button btn_login;
    FirebaseAuth auth;
    public static int Mid=0;

   // CircleImageView profile_image;
    ImageView profile_image, profile_image_menu; //USE FOR NOW
    DatabaseReference reference;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_msg);

        auth=FirebaseAuth.getInstance();

        String email1 = "test1@gmail.com";
        String email2 = "cbfu@wisc.edu";
        String password = "000000";

        //auth.signOut();
        //System.out.println("About to sign in");
        auth.signInWithEmailAndPassword(email2, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    System.out.println("Sign in success");
                    FirebaseUser user = auth.getCurrentUser();
                    // Do something with the user object
                } else {
                    // If sign in fails, display a message to the user.
                    System.out.println("Sign in failed");
                    Toast.makeText(MainActivity_msg.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //System.out.println("Past sign in");

        profile_image= findViewById(R.id.profile_image_main);
        username = findViewById(R.id.username);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        //reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference= FirebaseDatabase.getInstance().getReference("Data").child("Users");
        //System.out.println("reference is:" + reference);

       // Query query= reference.orderByChild("Uid").equalTo(firebaseUser.getUid());
        Query query= reference;


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
               //collects main users data from firebase realtime database table users
                //uses that data to populate user's profile

                //Revision, changes based on any changes to user data on firebase
                User user=new User();
                for (DataSnapshot userinfo: datasnapshot.getChildren()){
                    //System.out.println("data is:" + userinfo.getValue());
                    user=userinfo.getValue(User.class);
                    //System.out.println("username is:" + user.getName());
                    //System.out.println("user info is:" + user.getName());  //FINALLY
                }

                assert user != null;
                assert firebaseUser != null;

                //I.T. set profile pic for current user
                username.setTag(user.getName());
                if(user.getProfile_pic().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(MainActivity_msg.this).load(user.getProfile_pic()).into(profile_image);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        TabLayout tabLayout= findViewById(R.id.tab_layout);
        ViewPager viewPager= findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter= new ViewPagerAdapter(getSupportFragmentManager());
        //viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
        viewPagerAdapter.addFragment(new UsersFragment(), "Users");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
       // profile_image_menu=findViewById(R.id.profile_image_menu);
        //Glide.with(MainActivity.this).load("https://imgs.search.brave.com/LgQgImFe3svKlAS-BpyTjJeChTRvYkT37zxe_EIvuOA/rs:fit:711:225:1/g:ce/aHR0cHM6Ly90c2Uz/Lm1tLmJpbmcubmV0/L3RoP2lkPU9JUC5l/ZUxkNFQxSXI5OEVR/dE03RW1sYTdnSGFF/OCZwaWQ9QXBp").into(profile_image_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()){

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();

                finish();
                return true;
        }
        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments= new ArrayList<>();
            this.titles= new ArrayList<>();
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment (Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}