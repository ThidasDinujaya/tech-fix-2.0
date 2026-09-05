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

// Detailed view for a service with device selection, part quality, and dynamic pricing
public class ServiceDetailsActivity extends AppCompatActivity {

    private AutoCompleteTextView autoBrand, autoModel, autoQuality;
    private TextInputLayout layoutQuality;
    private TextView tvPrice, tvAvailability;
    private Service service;
    
    private final Map<String, List<String>> brandModelMap = new HashMap<>();
    private boolean isPartsService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        service = (Service) getIntent().getSerializableExtra("service_data");
        
        initializeViews();
        checkIfPartsService();
        setupData();
        
        if (service != null) {
            populateDetails(service);
        }

        Button btnBook = findViewById(R.id.btnBookService);
        btnBook.setOnClickListener(v -> {
            String brand = autoBrand.getText().toString();
            String model = autoModel.getText().toString();
            String quality = autoQuality.getText().toString();
            
            if (brand.isEmpty() || model.isEmpty()) {
                Toast.makeText(this, "Please select your device brand and model", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isPartsService && quality.isEmpty()) {
                Toast.makeText(this, "Please select part quality", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, BookRepairActivity.class);
            intent.putExtra("selected_service_id", service.getId());
            intent.putExtra("selected_service_name", service.getName());
            intent.putExtra("selected_device_type", service.getCategory());
            intent.putExtra("selected_brand", brand);
            intent.putExtra("selected_model", model);
            if (isPartsService) {
                intent.putExtra("selected_quality", quality);
            }
            startActivity(intent);
        });
    }

    private void initializeViews() {
        autoBrand = findViewById(R.id.autoDetailBrand);
        autoModel = findViewById(R.id.autoDetailModel);
        autoQuality = findViewById(R.id.autoPartQuality);
        layoutQuality = findViewById(R.id.layoutPartQuality);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvAvailability = findViewById(R.id.tvAvailability);
        
        // Initial state
        tvPrice.setText("Select device to see price");
        tvAvailability.setText("");
    }

    private void checkIfPartsService() {
        if (service == null) return;
        String name = service.getName().toLowerCase();
        // Determine if service involves hardware parts
        isPartsService = name.contains("repair") || name.contains("replacement") || 
                         name.contains("fix") || name.contains("swap");
        
        if (isPartsService) {
            layoutQuality.setVisibility(View.VISIBLE);
        } else {
            layoutQuality.setVisibility(View.GONE);
        }
    }

    private void setupData() {
        if (service == null) return;

        // Populate Quality dropdown
        String[] qualities = {"Original", "Grade A", "Grade B"};
        ArrayAdapter<String> qualityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, qualities);
        autoQuality.setAdapter(qualityAdapter);

        // Populate mock data based on category
        if ("Phone".equalsIgnoreCase(service.getCategory())) {
            setupPhoneData();
        } else if ("Laptop".equalsIgnoreCase(service.getCategory())) {
            setupLaptopData();
        } else if ("Desktop".equalsIgnoreCase(service.getCategory())) {
            setupDesktopData();
        } else if ("Tablet".equalsIgnoreCase(service.getCategory())) {
            setupTabletData();
        } else {
            setupGenericData();
        }

        autoBrand.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBrand = (String) parent.getItemAtPosition(position);
            updateModels(selectedBrand);
            resetPricing();
        });

        autoModel.setOnItemClickListener((parent, view, position, id) -> {
            resetPricingIfRequired();
            updatePricing();
        });

        autoQuality.setOnItemClickListener((parent, view, position, id) -> {
            updatePricing();
        });
    }

    private void setupPhoneData() {
        List<String> samsungModels = new ArrayList<>();
        samsungModels.add("Galaxy S23 Ultra");
        samsungModels.add("Galaxy S22");
        samsungModels.add("Galaxy A54");

        List<String> appleModels = new ArrayList<>();
        appleModels.add("iPhone 15 Pro");
        appleModels.add("iPhone 14");
        appleModels.add("iPhone 13");

        brandModelMap.put("Samsung", samsungModels);
        brandModelMap.put("Apple", appleModels);

        String[] brands = {"Samsung", "Apple"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands);
        autoBrand.setAdapter(brandAdapter);
    }

    private void setupLaptopData() {
        List<String> hpModels = new ArrayList<>();
        hpModels.add("Pavilion 15");
        hpModels.add("Envy x360");
        hpModels.add("Spectre x360");

        List<String> dellModels = new ArrayList<>();
        dellModels.add("XPS 13");
        dellModels.add("Inspiron 15");
        dellModels.add("Latitude 5000");

        brandModelMap.put("HP", hpModels);
        brandModelMap.put("Dell", dellModels);

        String[] brands = {"HP", "Dell"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands);
        autoBrand.setAdapter(brandAdapter);
    }

    private void setupDesktopData() {
        List<String> asusModels = new ArrayList<>();
        asusModels.add("ROG Strix G15");
        asusModels.add("TUF Gaming F15");

        brandModelMap.put("Asus", asusModels);

        String[] brands = {"Asus"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands);
        autoBrand.setAdapter(brandAdapter);
    }

    private void setupTabletData() {
        List<String> ipadModels = new ArrayList<>();
        ipadModels.add("iPad Pro 12.9");
        ipadModels.add("iPad Air");
        ipadModels.add("iPad Mini");

        brandModelMap.put("Apple", ipadModels);

        String[] brands = {"Apple"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands);
        autoBrand.setAdapter(brandAdapter);
    }

    private void setupGenericData() {
        List<String> genericModels = new ArrayList<>();
        genericModels.add("Standard Edition");
        genericModels.add("Pro Series");

        brandModelMap.put("Other", genericModels);

        String[] brands = {"Other"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands);
        autoBrand.setAdapter(brandAdapter);
    }

    private void updateModels(String brand) {
        List<String> models = brandModelMap.get(brand);
        if (models != null) {
            ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, models);
            autoModel.setAdapter(modelAdapter);
            autoModel.setText("");
        }
    }

    private void resetPricing() {
        tvPrice.setText("Select model to see price");
        tvAvailability.setText("");
        autoQuality.setText("");
    }

    private void resetPricingIfRequired() {
        if (isPartsService && autoQuality.getText().toString().isEmpty()) {
            tvPrice.setText("Select quality to see price");
        }
    }

    private void updatePricing() {
        String brand = autoBrand.getText().toString();
        String model = autoModel.getText().toString();
        String quality = autoQuality.getText().toString();

        if (brand.isEmpty() || model.isEmpty()) {
            resetPricing();
            return;
        }

        if (isPartsService && quality.isEmpty()) {
            tvPrice.setText("Select quality to see price");
            tvAvailability.setText("");
            return;
        }

        // Logic to simulate dynamic pricing and availability
        double calculatedPrice = service.getPrice();
        String status;
        int color;

        // Model based adjustment
        if (model.contains("Pro") || model.contains("Ultra") || model.contains("XPS")) {
            calculatedPrice += 5000;
        }

        // Quality based adjustment
        if (isPartsService) {
            if ("Original".equalsIgnoreCase(quality)) {
                calculatedPrice *= 1.5; // 50% more for original
                status = "Genuine Parts - Guaranteed";
                color = Color.parseColor("#2E7D32");
            } else if ("Grade A".equalsIgnoreCase(quality)) {
                calculatedPrice *= 1.2; // 20% more for Grade A
                status = "High Quality Compatible";
                color = Color.parseColor("#0867D9");
            } else {
                status = "Budget Friendly Option";
                color = Color.parseColor("#F57C00");
            }
        } else {
            status = "Standard Service Available";
            color = Color.parseColor("#2E7D32");
        }

        tvPrice.setText(String.format("LKR %,.2f", calculatedPrice));
        tvAvailability.setText(status);
        tvAvailability.setTextColor(color);
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
    }
}
