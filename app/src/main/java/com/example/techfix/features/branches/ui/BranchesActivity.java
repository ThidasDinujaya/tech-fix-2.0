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
import com.example.techfix.features.branches.data.Branch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        loadData();

        FloatingActionButton fab = findViewById(R.id.fabAddBranch);
        fab.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadData() {
        List<Branch> branchList = dbHelper.getAllBranches();
        adapter = new BranchAdapter(branchList, new BranchAdapter.OnBranchActionListener() {
            @Override
            public void onEdit(Branch branch) {
                showAddEditDialog(branch);
            }

            @Override
            public void onDelete(Branch branch) {
                new AlertDialog.Builder(BranchesActivity.this)
                        .setTitle("Delete Branch")
                        .setMessage("Are you sure you want to delete " + branch.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (dbHelper.deleteBranch(branch.getId())) {
                                loadData();
                                Toast.makeText(BranchesActivity.this, "Branch deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddEditDialog(Branch branch) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(branch == null ? "Add New Branch" : "Edit Branch");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_branch, null);
        EditText etName = view.findViewById(R.id.etBranchName);
        EditText etAddr = view.findViewById(R.id.etBranchAddress);
        EditText etPhone = view.findViewById(R.id.etBranchPhone);
        EditText etHours = view.findViewById(R.id.etBranchHours);

        if (branch != null) {
            etName.setText(branch.getName());
            etAddr.setText(branch.getAddress());
            etPhone.setText(branch.getPhone());
            etHours.setText(branch.getHours());
        }

        builder.setView(view);
        builder.setPositiveButton(branch == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String addr = etAddr.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String hours = etHours.getText().toString().trim();

            if (!name.isEmpty() && !addr.isEmpty()) {
                boolean success;
                if (branch == null) {
                    success = dbHelper.addBranch(name, addr, phone, hours, 0.0, 0.0);
                } else {
                    success = dbHelper.updateBranch(branch.getId(), name, addr, phone, hours, branch.getLatitude(), branch.getLongitude());
                }

                if (success) {
                    loadData();
                    Toast.makeText(this, branch == null ? "Branch added" : "Branch updated", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Name and Address are required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
