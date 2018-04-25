package com.example.stefano.spinup20;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button sign_in_button;
    private Button sign_up_button;
    private com.google.android.gms.common.SignInButton google_button;

    private FirebaseAuth firebaseAuth;

    private Boolean exit = false;

    // ********** GOOGLE VARIABLES ***************

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sign_in_button = findViewById(R.id.sign_in);
        sign_up_button = findViewById(R.id.sign_up);
        google_button = findViewById(R.id.google);

        firebaseAuth = FirebaseAuth.getInstance();

        //******************************* GOOGLE CODE BEGIN ****************************************

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //******************************* GOOGLE CODE END ****************************************

        sign_in_button.setOnClickListener(this);
        sign_up_button.setOnClickListener(this);
        google_button.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null) {
            finish();
            userHome();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == sign_in_button) {
            sign_in();
        }

        if(view == sign_up_button) {
            sign_up();
        }

        if(view == google_button) {
            google_signIn();
        }
    }

    @Override
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

    private void sign_in() {
        Intent intent = new Intent(MainActivity.this, SignIn.class);
        finish();
        startActivity(intent);
    }

    private void sign_up() {
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        finish();
        startActivity(intent);
    }

    private void userHome() {
        Intent intent = new Intent(MainActivity.this, UserHome.class);
        finish();
        startActivity(intent);
    }

    //******************************* GOOGLE METHODS ****************************************

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                /*
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
                */
            }
        }
    }

    //Auth with Google method

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            finish();
                            userHome();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Google Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void google_signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
