package com.example.techfix.features.admin.ui;

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
import com.example.techfix.features.admin.data.Brand;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class AdminManageBrandsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BrandAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_brands);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvBrands);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        FloatingActionButton fab = findViewById(R.id.fabAddBrand);
        fab.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadData() {
        List<Brand> brandList = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllBrands()) {
            while (cursor.moveToNext()) {
                brandList.add(new Brand(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BRAND_CATEGORY))
                ));
            }
        }
        adapter = new BrandAdapter(brandList, new BrandAdapter.OnBrandActionListener() {
            @Override
            public void onEdit(Brand brand) {
                showAddEditDialog(brand);
            }

            @Override
            public void onDelete(Brand brand) {
                new AlertDialog.Builder(AdminManageBrandsActivity.this)
                        .setTitle("Delete Brand")
                        .setMessage("Are you sure you want to delete " + brand.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (dbHelper.deleteBrand(brand.getId())) {
                                loadData();
                                Toast.makeText(AdminManageBrandsActivity.this, "Brand deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddEditDialog(Brand brand) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(brand == null ? "Add Brand" : "Edit Brand");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_brand, null);
        EditText etName = view.findViewById(R.id.etBrandName);
        AutoCompleteTextView autoCategory = view.findViewById(R.id.autoBrandCategory);

        String[] categories = {"Phone", "Laptop", "Desktop", "Tablet"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        autoCategory.setAdapter(catAdapter);

        if (brand != null) {
            etName.setText(brand.getName());
            autoCategory.setText(brand.getCategory(), false);
        }

        builder.setView(view);
        builder.setPositiveButton(brand == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String category = autoCategory.getText().toString().trim();

            if (!name.isEmpty() && !category.isEmpty()) {
                boolean success;
                if (brand == null) {
                    success = dbHelper.addBrand(name, category);
                } else {
                    success = dbHelper.updateBrand(brand.getId(), name, category);
                }

                if (success) {
                    loadData();
                    Toast.makeText(this, brand == null ? "Brand added" : "Brand updated", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
