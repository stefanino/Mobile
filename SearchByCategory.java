package com.example.stefano.spinup20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class SearchByCategory extends AppCompatActivity {

    private ArrayList<rvAppClass> appList;
    private RecyclerView recyclerView;
    private rvAdapterSearchCategory adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_category);

        appList = new ArrayList<>();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        appList = (ArrayList<rvAppClass>) bundle.getSerializable("appList");

        recyclerView = findViewById(R.id.myRecyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new rvAdapterSearchCategory(this, appList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(), UserHome.class));
    }
}
