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
import com.example.techfix.features.admin.data.DeviceModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminManageModelsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DeviceModelAdapter adapter;
    private DatabaseHelper dbHelper;
    
    private List<Brand> allBrands = new ArrayList<>();
    private List<String> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_models);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvModels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadInitialData();
        loadData();

        FloatingActionButton fab = findViewById(R.id.fabAddModel);
        fab.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadInitialData() {
        allBrands.clear();
        categories.clear();
        
        // Load all brands
        try (Cursor cursor = dbHelper.getAllBrands()) {
            while (cursor.moveToNext()) {
                allBrands.add(new Brand(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BRAND_CATEGORY))
                ));
            }
        }

        // Load all service categories
        try (Cursor cursor = dbHelper.getAllServiceCategories()) {
            while (cursor.moveToNext()) {
                categories.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
            }
        }
    }

    private void loadData() {
        List<DeviceModel> modelList = new ArrayList<>();
        for (Brand b : allBrands) {
            try (Cursor cursor = dbHelper.getModelsByBrand(b.getId())) {
                while (cursor.moveToNext()) {
                    modelList.add(new DeviceModel(
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MODEL_BRAND_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)),
                            b.getName()
                    ));
                }
            }
        }
        
        adapter = new DeviceModelAdapter(modelList, new DeviceModelAdapter.OnModelActionListener() {
            @Override
            public void onEdit(DeviceModel model) {
                showAddEditDialog(model);
            }

            @Override
            public void onDelete(DeviceModel model) {
                new AlertDialog.Builder(AdminManageModelsActivity.this)
                        .setTitle("Delete Model")
                        .setMessage("Are you sure you want to delete " + model.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (dbHelper.deleteModel(model.getId())) {
                                loadData();
                                Toast.makeText(AdminManageModelsActivity.this, "Model deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddEditDialog(DeviceModel model) {
        if (categories.isEmpty()) {
            Toast.makeText(this, "Please add a category first", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(model == null ? "Add Model" : "Edit Model");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_model, null);
        AutoCompleteTextView autoCategory = view.findViewById(R.id.autoModelCategory);
        AutoCompleteTextView autoBrand = view.findViewById(R.id.autoModelBrand);
        EditText etName = view.findViewById(R.id.etModelName);

        // Setup Category dropdown
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        autoCategory.setAdapter(catAdapter);

        // Setup Brand dropdown logic
        Map<String, Integer> currentBrandNameIdMap = new HashMap<>();
        autoCategory.setOnItemClickListener((parent, v, position, id) -> {
            String selectedCat = (String) parent.getItemAtPosition(position);
            updateBrandDropdown(autoBrand, selectedCat, currentBrandNameIdMap);
        });

        if (model != null) {
            etName.setText(model.getName());
            // Find current brand and its category
            Brand currentBrand = null;
            for (Brand b : allBrands) {
                if (b.getId() == model.getBrandId()) {
                    currentBrand = b;
                    break;
                }
            }
            if (currentBrand != null) {
                autoCategory.setText(currentBrand.getCategory(), false);
                updateBrandDropdown(autoBrand, currentBrand.getCategory(), currentBrandNameIdMap);
                autoBrand.setText(currentBrand.getName(), false);
            }
        }

        builder.setView(view);
        builder.setPositiveButton(model == null ? "Add" : "Update", (dialog, which) -> {
            String selectedBrandStr = autoBrand.getText().toString().trim();
            String name = etName.getText().toString().trim();

            if (!selectedBrandStr.isEmpty() && !name.isEmpty()) {
                Integer brandId = currentBrandNameIdMap.get(selectedBrandStr);
                if (brandId != null) {
                    boolean success;
                    if (model == null) {
                        success = dbHelper.addModel(brandId, name);
                    } else {
                        success = dbHelper.updateModel(model.getId(), brandId, name);
                    }

                    if (success) {
                        loadData();
                        Toast.makeText(this, model == null ? "Model added" : "Model updated", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Please select category, brand and enter model name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateBrandDropdown(AutoCompleteTextView autoBrand, String category, Map<String, Integer> nameIdMap) {
        nameIdMap.clear();
        List<String> brandNames = new ArrayList<>();
        for (Brand b : allBrands) {
            if (b.getCategory().equalsIgnoreCase(category)) {
                brandNames.add(b.getName());
                nameIdMap.put(b.getName(), b.getId());
            }
        }
        autoBrand.setText(""); // Reset brand when category changes
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brandNames);
        autoBrand.setAdapter(brandAdapter);
    }
}
