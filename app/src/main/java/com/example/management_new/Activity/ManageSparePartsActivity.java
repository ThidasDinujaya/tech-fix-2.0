package com.example.management_new.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.management_new.Database.DatabaseHelper;
import com.example.management_new.R;
import com.example.management_new.Adapter.SparePartAdapter;
import com.example.management_new.model.SparePart;

import java.util.List;

public class ManageSparePartsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SparePartAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_spare_parts);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvSpareParts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<SparePart> partList = dbHelper.getAllSpareParts();
        adapter = new SparePartAdapter(partList);
        recyclerView.setAdapter(adapter);
    }
}