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
import com.example.techfix.features.admin.data.ServiceCategory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class AdminManageCategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServiceCategoryAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_categories);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        FloatingActionButton fab = findViewById(R.id.fabAddCategory);
        fab.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadData() {
        List<ServiceCategory> categoryList = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllServiceCategories()) {
            while (cursor.moveToNext()) {
                categoryList.add(new ServiceCategory(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME))
                ));
            }
        }
        adapter = new ServiceCategoryAdapter(categoryList, new ServiceCategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEdit(ServiceCategory category) {
                showAddEditDialog(category);
            }

            @Override
            public void onDelete(ServiceCategory category) {
                new AlertDialog.Builder(AdminManageCategoriesActivity.this)
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete " + category.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (dbHelper.deleteServiceCategory(category.getId())) {
                                loadData();
                                Toast.makeText(AdminManageCategoriesActivity.this, "Category deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddEditDialog(ServiceCategory category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(category == null ? "Add Category" : "Edit Category");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_role, null); // Reuse role dialog layout
        EditText etName = view.findViewById(R.id.etRoleName);
        etName.setHint("Category Name (e.g. Phone)");

        if (category != null) {
            etName.setText(category.getName());
        }

        builder.setView(view);
        builder.setPositiveButton(category == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();

            if (!name.isEmpty()) {
                boolean success;
                if (category == null) {
                    success = dbHelper.addServiceCategory(name);
                } else {
                    success = dbHelper.updateServiceCategory(category.getId(), name);
                }

                if (success) {
                    loadData();
                    Toast.makeText(this, category == null ? "Category added" : "Category updated", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
