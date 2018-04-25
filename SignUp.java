package com.example.stefano.spinup20;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private Button register_button;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser fbUser;

    private GoogleSignInAccount profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        register_button = findViewById(R.id.signup);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextName = findViewById(R.id.name);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        profile = GoogleSignIn.getLastSignedInAccount(this);

        if(firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), UserHome.class));
        }

        register_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == register_button) {
                registerUser();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void registerUser() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String name = editTextName.getText().toString().trim();

        if(!checkInput(email, password, name)) {
            return;
        }

        //Check if the username already exists
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.child("name").getValue(String.class).equals(name)) {
                        Toast.makeText(SignUp.this, "Failed to create user : username already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //Here no user has that username : start authentication

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    //User is successfully registered and logged in

                                    writeNewUser(name);

                                    Toast.makeText(SignUp.this, "User Successfully Registered", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), UserHome.class));
                                }
                                else {
                                    //Error during registration
                                    Toast.makeText(SignUp.this, "Failed to create user : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void writeNewUser(String name) {
        User user = new User(name, null, null);
        fbUser = firebaseAuth.getCurrentUser();

        databaseReference.child("users").child(fbUser.getUid()).setValue(user);
        databaseReference.child("users").child(fbUser.getUid()).child("nCreated").setValue("0");
        databaseReference.child("users").child(fbUser.getUid()).child("nFollowed").setValue("0");
    }

    private boolean checkInput(String email, String password, String name) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(TextUtils.isEmpty(name)) {
            //Name is empty
            editTextName.setError("The item cannot be empty");
            return false;
        }

        /*
        if(!name.matches("[a-zA-Z]+")) {
            editTextName.setError("Please enter a valid name");
            return false;
        }
        */

        if(name.length() > 15) {
            editTextName.setError("The name is too long");
            return false;
        }

        if(name.length() < 2) {
            editTextName.setError("The name is too short");
            return false;
        }

        if(TextUtils.isEmpty(email)) {
            //Email is empty
            editTextEmail.setError("The item cannot be empty");
            return false;
        }

        else if(!(email.matches(emailPattern))) {
            editTextEmail.setError("Please enter a mail with a valid format");
            return false;
        }

        if(TextUtils.isEmpty(password)) {
            //Password is empty
            editTextPassword.setError("The item cannot be empty");
            return false;
        }

        if(password.length() < 6) {
            editTextPassword.setError("The password must contain at least 6 characters");
            return false;
        }

        return true;
    }
}
