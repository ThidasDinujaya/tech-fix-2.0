package com.example.techfix.features.admin.ui;

import android.database.Cursor;
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
import com.example.techfix.features.admin.data.TechnicianRole;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class AdminManageRolesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TechnicianRoleAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_roles);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvRoles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        FloatingActionButton fab = findViewById(R.id.fabAddRole);
        fab.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadData() {
        List<TechnicianRole> roleList = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllRoles()) {
            while (cursor.moveToNext()) {
                roleList.add(new TechnicianRole(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME))
                ));
            }
        }
        adapter = new TechnicianRoleAdapter(roleList, new TechnicianRoleAdapter.OnRoleActionListener() {
            @Override
            public void onEdit(TechnicianRole role) {
                showAddEditDialog(role);
            }

            @Override
            public void onDelete(TechnicianRole role) {
                new AlertDialog.Builder(AdminManageRolesActivity.this)
                        .setTitle("Delete Role")
                        .setMessage("Are you sure you want to delete " + role.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (dbHelper.deleteRole(role.getId())) {
                                loadData();
                                Toast.makeText(AdminManageRolesActivity.this, "Role deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddEditDialog(TechnicianRole role) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(role == null ? "Add Role" : "Edit Role");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_role, null);
        EditText etName = view.findViewById(R.id.etRoleName);

        if (role != null) {
            etName.setText(role.getName());
        }

        builder.setView(view);
        builder.setPositiveButton(role == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();

            if (!name.isEmpty()) {
                boolean success;
                if (role == null) {
                    success = dbHelper.addRole(name);
                } else {
                    success = dbHelper.updateRole(role.getId(), name);
                }

                if (success) {
                    loadData();
                    Toast.makeText(this, role == null ? "Role added" : "Role updated", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a role name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
