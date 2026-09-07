package com.example.techfix.features.branches.ui;

import android.content.Intent;
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
import com.example.techfix.common.sync.FirebaseSyncRepository;
import com.example.techfix.features.admin.ui.TechnicianEditActivity;
import com.example.techfix.features.branches.data.Branch;
import com.example.techfix.features.branches.data.Technician;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ManageTechniciansActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TechnicianAdapter adapter;
    private DatabaseHelper dbHelper;
    private FirebaseSyncRepository syncRepo;
    private List<Technician> technicianList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_technicians);

        dbHelper = new DatabaseHelper(this);
        syncRepo = new FirebaseSyncRepository(this);
        recyclerView = findViewById(R.id.rvTechnicians);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.fabAddTechnician).setOnClickListener(v -> showAddDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        technicianList = dbHelper.getAllTechnicians();
        adapter = new TechnicianAdapter(technicianList, new TechnicianAdapter.OnTechnicianActionListener() {
            @Override
            public void onEdit(Technician technician) {
                Intent intent = new Intent(ManageTechniciansActivity.this, TechnicianEditActivity.class);
                intent.putExtra("TECH_DATA", technician);
                startActivity(intent);
            }

            @Override
            public void onDelete(Technician technician) {
                new AlertDialog.Builder(ManageTechniciansActivity.this)
                        .setTitle("Delete Technician")
                        .setMessage("Are you sure you want to delete " + technician.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (dbHelper.deleteTechnician(technician.getId())) {
                                FirebaseFirestore.getInstance()
                                        .collection("technicians")
                                        .document(String.valueOf(technician.getId()))
                                        .delete();
                                loadData();
                                Toast.makeText(ManageTechniciansActivity.this, "Technician deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        }, dbHelper);
        recyclerView.setAdapter(adapter);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Technician");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_technician, null);
        EditText etName = view.findViewById(R.id.etTechName);
        AutoCompleteTextView autoBranch = view.findViewById(R.id.autoTechBranch);

        List<Branch> branches = dbHelper.getAllBranches();
        List<String> branchNames = new ArrayList<>();
        for (Branch b : branches) branchNames.add(b.getName());
        autoBranch.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, branchNames));

        builder.setView(view);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String branch = autoBranch.getText().toString().trim();

            if (!name.isEmpty() && !branch.isEmpty()) {
                if (dbHelper.addTechnician(name, branch, "Available")) {
                    List<Technician> all = dbHelper.getAllTechnicians();
                    for (Technician t : all) {
                        if (t.getName().equals(name) && t.getBranchName().equals(branch)) {
                            syncRepo.syncTechnician(t);
                            break;
                        }
                    }
                    loadData();
                    Toast.makeText(this, "Technician added", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
