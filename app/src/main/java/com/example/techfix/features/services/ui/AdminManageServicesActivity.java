package com.example.techfix.features.services.ui;

import android.content.Intent;
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
import com.example.techfix.features.admin.ui.AdminManageCategoriesActivity;
import com.example.techfix.features.services.data.Service;
import com.example.techfix.features.services.data.ServiceRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminManageServicesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminServiceAdapter adapter;
    private ServiceRepository repository;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_services);

        repository = ServiceRepository.getInstance(this);
        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvAdminServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        findViewById(R.id.fabAddService).setOnClickListener(v -> showAddServiceDialog(null));
    }

    private void loadData() {
        List<Service> services = repository.getAllServices();
        adapter = new AdminServiceAdapter(services, new AdminServiceAdapter.OnServiceActionListener() {
            @Override
            public void onEdit(Service service) {
                showAddServiceDialog(service);
            }

            @Override
            public void onDelete(Service service) {
                new AlertDialog.Builder(AdminManageServicesActivity.this)
                        .setTitle("Delete Service")
                        .setMessage("Are you sure you want to delete " + service.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (repository.deleteService(service.getId())) {
                                loadData();
                                Toast.makeText(AdminManageServicesActivity.this, "Service deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddServiceDialog(Service service) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(service == null ? "Add New Service" : "Edit Service");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_service, null);
        
        EditText etName = view.findViewById(R.id.etServiceName);
        EditText etDesc = view.findViewById(R.id.etServiceDesc);
        EditText etPrice = view.findViewById(R.id.etServicePrice);
        EditText etWarranty = view.findViewById(R.id.etServiceWarranty);
        EditText etImage = view.findViewById(R.id.etServiceImage);

        AutoCompleteTextView autoCategory = view.findViewById(R.id.autoServiceCategory);
        AutoCompleteTextView autoBrand = view.findViewById(R.id.autoServiceBrand);
        AutoCompleteTextView autoModel = view.findViewById(R.id.autoServiceModel);
        AutoCompleteTextView autoPart = view.findViewById(R.id.autoServicePart);
        AutoCompleteTextView autoQuality = view.findViewById(R.id.autoServiceQuality);

        // State maps for lookups
        Map<String, Integer> brandMap = new HashMap<>();
        Map<String, Integer> partMap = new HashMap<>();

        // 1. Categories
        List<String> categories = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllServiceCategories()) {
            while (cursor.moveToNext()) categories.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
        }
        autoCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories));

        // 2. Logic: Category -> Brand
        autoCategory.setOnItemClickListener((parent, v, position, id) -> {
            String cat = (String) parent.getItemAtPosition(position);
            updateBrands(autoBrand, cat, brandMap);
            autoModel.setText("");
            autoPart.setText("");
        });

        // 3. Logic: Brand -> Model
        autoBrand.setOnItemClickListener((parent, v, position, id) -> {
            String brandName = (String) parent.getItemAtPosition(position);
            updateModels(autoModel, brandMap.get(brandName));
            autoPart.setText("");
        });

        // 4. Logic: Model -> Spare Part
        autoModel.setOnItemClickListener((parent, v, position, id) -> {
            String modelName = (String) parent.getItemAtPosition(position);
            String brandName = autoBrand.getText().toString();
            updateParts(autoPart, brandName, modelName, partMap);
        });

        // 5. Qualities
        List<String> qualities = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllQualities()) {
            while (cursor.moveToNext()) qualities.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
        }
        autoQuality.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, qualities));

        if (service != null) {
            etName.setText(service.getName());
            etDesc.setText(service.getDescription());
            etPrice.setText(String.valueOf(service.getPrice()));
            etWarranty.setText(service.getWarranty());
            etImage.setText(service.getImageUrl());
            
            autoCategory.setText(service.getCategory(), false);
            updateBrands(autoBrand, service.getCategory(), brandMap);
            autoBrand.setText(service.getBrand(), false);
            updateModels(autoModel, brandMap.get(service.getBrand()));
            autoModel.setText(service.getModel(), false);
            updateParts(autoPart, service.getBrand(), service.getModel(), partMap);
            // Find part name if id > 0
            if (service.getSparePartId() > 0) {
                for (Map.Entry<String, Integer> entry : partMap.entrySet()) {
                    if (entry.getValue() == service.getSparePartId()) {
                        autoPart.setText(entry.getKey(), false);
                        break;
                    }
                }
            }
            autoQuality.setText(service.getQuality(), false);
        }

        builder.setView(view);
        builder.setPositiveButton(service == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String warranty = etWarranty.getText().toString().trim();
            String image = etImage.getText().toString().trim();
            String category = autoCategory.getText().toString().trim();
            String brand = autoBrand.getText().toString().trim();
            String model = autoModel.getText().toString().trim();
            String quality = autoQuality.getText().toString().trim();
            String partName = autoPart.getText().toString().trim();
            int partId = partMap.getOrDefault(partName, 0);

            if (!name.isEmpty() && !priceStr.isEmpty() && !category.isEmpty()) {
                double price = Double.parseDouble(priceStr);
                boolean success;
                if (service == null) {
                    success = repository.addService(name, desc, price, warranty, image, category, brand, model, quality, partId);
                } else {
                    success = repository.updateService(service.getId(), name, desc, price, warranty, image, category, brand, model, quality, partId);
                }

                if (success) {
                    loadData();
                    Toast.makeText(this, service == null ? "Service added" : "Service updated", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Required fields missing", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateBrands(AutoCompleteTextView autoBrand, String category, Map<String, Integer> brandMap) {
        brandMap.clear();
        List<String> names = new ArrayList<>();
        try (Cursor cursor = dbHelper.getBrandsByCategory(category)) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                names.add(name);
                brandMap.put(name, id);
            }
        }
        autoBrand.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
        autoBrand.setText("");
    }

    private void updateModels(AutoCompleteTextView autoModel, Integer brandId) {
        List<String> names = new ArrayList<>();
        if (brandId != null) {
            try (Cursor cursor = dbHelper.getModelsByBrand(brandId)) {
                while (cursor.moveToNext()) names.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
            }
        }
        autoModel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
        autoModel.setText("");
    }

    private void updateParts(AutoCompleteTextView autoPart, String brand, String model, Map<String, Integer> partMap) {
        partMap.clear();
        List<String> names = new ArrayList<>();
        try (Cursor cursor = dbHelper.getSparePartsByModel(brand, model)) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                names.add(name);
                partMap.put(name, id);
            }
        }
        autoPart.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
        autoPart.setText("");
    }
}
