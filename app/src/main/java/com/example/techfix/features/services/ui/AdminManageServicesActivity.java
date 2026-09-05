package com.example.techfix.features.services.ui;

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
import com.example.techfix.features.services.data.Service;
import com.example.techfix.features.services.data.ServiceRepository;
import java.util.List;

public class AdminManageServicesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminServiceAdapter adapter;
    private ServiceRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_services);

        repository = ServiceRepository.getInstance(this);
        recyclerView = findViewById(R.id.rvAdminServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        findViewById(R.id.fabAddService).setOnClickListener(v -> showAddServiceDialog());
    }

    private void loadData() {
        List<Service> services = repository.getAllServices();
        adapter = new AdminServiceAdapter(services, new AdminServiceAdapter.OnServiceActionListener() {
            @Override
            public void onEdit(Service service) {
                showEditServiceDialog(service);
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

    private void showAddServiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Service");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_service, null);
        
        EditText etName = view.findViewById(R.id.etServiceName);
        EditText etDesc = view.findViewById(R.id.etServiceDesc);
        EditText etPrice = view.findViewById(R.id.etServicePrice);
        EditText etWarranty = view.findViewById(R.id.etServiceWarranty);
        EditText etCategory = view.findViewById(R.id.etServiceCategory);
        EditText etImage = view.findViewById(R.id.etServiceImage);

        builder.setView(view);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String warranty = etWarranty.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String image = etImage.getText().toString().trim();

            if (!name.isEmpty() && !priceStr.isEmpty()) {
                repository.addService(name, desc, Double.parseDouble(priceStr), warranty, image, category);
                loadData();
                Toast.makeText(this, "Service added", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditServiceDialog(Service service) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Service");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_service, null);
        
        EditText etName = view.findViewById(R.id.etServiceName);
        EditText etDesc = view.findViewById(R.id.etServiceDesc);
        EditText etPrice = view.findViewById(R.id.etServicePrice);
        EditText etWarranty = view.findViewById(R.id.etServiceWarranty);
        EditText etCategory = view.findViewById(R.id.etServiceCategory);
        EditText etImage = view.findViewById(R.id.etServiceImage);

        etName.setText(service.getName());
        etDesc.setText(service.getDescription());
        etPrice.setText(String.valueOf(service.getPrice()));
        etWarranty.setText(service.getWarranty());
        etCategory.setText(service.getCategory());
        etImage.setText(service.getImageUrl());

        builder.setView(view);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String warranty = etWarranty.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String image = etImage.getText().toString().trim();

            if (!name.isEmpty() && !priceStr.isEmpty()) {
                repository.updateService(service.getId(), name, desc, Double.parseDouble(priceStr), warranty, image, category);
                loadData();
                Toast.makeText(this, "Service updated", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
