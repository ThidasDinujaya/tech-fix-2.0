package com.example.techfix.features.services.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.techfix.R;
import com.example.techfix.features.booking.ui.BookRepairActivity;
import com.example.techfix.features.services.data.Service;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Detailed view for a service with device selection via dropdowns and dynamic pricing
public class ServiceDetailsActivity extends AppCompatActivity {

    private AutoCompleteTextView autoBrand, autoModel, autoQuality;
    private TextInputLayout layoutBrand, layoutModel, layoutQuality;
    private TextView tvPrice, tvAvailability, tvSpecs;
    private Service service;
    
    private final Map<String, List<String>> brandModelMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        service = (Service) getIntent().getSerializableExtra("service_data");
        
        initializeViews();
        setupData();
        
        if (service != null) {
            populateDetails(service);
        }

        Button btnBook = findViewById(R.id.btnBookService);
        btnBook.setOnClickListener(v -> {
            String brand = autoBrand.getText().toString().trim();
            String model = autoModel.getText().toString().trim();
            String quality = autoQuality.getText().toString().trim();
            
            if (brand.isEmpty()) {
                Toast.makeText(this, "Please select your device brand", Toast.LENGTH_SHORT).show();
                return;
            }

            if (model.isEmpty()) {
                Toast.makeText(this, "Please select your device model", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, BookRepairActivity.class);
            intent.putExtra("selected_service_id", service.getId());
            intent.putExtra("selected_service_name", service.getName());
            intent.putExtra("selected_device_type", service.getCategory());
            intent.putExtra("selected_brand", brand);
            intent.putExtra("selected_model", model);
            intent.putExtra("selected_quality", quality);
            startActivity(intent);
        });
    }

    private void initializeViews() {
        autoBrand = findViewById(R.id.autoDetailBrand);
        autoModel = findViewById(R.id.autoDetailModel);
        autoQuality = findViewById(R.id.autoPartQuality);
        
        layoutBrand = (TextInputLayout) autoBrand.getParent().getParent();
        layoutModel = (TextInputLayout) autoModel.getParent().getParent();
        layoutQuality = findViewById(R.id.layoutPartQuality);
        
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvAvailability = findViewById(R.id.tvAvailability);
        
        tvPrice.setText("Select device to see price");
        tvAvailability.setText("");
    }

    private void setupData() {
        if (service == null) return;

        // Load qualities
        String[] qualities = {"Original", "Grade A", "Grade B"};
        autoQuality.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, qualities));

        // Logic: If service has fixed specs, show them and disable dropdowns
        if (service.getBrand() != null && !service.getBrand().isEmpty()) {
            autoBrand.setText(service.getBrand(), false);
            autoBrand.setEnabled(false);
            
            if (service.getModel() != null && !service.getModel().isEmpty()) {
                autoModel.setText(service.getModel(), false);
                autoModel.setEnabled(false);
            }
            
            if (service.getQuality() != null && !service.getQuality().isEmpty()) {
                layoutQuality.setVisibility(View.VISIBLE);
                autoQuality.setText(service.getQuality(), false);
                autoQuality.setEnabled(false);
            }
            
            updatePricing();
        } else {
            // Setup generic data if service is flexible
            setupGenericDeviceData();
            
            autoBrand.setOnItemClickListener((parent, view, position, id) -> {
                String selectedBrand = (String) parent.getItemAtPosition(position);
                updateModels(selectedBrand);
                updatePricing();
            });

            autoModel.setOnItemClickListener((parent, view, position, id) -> updatePricing());
            autoQuality.setOnItemClickListener((parent, view, position, id) -> updatePricing());
        }
    }

    private void setupGenericDeviceData() {
        // Fallback or generic brands based on category
        String[] brands;
        if ("Phone".equalsIgnoreCase(service.getCategory())) {
            brands = new String[]{"Samsung", "Apple", "Google", "Huawei", "Other"};
        } else if ("Laptop".equalsIgnoreCase(service.getCategory())) {
            brands = new String[]{"HP", "Dell", "Asus", "Apple", "Lenovo", "Other"};
        } else {
            brands = new String[]{"Generic", "Other"};
        }
        autoBrand.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands));
    }

    private void updateModels(String brand) {
        // Fetch from DB or use mock list
        String[] models = {"Model X", "Model Y", "Standard", "Other"};
        autoModel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, models));
        autoModel.setText("");
    }

    private void updatePricing() {
        // Simplified pricing
        tvPrice.setText(String.format("LKR %,.2f", service.getPrice()));
        tvAvailability.setText("Service Available");
        tvAvailability.setTextColor(Color.parseColor("#2E7D32"));
    }

    private void populateDetails(Service service) {
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvDesc = findViewById(R.id.tvDetailDesc);
        ImageView ivHeader = findViewById(R.id.ivDetailHeader);

        tvName.setText(service.getName());
        tvDesc.setText(service.getDescription());

        Glide.with(this)
            .load(service.getImageUrl())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .centerCrop()
            .into(ivHeader);
            
        // Show fixed specs if any
        if (service.getBrand() != null && !service.getBrand().isEmpty()) {
            String specs = "For: " + service.getBrand();
            if (service.getModel() != null) specs += " " + service.getModel();
            if (service.getQuality() != null) specs += " | Quality: " + service.getQuality();
            
            // We could add a tvSpecs to layout
        }
    }
}
