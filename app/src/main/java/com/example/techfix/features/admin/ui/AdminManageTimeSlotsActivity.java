package com.example.techfix.features.admin.ui;

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
import com.example.techfix.features.admin.data.TimeSlot;
import com.example.techfix.features.branches.data.Branch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminManageTimeSlotsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TimeSlotAdapter adapter;
    private DatabaseHelper dbHelper;
    private FirebaseSyncRepository syncRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_time_slots);

        dbHelper = new DatabaseHelper(this);
        syncRepo = new FirebaseSyncRepository(this);
        recyclerView = findViewById(R.id.rvTimeSlots);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        FloatingActionButton fab = findViewById(R.id.fabAddTimeSlot);
        fab.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadData() {
        List<TimeSlot> slotList = dbHelper.getAllTimeSlots();
        adapter = new TimeSlotAdapter(slotList, new TimeSlotAdapter.OnTimeSlotActionListener() {
            @Override
            public void onEdit(TimeSlot slot) {
                showAddEditDialog(slot);
            }

            @Override
            public void onDelete(TimeSlot slot) {
                new AlertDialog.Builder(AdminManageTimeSlotsActivity.this)
                        .setTitle("Delete Time Slot")
                        .setMessage("Are you sure you want to delete " + slot.getSlotName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (dbHelper.deleteTimeSlot(slot.getId())) {
                                syncRepo.deleteTimeSlot(slot.getId());
                                loadData();
                                Toast.makeText(AdminManageTimeSlotsActivity.this, "Time slot deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddEditDialog(TimeSlot slot) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(slot == null ? "Add Time Slot" : "Edit Time Slot");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_time_slot, null);
        EditText etSlotName = view.findViewById(R.id.etSlotName);
        AutoCompleteTextView autoBranch = view.findViewById(R.id.autoSlotBranch);
        AutoCompleteTextView autoStatus = view.findViewById(R.id.autoSlotStatus);

        // Load branches
        List<Branch> branches = dbHelper.getAllBranches();
        List<String> branchNames = new ArrayList<>();
        for (Branch b : branches) {
            branchNames.add(b.getName());
        }
        if (branchNames.isEmpty()) {
            branchNames.add("Colombo Main");
            branchNames.add("Kandy Branch");
        }
        autoBranch.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, branchNames));

        String[] statuses = {"Available", "Unavailable"};
        autoStatus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, statuses));

        if (slot != null) {
            etSlotName.setText(slot.getSlotName());
            autoBranch.setText(slot.getBranchName(), false);
            autoStatus.setText(slot.getStatus(), false);
        } else {
            autoBranch.setText("Colombo Main", false);
            autoStatus.setText("Available", false);
        }

        builder.setView(view);
        builder.setPositiveButton(slot == null ? "Add" : "Update", (dialog, which) -> {
            String name = etSlotName.getText().toString().trim();
            String branch = autoBranch.getText().toString().trim();
            String status = autoStatus.getText().toString().trim();

            if (branch.isEmpty()) {
                branch = "Colombo Main";
            }
            if (status.isEmpty()) {
                status = "Available";
            }

            if (!name.isEmpty()) {
                if (slot == null) {
                    long newId = dbHelper.addTimeSlot(name, status, branch);
                    if (newId != -1) {
                        syncRepo.syncTimeSlot(new TimeSlot((int) newId, name, status, branch));
                        loadData();
                        Toast.makeText(this, "Time slot added", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    boolean success = dbHelper.updateTimeSlot(slot.getId(), name, status, branch);
                    if (success) {
                        syncRepo.syncTimeSlot(new TimeSlot(slot.getId(), name, status, branch));
                        loadData();
                        Toast.makeText(this, "Time slot updated", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Please enter time slot name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
