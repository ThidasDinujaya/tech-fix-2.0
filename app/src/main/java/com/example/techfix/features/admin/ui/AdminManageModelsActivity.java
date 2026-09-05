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
    private Map<String, Integer> brandNameIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_models);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvModels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadBrands();
        loadData();

        FloatingActionButton fab = findViewById(R.id.fabAddModel);
        fab.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadBrands() {
        allBrands.clear();
        brandNameIdMap.clear();
        try (Cursor cursor = dbHelper.getAllBrands()) {
            while (cursor.moveToNext()) {
                Brand b = new Brand(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BRAND_CATEGORY))
                );
                allBrands.add(b);
                brandNameIdMap.put(b.getName() + " (" + b.getCategory() + ")", b.getId());
            }
        }
    }

    private void loadData() {
        List<DeviceModel> modelList = new ArrayList<>();
        // Simple query joining brand name might be better, but we have allBrands in memory
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
        if (allBrands.isEmpty()) {
            Toast.makeText(this, "Please add a brand first", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(model == null ? "Add Model" : "Edit Model");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_model, null);
        AutoCompleteTextView autoBrand = view.findViewById(R.id.autoModelBrand);
        EditText etName = view.findViewById(R.id.etModelName);

        List<String> brandDisplayNames = new ArrayList<>(brandNameIdMap.keySet());
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brandDisplayNames);
        autoBrand.setAdapter(brandAdapter);

        if (model != null) {
            etName.setText(model.getName());
            // Find current brand display name
            for (String display : brandDisplayNames) {
                if (brandNameIdMap.get(display) == model.getBrandId()) {
                    autoBrand.setText(display, false);
                    break;
                }
            }
        }

        builder.setView(view);
        builder.setPositiveButton(model == null ? "Add" : "Update", (dialog, which) -> {
            String selectedBrandStr = autoBrand.getText().toString().trim();
            String name = etName.getText().toString().trim();

            if (!selectedBrandStr.isEmpty() && !name.isEmpty()) {
                Integer brandId = brandNameIdMap.get(selectedBrandStr);
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
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
