package com.example.stefano.spinup20;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppHome extends AppCompatActivity implements View.OnClickListener{

    private Intent intentExtras;
    private Bundle bundle;
    private String appName;
    private String userName;

    private TextView appNameTextView;
    private TextView listDeveloperTextView;
    private TextView numberFollowTextView;
    private TextView rateValueTextView;
    private EditText insertComment;
    private Button postComment;
    private Button follow_button;
    private Button rateButton;
    private Spinner rateSpinner;

    private RecyclerView recyclerView;
    private rvAdapter adapter;
    private String commentCreator;
    private ArrayList<rvCommentClass> commentCreatorList;
    private String singleComment;
    private String like;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser fbUser;

    private int newValue;
    private String currentValue;
    private String number;

    private String currentRate;
    private String currentNumber;
    private String currentSum;
    private double newRate;
    private double newSum;
    private int newNumber;

    private String likeString;
    private boolean flag;

    private GoogleSignInAccount profile;
    private String userID;

    private String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_home);

        firebaseAuth = firebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fbUser = firebaseAuth.getCurrentUser();

        appNameTextView = findViewById(R.id.appNameTextView);
        listDeveloperTextView = findViewById(R.id.listDeveloperTextView);
        numberFollowTextView = findViewById(R.id.numberFollow);
        rateValueTextView = findViewById(R.id.rateValue);
        insertComment = findViewById(R.id.insertComment);
        postComment = findViewById(R.id.postComment);
        follow_button = findViewById(R.id.follow);
        rateButton = findViewById(R.id.rate);
        rateSpinner = findViewById(R.id.rateSpinner);

        recyclerView = findViewById(R.id.myRecyclerView);
        commentCreatorList = new ArrayList<>();
        singleComment = "";
        like = "";

        newValue = 0;
        currentValue = "";
        number = "";

        newRate = 0.0;
        newSum = 0.0;
        newNumber = 0;
        currentRate = "";
        currentSum = "";
        currentNumber = "";

        likeString = "Like";
        flag = false;

        profile = GoogleSignIn.getLastSignedInAccount(this);
        userID = "";

        temp = "0";

        checkWhoSignIn();
        //getUser();
        getAppName();
        //getDevelopers();
        //getNumberFollow();
        //getRate();
        //printComments();
        followUnfollow();
        checkRate();

        new MyTask().execute();
        new CommentTask().execute();


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new rvAdapter(this, commentCreatorList);
        recyclerView.setAdapter(adapter);


        postComment.setOnClickListener(this);
        follow_button.setOnClickListener(this);
        rateButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v == postComment) {
            createComment();
        }

        if(v == follow_button) {
            if (follow_button.getText().toString().trim().equals("Follow")) {
                follow();
            }
            else {
                unfollow();
            }
        }

        if(v == rateButton) {
            rate();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(), UserHome.class));
    }

    //**************  Utility functions  **************

    public void getAppName() {
        //Get the name of the app
        intentExtras = getIntent();
        bundle = intentExtras.getExtras();
        if(bundle == null) {
            Toast.makeText(this, "App name not available", Toast.LENGTH_SHORT).show();
        }
        else {
            appName = bundle.getString("appName");
        }

        String replace = appName;

        replace = replace.replace("£", ".");
        replace = replace.replace("%", "$");
        replace = replace.replace("&", "[");
        replace = replace.replace("^", "]");
        replace = replace.replace("ç", "#");
        replace = replace.replace("§", "/");

        appNameTextView.setText(replace);
    }

    private void followUnfollow() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("developed_apps").getChildren()) {
                    if (ds.getValue(String.class).equals(appName)) {
                        follow_button.setVisibility(View.INVISIBLE);
                    }
                }
                if(follow_button.getVisibility() != View.INVISIBLE) {
                    for (DataSnapshot ds : dataSnapshot.child("users").child(userID).child("followed_apps").getChildren()) {
                        if (ds.getValue(String.class).equals(appName)) {
                            follow_button.setText("Unfollow");
                            break;
                        } else {
                            follow_button.setText("Follow");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkRate() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("rated_apps").getChildren()) {
                    if(ds.getValue(String.class).equals(appName)) {
                        rateButton.setVisibility(View.INVISIBLE);
                        rateSpinner.setVisibility(View.INVISIBLE);
                        break;
                    }
                }
                for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("developed_apps").getChildren()) {
                    if(ds.getValue(String.class).equals(appName)) {
                        rateButton.setVisibility(View.INVISIBLE);
                        rateSpinner.setVisibility(View.INVISIBLE);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkWhoSignIn() {
        if(profile != null) {
            userID = profile.getId();
        }
        else {
            userID = fbUser.getUid();
        }
    }

    //**************  Button functions  **************

    public void createComment() {
        final String commentText = insertComment.getText().toString().trim();
        String replace;
        String replace_bis;

        //Comment comment = new Comment(commentText, appName, userName);

        //Replace special characters
        replace = commentText.replace(".", "£");
        replace = replace.replace("$", "%");
        replace = replace.replace("[", "&");
        replace = replace.replace("]", "^");
        replace = replace.replace("#", "ç");
        replace = replace.replace("/", "§");

        replace_bis = userName.replace(".", "£");
        replace_bis = replace_bis.replace("$", "%");
        replace_bis = replace_bis.replace("[", "&");
        replace_bis = replace_bis.replace("]", "^");
        replace_bis = replace_bis.replace("#", "ç");
        replace_bis = replace_bis.replace("/", "§");


        databaseReference.child("apps").child(appName).child("comments").child(replace_bis).child(replace).setValue("0");


        //Reload the activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void follow() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentValue =  dataSnapshot.child("apps").child(appName).child("follower").getValue(String.class);
                if(currentValue != null) {
                    newValue = Integer.parseInt(currentValue);
                }
                newValue = newValue + 1;

                temp = dataSnapshot.child("users").child(userID).child("nFollowed").getValue(String.class);
                int number = Integer.parseInt(temp);
                number = number + 1;
                temp = Integer.toString(number);

                dataSnapshot.child("apps").child(appName).getRef().child("follower").setValue(Integer.toString(newValue));
                dataSnapshot.child("users").child(userID).getRef().child("nFollowed").setValue(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(userID).child("followed_apps").child(appName).setValue(appName);

        //Reload the activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void unfollow() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentValue =  dataSnapshot.child("apps").child(appName).child("follower").getValue(String.class);
                if(currentValue != null) {
                    newValue = Integer.parseInt(currentValue);
                }
                else {
                    Toast.makeText(AppHome.this, "Error : follower number not available", Toast.LENGTH_SHORT).show();
                }
                newValue = newValue - 1;

                temp = dataSnapshot.child("users").child(userID).child("nFollowed").getValue(String.class);
                int number = Integer.parseInt(temp);
                number = number - 1;
                temp = Integer.toString(number);

                dataSnapshot.child("apps").child(appName).getRef().child("follower").setValue(Integer.toString(newValue));
                dataSnapshot.child("users").child(userID).getRef().child("nFollowed").setValue(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(userID).child("followed_apps").child(appName).removeValue();

        //Reload the activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void rate() {
        final String value = rateSpinner.getSelectedItem().toString().trim();
        final int v = Integer.parseInt(value);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentSum = dataSnapshot.child("apps").child(appName).child("rate").child("sum_rate").getValue(String.class);
                currentNumber = dataSnapshot.child("apps").child(appName).child("rate").child("total_rate").getValue(String.class);
                if(currentRate != null && currentSum != null && currentNumber != null) {
                    newSum = Double.parseDouble(currentSum);
                    newNumber = Integer.parseInt(currentNumber);
                }
                else {
                    Toast.makeText(AppHome.this, "Error : rate not available", Toast.LENGTH_SHORT).show();
                }

                newNumber = newNumber + 1;
                newSum = newSum + v;
                newRate = newSum/newNumber;

                String two_decimal = String.format("%.2f", newRate);

                dataSnapshot.child("apps").child(appName).child("rate").child("total_rate").getRef().setValue(Integer.toString(newNumber));
                dataSnapshot.child("apps").child(appName).child("rate").child("sum_rate").getRef().setValue(Double.toString(newSum));
                dataSnapshot.child("apps").child(appName).child("rate").child("average_rate").getRef().setValue(two_decimal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(userID).child("rated_apps").child(appName).setValue(appName);

        //Reload the activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private class MyTask extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            //GET USER
            if(firebaseAuth.getCurrentUser() == null) {
                finish();
                startActivity(new Intent(AppHome.this, SignIn.class));
            }
            //Get the name of user logged in
            else {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.child("users").child(userID).getValue(User.class);
                        if(user != null) {
                            userName = user.getName();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            //GET DEV
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Developer developer = dataSnapshot.child("apps").child(appName).child("developers").getValue(Developer.class);
                    if(developer != null) {
                        listDeveloperTextView.setText(developer.user_name);
                    }
                    else {
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //GET FOLLOW
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    number = dataSnapshot.child("apps").child(appName).child("follower").getValue(String.class);
                    if(number != null) {
                        numberFollowTextView.setText(number);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //GET RATE
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("apps").child(appName).hasChild("rate")) {
                        String rate = dataSnapshot.child("apps").child(appName).child("rate").child("average_rate").getValue(String.class);
                        if(!rate.equals("0")) {
                            rateValueTextView.setText(rate);
                        }
                        else {
                            rateValueTextView.setText("-");
                        }
                    }
                    else {
                        Toast.makeText(AppHome.this, "Error : rate value not available", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            return null;
        }
    }

    private class CommentTask extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            //PRINT COMMENTS
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("apps").child(appName).hasChild("comments")) {
                        //Recupero il creatore di un commento ed tutti i suoi relativi commenti
                        //for(DataSnapshot ds : dataSnapshot.child("apps").child(appName).child("comments").getChildren()) {
                            collectCommentsCreator((Map<String, Object>) dataSnapshot.child("apps").child(appName).child("comments").getValue());
                        //}
                    }
                    else {
                        //Se non ha commenti è più giusto non fare nulla
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            return null;
        }
    }


    /*
    private void collectCommentsCreator(Map<String,Object> cc) {
        for(Map.Entry<String,Object> entry : cc.entrySet()) {
            commentCreator = entry.getKey();

            //Per ogni user che ha commentato, recupero tutti i commenti
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Toast.makeText(AppHome.this, commentCreator, Toast.LENGTH_SHORT).show();
                    Map<String,Object> temp = (Map<String,Object>) dataSnapshot.child("apps").child(appName).child("comments").child(commentCreator).getValue();
                    for(Map.Entry<String,Object> entry : temp.entrySet()) {
                        singleComment = entry.getKey();
                        like = (String) entry.getValue();

                        //Toast.makeText(AppHome.this, "" + temp.entrySet(), Toast.LENGTH_SHORT).show();

                        for(DataSnapshot ds : dataSnapshot.child("users").child(userID).child("liked_comments").child(appName).getChildren()) {
                            if(ds.getValue(String.class).equals(singleComment)) {
                                likeString = "Unlike";
                                break;
                            }
                            else {
                                likeString = "Like";
                            }
                        }

                        rvCommentClass commentClass = new rvCommentClass(commentCreator, singleComment, like, likeString, appName);
                        commentCreatorList.add(commentClass);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
    */

    private void collectCommentsCreator(final Map<String,Object> cc) {

            //Per ogni user che ha commentato, recupero tutti i commenti
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(Map.Entry<String,Object> entry : cc.entrySet()) {
                        commentCreator = entry.getKey();

                        Map<String, Object> temp = (Map<String, Object>) dataSnapshot.child("apps").child(appName).child("comments").child(commentCreator).getValue();
                        for (Map.Entry<String, Object> e : temp.entrySet()) {
                            singleComment = e.getKey();
                            like = (String) e.getValue();

                            for (DataSnapshot ds : dataSnapshot.child("users").child(userID).child("liked_comments").child(appName).getChildren()) {
                                if (ds.getValue(String.class).equals(singleComment)) {
                                    likeString = "Unlike";
                                    break;
                                } else {
                                    likeString = "Like";
                                }
                            }

                            rvCommentClass commentClass = new rvCommentClass(commentCreator, singleComment, like, likeString, appName);
                            commentCreatorList.add(commentClass);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
    }

}
