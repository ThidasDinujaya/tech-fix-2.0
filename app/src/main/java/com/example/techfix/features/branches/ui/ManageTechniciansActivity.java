package com.example.techfix.features.branches.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.admin.ui.AdminManageRolesActivity;
import com.example.techfix.features.branches.data.Branch;
import com.example.techfix.features.branches.data.Technician;

import java.util.ArrayList;
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

        findViewById(R.id.fabAddTechnician).setOnClickListener(v -> showAddEditDialog(null));
        findViewById(R.id.btnManageRoles).setOnClickListener(v -> 
            startActivity(new Intent(this, AdminManageRolesActivity.class)));
    }

    private void loadData() {
        technicianList = dbHelper.getAllTechnicians();
        adapter = new TechnicianAdapter(technicianList, new TechnicianAdapter.OnTechnicianActionListener() {
            @Override
            public void onEdit(Technician technician) {
                showAddEditDialog(technician);
            }

            @Override
            public void onDelete(Technician technician) {
                new AlertDialog.Builder(ManageTechniciansActivity.this)
                        .setTitle("Delete Technician")
                        .setMessage("Are you sure you want to delete " + technician.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (dbHelper.deleteTechnician(technician.getId())) {
                                loadData();
                                Toast.makeText(ManageTechniciansActivity.this, "Technician deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddEditDialog(Technician tech) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(tech == null ? "Add New Technician" : "Edit Technician");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_technician, null);
        EditText etName = view.findViewById(R.id.etTechName);
        AutoCompleteTextView autoRole = view.findViewById(R.id.autoTechRole);
        AutoCompleteTextView autoBranch = view.findViewById(R.id.autoTechBranch);

        // Fetch current branches
        List<Branch> branches = dbHelper.getAllBranches();
        List<String> branchNames = new ArrayList<>();
        for (Branch b : branches) {
            branchNames.add(b.getName());
        }
        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, branchNames);
        autoBranch.setAdapter(branchAdapter);

        // Fetch current roles
        List<String> roleNames = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllRoles()) {
            while (cursor.moveToNext()) {
                roleNames.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
            }
        }
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roleNames);
        autoRole.setAdapter(roleAdapter);

        if (tech != null) {
            etName.setText(tech.getName());
            autoRole.setText(tech.getRole(), false);
            autoBranch.setText(tech.getBranchName(), false);
        }

        builder.setView(view);
        builder.setPositiveButton(tech == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String role = autoRole.getText().toString().trim();
            String branch = autoBranch.getText().toString().trim();

            if (!name.isEmpty() && !role.isEmpty() && !branch.isEmpty()) {
                boolean success;
                if (tech == null) {
                    success = dbHelper.addTechnician(name, role, branch, "Available");
                } else {
                    success = dbHelper.updateTechnician(tech.getId(), name, role, branch, tech.getStatus());
                }

                if (success) {
                    loadData();
                    Toast.makeText(this, tech == null ? "Technician added" : "Technician updated", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
