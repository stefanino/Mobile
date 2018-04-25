package com.example.stefano.spinup20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by Stefano on 07/04/18.
 */

public class SearchByFollow extends AppCompatActivity {
    private ArrayList<rvFollowedApp> appList;
    private RecyclerView recyclerView;
    private rvAdapterSearchFollowed adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_follow);

        appList = new ArrayList<>();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        appList = (ArrayList<rvFollowedApp>) bundle.getSerializable("appList");

        recyclerView = findViewById(R.id.myRecyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new rvAdapterSearchFollowed(this, appList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(), UserHome.class));
    }
}
