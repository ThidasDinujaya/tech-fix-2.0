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
import java.util.List;

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
        
        // Add a way to manage categories - maybe a button in the layout? 
        // For now, let's assume we can trigger it or add it to the toolbar.
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
        AutoCompleteTextView autoCategory = view.findViewById(R.id.autoServiceCategory);
        EditText etImage = view.findViewById(R.id.etServiceImage);

        // Load Categories from DB
        List<String> categories = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllServiceCategories()) {
            while (cursor.moveToNext()) {
                categories.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
            }
        }
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        autoCategory.setAdapter(catAdapter);

        if (service != null) {
            etName.setText(service.getName());
            etDesc.setText(service.getDescription());
            etPrice.setText(String.valueOf(service.getPrice()));
            etWarranty.setText(service.getWarranty());
            autoCategory.setText(service.getCategory(), false);
            etImage.setText(service.getImageUrl());
        }

        builder.setView(view);
        builder.setPositiveButton(service == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String warranty = etWarranty.getText().toString().trim();
            String category = autoCategory.getText().toString().trim();
            String image = etImage.getText().toString().trim();

            if (!name.isEmpty() && !priceStr.isEmpty() && !category.isEmpty()) {
                boolean success;
                if (service == null) {
                    success = repository.addService(name, desc, Double.parseDouble(priceStr), warranty, image, category);
                } else {
                    success = repository.updateService(service.getId(), name, desc, Double.parseDouble(priceStr), warranty, image, category);
                }

                if (success) {
                    loadData();
                    Toast.makeText(this, service == null ? "Service added" : "Service updated", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Required fields missing", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNeutralButton("Manage Categories", (dialog, which) -> {
            startActivity(new Intent(this, AdminManageCategoriesActivity.class));
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
