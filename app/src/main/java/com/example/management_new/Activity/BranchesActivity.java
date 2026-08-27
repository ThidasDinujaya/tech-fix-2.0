package com.example.management_new.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.management_new.R;
import com.example.management_new.Adapter.BranchAdapter;
import com.example.management_new.Database.DatabaseHelper;
import com.example.management_new.model.Branch;

import java.util.List;

public class BranchesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BranchAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);

        recyclerView = findViewById(R.id.recyclerViewBranches);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        List<Branch> branchList = dbHelper.getAllBranches();

        adapter = new BranchAdapter(branchList);
        recyclerView.setAdapter(adapter);
    }
}