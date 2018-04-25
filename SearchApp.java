package com.example.stefano.spinup20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchApp extends AppCompatActivity implements View.OnClickListener{

    private EditText editText_searchAppName;
    private Spinner spinner_searchAppCategory;
    private Button searchApp_button;

    private List<rvAppClass> appList;
    private List<String> appSearch;

    private String s;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_app);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        editText_searchAppName = findViewById(R.id.searchAppName);
        spinner_searchAppCategory = findViewById(R.id.searchAppCategory);
        searchApp_button = findViewById(R.id.searchApp);

        appList = new ArrayList<>();
        appSearch = new ArrayList<>();

        s = "";

        searchApp_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == searchApp_button) {
            searchApp();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(), UserHome.class));
    }

    private void searchApp() {
        final String searchName = editText_searchAppName.getText().toString().trim();
        final String searchCategory = spinner_searchAppCategory.getSelectedItem().toString().trim();

        if(TextUtils.isEmpty(searchName) && searchCategory.equals("-")) {
            Toast.makeText(this, "Please, select at least one of the two fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if(searchCategory.equals("-")) {
            databaseReference.child("apps").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        s = ds.child("app_name").getValue(String.class);
                        if(s == null) {
                            Toast.makeText(SearchApp.this, "Error on app read", Toast.LENGTH_SHORT).show();
                        }
                        if(s.equals(searchName)) {
                            break;
                            /*
                            Intent intent = new Intent(SearchApp.this, AppHome.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("appName", s);
                            intent.putExtras(bundle);

                            startActivity(intent);
                            finish();

                            appSearch.add(s);
                            */
                        }
                    }
                    if(s.equals(searchName)) {
                        Intent intent = new Intent(SearchApp.this, AppHome.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("appName", s);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        finish();

                        appSearch.add(s);
                    }
                    else {
                        Toast.makeText(SearchApp.this, "No app matched", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        if(TextUtils.isEmpty(searchName)) {
            databaseReference.child("apps").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        String c = ds.child("category").getValue(String.class);
                        String a = ds.child("app_name").getValue(String.class);
                        if(c == null) {
                            Toast.makeText(SearchApp.this, "Error on app read", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(c.equals(searchCategory)) {
                                rvAppClass app = new rvAppClass(a, c);
                                appList.add(app);
                            }
                        }
                    }
                    if(appList.size() != 0) {
                        Intent intent = new Intent(SearchApp.this, SearchByCategory.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("appList", (Serializable)appList);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(SearchApp.this, "No app matched", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        if(!TextUtils.isEmpty(searchName) && !searchCategory.equals("-")) {
            databaseReference.child("apps").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String c = "";
                    String a = "";
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        c = ds.child("category").getValue(String.class);
                        a = ds.child("app_name").getValue(String.class);
                        if(a == null) {
                            Toast.makeText(SearchApp.this, "Error on app read", Toast.LENGTH_SHORT).show();
                        }
                        if(a.equals(searchName) && c.equals(searchCategory)) {
                            break;
                            /*
                            Intent intent = new Intent(SearchApp.this, AppHome.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("appName", a);
                            intent.putExtras(bundle);

                            startActivity(intent);
                            finish();
                            */
                        }
                    }
                    if(a.equals(searchName) && c.equals(searchCategory)) {
                        Intent intent = new Intent(SearchApp.this, AppHome.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("appName", a);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(SearchApp.this, "No app matched", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
}
