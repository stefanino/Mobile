package com.example.stefano.spinup20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity implements View.OnClickListener{

    private Button login_button;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        login_button = findViewById(R.id.sign_in);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        pd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), UserHome.class));
        }

        login_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == login_button) {
            user_login();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void user_login() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(TextUtils.isEmpty(email)) {
            //Email is empty
            Toast.makeText(this, "Error, please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        else if(!(email.matches(emailPattern))) {
            Toast.makeText(this, "Error email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            //Password is empty
            Toast.makeText(this, "Error, please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        pd.setMessage("Authenticating user...");
        pd.show();

        //Start the login

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    pd.dismiss();
                    if(task.isSuccessful()) {
                        finish(); //End this activity, before start next one
                        startActivity(new Intent(getApplicationContext(), UserHome.class));
                    }
                    else {
                        Toast.makeText(SignIn.this, "Wrong mail or password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    /*
    private void developer_login() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(TextUtils.isEmpty(email)) {
            //Email is empty
            Toast.makeText(this, "Error, please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        else if(!(email.matches(emailPattern))) {
            Toast.makeText(this, "Error email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            //Password is empty
            Toast.makeText(this, "Error, please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        pd.setMessage("Authenticating user...");
        pd.show();

        //Start the login

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();
                        if(task.isSuccessful()) {
                            finish(); //End this activity, before start next one
                            startActivity(new Intent(getApplicationContext(), UserHome.class));
                        }
                    }
                });
    }
    */
}
