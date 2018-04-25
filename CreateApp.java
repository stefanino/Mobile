package com.example.stefano.spinup20;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class CreateApp extends AppCompatActivity implements View.OnClickListener{

    private EditText app_name;
    private Spinner app_category;
    private Button appCreation_button;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser fbUser;

    private GoogleSignInAccount profile;
    private String userID;

    private String temp;

    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_app);

        app_name = findViewById(R.id.app_name);
        app_category = findViewById(R.id.app_category);
        appCreation_button = findViewById(R.id.app_creation);

        firebaseAuth = firebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fbUser = firebaseAuth.getCurrentUser();

        profile = GoogleSignIn.getLastSignedInAccount(this);
        userID = "";

        temp = "0";

        flag = true;

        checkWhoSignIn();

        appCreation_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == appCreation_button) {
            createApp();
        }
    }

    public void createApp() {
        String name = app_name.getText().toString().trim();
        String category = app_category.getSelectedItem().toString().trim();

        String replace;

        replace = name.replace(".", "£");
        replace = replace.replace("$", "%");
        replace = replace.replace("[", "&");
        replace = replace.replace("]", "^");
        replace = replace.replace("#", "ç");
        replace = replace.replace("/", "§");

        checkInput(replace, category);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(), UserHome.class));
    }

    private void write_new_app(final String name, String category) {
        App app = new App(name, category, "0");

        //Get Name of the user. This is useful to modify the data
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.child("users").child(userID).getValue(User.class);
                if(user != null) {
                    databaseReference.child("apps").child(name).child("developers").child("user_name").setValue(user.getName());
                }
                temp = dataSnapshot.child("users").child(userID).child("nCreated").getValue(String.class);
                int number = Integer.parseInt(temp);
                number = number + 1;
                temp = Integer.toString(number);
                databaseReference.child("users").child(userID).child("nCreated").setValue(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("apps").child(name).setValue(app);
        databaseReference.child("apps").child(name).child("follower").setValue("0");
        databaseReference.child("apps").child(name).child("rate").child("average_rate").setValue("0");
        databaseReference.child("apps").child(name).child("rate").child("sum_rate").setValue("0");
        databaseReference.child("apps").child(name).child("rate").child("total_rate").setValue("0");
        databaseReference.child("users").child(userID).child("developed_apps").child(name).setValue(name);
    }

    private void checkInput(final String name, final String category) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.child("apps").getChildren()) {
                    if(ds.child("app_name").getValue(String.class).equals(name)) {
                        flag = false;
                        Toast.makeText(CreateApp.this, "App name already exist : please choose another name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(TextUtils.isEmpty(name)) {
                    flag = false;
                    app_name.setError("The item cannot be empty");
                    return;
                }

                if(name.length() < 2) {
                    flag = false;
                    app_name.setError("App name too short");
                    return;
                }

                if(name.length() > 20) {
                    flag = false;
                    app_name.setError("App name too long");
                    return;
                }
                write_new_app(name, category);

                //Create the Intent to start a new activity and a Bundle to pass the name of the app as parameter
                Intent intent = new Intent(CreateApp.this, AppHome.class);
                Bundle bundle = new Bundle();
                bundle.putString("appName", name);
                intent.putExtras(bundle);

                startActivity(intent);
                finish();
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
}
