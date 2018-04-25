package com.example.stefano.spinup20;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserHome extends AppCompatActivity implements View.OnClickListener{

    private Button logout_button;
    private Button createApp_button;
    private Button searchApp_button;
    private Button followedApp_button;
    private Button createdApp_button;
    private TextView createdNumberTextView;
    private TextView followedNumberTextView;
    private TextView username;
    private TextView mail;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser fbUser;

    private List<rvFollowedApp> appList;
    private List<rvCreatedApp> createdAppList;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount profile;
    private String userID;

    private int createdApp_counter;
    private int followedApp_counter;

    private boolean exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        profile = GoogleSignIn.getLastSignedInAccount(this);
        userID = "";

        databaseReference = FirebaseDatabase.getInstance().getReference();

        logout_button = findViewById(R.id.logout);
        createApp_button = findViewById(R.id.create_app);
        searchApp_button = findViewById(R.id.search_app);
        followedApp_button = findViewById(R.id.followed_app);
        createdApp_button = findViewById(R.id.created_app);
        createdNumberTextView = findViewById(R.id.created_number);
        followedNumberTextView = findViewById(R.id.followed_number);
        username = findViewById(R.id.username);
        mail = findViewById(R.id.mail);

        firebaseAuth = firebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();

        appList = new ArrayList<>();
        createdAppList = new ArrayList<>();

        exit = false;

        createdApp_counter = 0;
        followedApp_counter = 0;

        //Check if the user is not logged in : return to the login activity
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SignIn.class));
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        profile = GoogleSignIn.getLastSignedInAccount(this);

        //getData();
        checkWhoSignIn();
        new MyTask().execute();


        logout_button.setOnClickListener(this);
        createApp_button.setOnClickListener(this);
        searchApp_button.setOnClickListener(this);
        followedApp_button.setOnClickListener(this);
        createdApp_button.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        if(view == logout_button) {
            firebaseAuth.signOut();

            // Google sign out
            mGoogleSignInClient.signOut();

            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        if(view == createApp_button) {
            startActivity(new Intent(this, CreateApp.class));
            finish();
        }

        if(view == searchApp_button) {
            startActivity(new Intent(this, SearchApp.class));
            finish();
        }

        if(view == followedApp_button) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /*
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        String followedApp = ds.getValue(String.class);
                        if(followedApp == null) {
                            Toast.makeText(UserHome.this, "You follow no apps", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            rvFollowedApp app = new rvFollowedApp(followedApp);
                            appList.add(app);
                        }
                    }
                    if(appList.size() > 1) {
                        Intent intent = new Intent(UserHome.this, SearchByFollow.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("appList", (Serializable) appList);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(UserHome.this, "You not follow any app", Toast.LENGTH_SHORT).show();
                    }
                    */
                    if(!dataSnapshot.child("users").child(userID).child("nFollowed").getValue(String.class).equals("0")) {
                        for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("followed_apps").getChildren()) {
                            String followedApp = ds.getValue(String.class);
                            if(followedApp == null) {
                                Toast.makeText(UserHome.this, "You follow no apps", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                rvFollowedApp app = new rvFollowedApp(followedApp);
                                appList.add(app);
                            }
                        }

                        Intent intent = new Intent(UserHome.this, SearchByFollow.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("appList", (Serializable) appList);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        finish();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        if(view == createdApp_button) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /*
                    for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("developed_apps").getChildren()) {
                        String createdApp = ds.getValue(String.class);
                        String category = "";
                        //COMMENTA
                        for(DataSnapshot dsds : dataSnapshot.child("apps").child(createdApp).child("category").getChildren()) {
                            category = dsds.getValue(String.class);
                            Toast.makeText(UserHome.this, "Entri ecco aventro?", Toast.LENGTH_SHORT).show();
                        }
                        //FINE COMMENTO
                        if(createdApp == null) {
                            Toast.makeText(UserHome.this, "You are not created any app", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            rvCreatedApp app = new rvCreatedApp(createdApp, category);
                            createdAppList.add(app);
                        }
                    }
                    if(createdAppList.size() > 1) {
                        Intent intent = new Intent(UserHome.this, SearchByCreated.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("appList", (Serializable) createdAppList);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(UserHome.this, "You are not create any app", Toast.LENGTH_SHORT).show();
                    }
                    */
                    if(!dataSnapshot.child("users").child(userID).child("nCreated").getValue(String.class).equals("0")) {
                        for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("developed_apps").getChildren()) {
                            String createdApp = ds.getValue(String.class);
                            String category = "";

                            if(createdApp == null) {
                                Toast.makeText(UserHome.this, "You are not created any app", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                rvCreatedApp app = new rvCreatedApp(createdApp, category);
                                createdAppList.add(app);
                            }
                        }
                        Intent intent = new Intent(UserHome.this, SearchByCreated.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("appList", (Serializable) createdAppList);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void onBackPressed() {
        //super.onBackPressed();
        if(exit) {
            finish();
        }
        else {
            Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3*1000);
        }
    }

    private void checkWhoSignIn() {
        if(profile != null) {
            userID = profile.getId();
            getGoogleUserInfo(userID);
            mail.setText(profile.getEmail());
        }
        else {
            userID = fbUser.getUid();
            mail.setText(fbUser.getEmail());
        }
    }
    
    private void getGoogleUserInfo(final String user) {
        final String name = profile.getGivenName();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("users").child(user).child("name").getValue(String.class).equals(name)) {
                    return;
                }

                databaseReference.child("users").child(user).child("nFollowed").setValue("0");
                databaseReference.child("users").child(user).child("nCreated").setValue("0");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("developed_apps").getChildren()) {
                    if(ds.getValue() != null) {
                        createdApp_counter++;
                    }
                }
                createdNumberTextView.setText(Integer.toString(createdApp_counter));

                for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("followed_apps").getChildren()) {
                    if(ds.getValue() != null) {
                        followedApp_counter++;
                    }
                }
                followedNumberTextView.setText(Integer.toString(followedApp_counter));

                /*
                for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("followed_users").getChildren()) {
                    if(ds.getValue() != null) {
                        followedUser_counter++;
                    }
                }
                followedUserNumberTextView.setText(Integer.toString(followedUser_counter));
                */

                username.setText(dataSnapshot.child("users").child(userID).child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class MyTask extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... params) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /*
                    for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("developed_apps").getChildren()) {
                        if(ds.getValue() != null) {
                            createdApp_counter++;
                        }
                    }
                    createdNumberTextView.setText(Integer.toString(createdApp_counter));

                    for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("followed_apps").getChildren()) {
                        if(ds.getValue() != null) {
                            followedApp_counter++;
                        }
                    }
                    followedNumberTextView.setText(Integer.toString(followedApp_counter));
                    */

                    createdNumberTextView.setText(dataSnapshot.child("users").child(userID).child("nCreated").getValue(String.class));
                    followedNumberTextView.setText(dataSnapshot.child("users").child(userID).child("nFollowed").getValue(String.class));
                    username.setText(dataSnapshot.child("users").child(userID).child("name").getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }
    }
}
