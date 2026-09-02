package com.example.techfix.features.branches.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.branches.data.Technician;

import java.util.List;

public class ManageTechniciansActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TechnicianAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Technician> technicianList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_technicians);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvTechnicians);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        findViewById(R.id.fabAddTechnician).setOnClickListener(v -> showAddTechnicianDialog());
    }

    private void loadData() {
        technicianList = dbHelper.getAllTechnicians();
        adapter = new TechnicianAdapter(technicianList);
        recyclerView.setAdapter(adapter);
    }

    private void showAddTechnicianDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Technician");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_technician, null);
        EditText etName = view.findViewById(R.id.etTechName);
        EditText etRole = view.findViewById(R.id.etTechRole);
        EditText etBranch = view.findViewById(R.id.etTechBranch);

        builder.setView(view);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String role = etRole.getText().toString().trim();
            String branch = etBranch.getText().toString().trim();

            if (!name.isEmpty() && !role.isEmpty()) {
                dbHelper.addTechnician(name, role, branch.isEmpty() ? "Colombo Branch" : branch, "Available");
                loadData();
                Toast.makeText(this, "Technician added successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
