package com.example.techfix.features.branches.ui;

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
import com.example.techfix.features.branches.data.SparePart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageSparePartsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SparePartAdapter adapter;
    private DatabaseHelper dbHelper;
    private Map<String, Integer> brandIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_spare_parts);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvSpareParts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        FloatingActionButton fab = findViewById(R.id.fabAddSparePart);
        fab.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadData() {
        List<SparePart> partList = dbHelper.getAllSpareParts();
        adapter = new SparePartAdapter(partList, new SparePartAdapter.OnPartActionListener() {
            @Override
            public void onEdit(SparePart part) {
                showAddEditDialog(part);
            }

            @Override
            public void onDelete(SparePart part) {
                new AlertDialog.Builder(ManageSparePartsActivity.this)
                        .setTitle("Delete Part")
                        .setMessage("Are you sure you want to delete " + part.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (dbHelper.deleteSparePart(part.getId())) {
                                loadData();
                                Toast.makeText(ManageSparePartsActivity.this, "Part deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddEditDialog(SparePart part) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(part == null ? "Add New Part" : "Edit Part");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_spare_part, null);
        EditText etName = view.findViewById(R.id.etPartName);
        AutoCompleteTextView autoCategory = view.findViewById(R.id.autoPartCategory);
        AutoCompleteTextView autoBrand = view.findViewById(R.id.autoPartBrand);
        AutoCompleteTextView autoModel = view.findViewById(R.id.autoPartModel);
        AutoCompleteTextView autoQuality = view.findViewById(R.id.autoPartQuality);
        EditText etStock = view.findViewById(R.id.etPartStock);
        EditText etPrice = view.findViewById(R.id.etPartPrice);

        // Load Categories
        List<String> categories = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllServiceCategories()) {
            while (cursor.moveToNext()) {
                categories.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
            }
        }
        autoCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories));

        // Load Brands (Filtered by Category)
        autoCategory.setOnItemClickListener((parent, v, position, id) -> {
            String selectedCategory = (String) parent.getItemAtPosition(position);
            updateBrands(autoBrand, selectedCategory);
            autoModel.setText("");
        });

        // Handle Brand selection to update Model list
        autoBrand.setOnItemClickListener((parent, v, position, id) -> {
            String selectedBrand = (String) parent.getItemAtPosition(position);
            updateModels(autoModel, brandIdMap.get(selectedBrand));
        });

        // Load Qualities
        List<String> qualityNames = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllQualities()) {
            while (cursor.moveToNext()) {
                qualityNames.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
            }
        }
        autoQuality.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, qualityNames));

        if (part != null) {
            etName.setText(part.getName());
            autoBrand.setText(part.getBrand(), false);
            updateModels(autoModel, brandIdMap.get(part.getBrand()));
            autoModel.setText(part.getModel(), false);
            autoQuality.setText(part.getQuality(), false);
            etStock.setText(String.valueOf(part.getStock()));
            etPrice.setText(String.valueOf(part.getPrice()));
        }

        builder.setView(view);
        builder.setPositiveButton(part == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String brand = autoBrand.getText().toString().trim();
            String model = autoModel.getText().toString().trim();
            String quality = autoQuality.getText().toString().trim();
            String stockStr = etStock.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (!name.isEmpty() && !brand.isEmpty() && !model.isEmpty() && !quality.isEmpty() && !stockStr.isEmpty() && !priceStr.isEmpty()) {
                try {
                    int stock = Integer.parseInt(stockStr);
                    double price = Double.parseDouble(priceStr);
                    boolean success;
                    
                    if (part == null) {
                        success = dbHelper.addSparePart(name, stock, price, brand, model, quality);
                    } else {
                        success = dbHelper.updateSparePart(part.getId(), name, stock, price, brand, model, quality);
                    }

                    if (success) {
                        loadData();
                        Toast.makeText(this, part == null ? "Part added" : "Part updated", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid numbers", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateBrands(AutoCompleteTextView autoBrand, String category) {
        List<String> brandNames = new ArrayList<>();
        brandIdMap.clear();
        try (Cursor cursor = dbHelper.getBrandsByCategory(category)) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                brandNames.add(name);
                brandIdMap.put(name, id);
            }
        }
        autoBrand.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brandNames));
        autoBrand.setText("");
    }

    private void updateModels(AutoCompleteTextView autoModel, Integer brandId) {
        if (brandId == null) return;
        List<String> modelNames = new ArrayList<>();
        try (Cursor cursor = dbHelper.getModelsByBrand(brandId)) {
            while (cursor.moveToNext()) {
                modelNames.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
            }
        }
        autoModel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, modelNames));
    }
}
