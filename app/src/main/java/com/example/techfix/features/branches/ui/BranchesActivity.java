package com.example.techfix.features.branches.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);

        isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        recyclerView = findViewById(R.id.recyclerViewBranches);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        loadData();

        FloatingActionButton fab = findViewById(R.id.fabAddBranch);
        if (isAdmin) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> showAddEditDialog(null));
        } else {
            fab.setVisibility(View.GONE);
        }
        
        Toolbar toolbar = findViewById(R.id.toolbarBranches);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadData() {
        List<Branch> branchList = dbHelper.getAllBranches();
        adapter = new BranchAdapter(branchList, isAdmin, new BranchAdapter.OnBranchActionListener() {
            @Override
            public void onEdit(Branch branch) {
                if (isAdmin) showAddEditDialog(branch);
            }

            @Override
            public void onDelete(Branch branch) {
                if (isAdmin) {
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
        EditText etPhone2 = view.findViewById(R.id.etBranchPhone2);
        EditText etMonFri = view.findViewById(R.id.etHoursMonFri);
        EditText etSat = view.findViewById(R.id.etHoursSat);
        EditText etSun = view.findViewById(R.id.etHoursSun);
        EditText etLat = view.findViewById(R.id.etBranchLat);
        EditText etLon = view.findViewById(R.id.etBranchLon);

        if (branch != null) {
            etName.setText(branch.getName());
            etAddr.setText(branch.getAddress());
            etPhone.setText(branch.getPhone());
            etPhone2.setText(branch.getPhone2());
            etMonFri.setText(branch.getHoursMonFri());
            etSat.setText(branch.getHoursSat());
            etSun.setText(branch.getHoursSun());
            etLat.setText(String.valueOf(branch.getLatitude()));
            etLon.setText(String.valueOf(branch.getLongitude()));
        }

        builder.setView(view);
        builder.setPositiveButton(branch == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String addr = etAddr.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String phone2 = etPhone2.getText().toString().trim();
            String monFri = etMonFri.getText().toString().trim();
            String sat = etSat.getText().toString().trim();
            String sun = etSun.getText().toString().trim();
            String latStr = etLat.getText().toString().trim();
            String lonStr = etLon.getText().toString().trim();

            if (!name.isEmpty() && !addr.isEmpty() && !phone.isEmpty()) {
                try {
                    double lat = latStr.isEmpty() ? 0.0 : Double.parseDouble(latStr);
                    double lon = lonStr.isEmpty() ? 0.0 : Double.parseDouble(lonStr);
                    
                    boolean success;
                    if (branch == null) {
                        success = dbHelper.addBranch(name, addr, phone, phone2, monFri, sat, sun, lat, lon);
                    } else {
                        success = dbHelper.updateBranch(branch.getId(), name, addr, phone, phone2, monFri, sat, sun, lat, lon);
                    }

                    if (success) {
                        loadData();
                        Toast.makeText(this, branch == null ? "Branch added" : "Branch updated", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid coordinate format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Name, Address, and Phone are required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
