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
        
        tvPrice.setText("Select device to see price");
        tvAvailability.setText("");

        // Ensure dropdown shows on click for non-editable AutoCompleteTextViews
        View.OnClickListener dropdownClick = v -> {
            if (v instanceof AutoCompleteTextView) {
                ((AutoCompleteTextView) v).showDropDown();
            }
        };
        autoBrand.setOnClickListener(dropdownClick);
        autoModel.setOnClickListener(dropdownClick);
        autoQuality.setOnClickListener(dropdownClick);
    }

    private void checkIfPartsService() {
        if (service == null) return;
        String name = service.getName().toLowerCase();
        
        if (name.contains("diagnosis")) {
            isPartsService = false;
            layoutQuality.setVisibility(View.GONE);
            return;
        }

        isPartsService = name.contains("repair") || name.contains("replacement") || 
                         name.contains("fix") || name.contains("swap");
        
        layoutQuality.setVisibility(isPartsService ? View.VISIBLE : View.GONE);
    }

    private void setupData() {
        if (service == null) return;

        String[] qualities = {"Original", "Grade A", "Grade B"};
        ArrayAdapter<String> qualityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, qualities);
        autoQuality.setAdapter(qualityAdapter);

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
            if (selectedBrand.equals("Other")) {
                // Automatically set model to "Other" as requested
                List<String> otherModels = new ArrayList<>();
                otherModels.add("Other");
                ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, otherModels);
                autoModel.setAdapter(modelAdapter);
                autoModel.setText("Other", false);
            } else {
                updateModels(selectedBrand);
            }
            updatePricing();
        });

        autoModel.setOnItemClickListener((parent, view, position, id) -> {
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
        samsungModels.add("Other");

        List<String> appleModels = new ArrayList<>();
        appleModels.add("iPhone 15 Pro");
        appleModels.add("iPhone 14");
        appleModels.add("iPhone 13");
        appleModels.add("Other");

        brandModelMap.put("Samsung", samsungModels);
        brandModelMap.put("Apple", appleModels);

        String[] brands = {"Samsung", "Apple", "Other"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands);
        autoBrand.setAdapter(brandAdapter);
    }

    private void setupLaptopData() {
        List<String> hpModels = new ArrayList<>();
        hpModels.add("Pavilion 15");
        hpModels.add("Envy x360");
        hpModels.add("Other");

        List<String> dellModels = new ArrayList<>();
        dellModels.add("XPS 13");
        dellModels.add("Inspiron 15");
        dellModels.add("Other");

        brandModelMap.put("HP", hpModels);
        brandModelMap.put("Dell", dellModels);

        String[] brands = {"HP", "Dell", "Other"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands);
        autoBrand.setAdapter(brandAdapter);
    }

    private void setupDesktopData() {
        List<String> asusModels = new ArrayList<>();
        asusModels.add("ROG Strix G15");
        asusModels.add("Other");

        brandModelMap.put("Asus", asusModels);

        String[] brands = {"Asus", "Other"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands);
        autoBrand.setAdapter(brandAdapter);
    }

    private void setupTabletData() {
        List<String> ipadModels = new ArrayList<>();
        ipadModels.add("iPad Pro 12.9");
        ipadModels.add("Other");

        brandModelMap.put("Apple", ipadModels);

        String[] brands = {"Apple", "Other"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands);
        autoBrand.setAdapter(brandAdapter);
    }

    private void setupGenericData() {
        List<String> genericModels = new ArrayList<>();
        genericModels.add("Standard Edition");
        genericModels.add("Other");

        brandModelMap.put("Generic", genericModels);

        String[] brands = {"Generic", "Other"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands);
        autoBrand.setAdapter(brandAdapter);
    }

    private void updateModels(String brand) {
        List<String> models = brandModelMap.get(brand);
        if (models != null) {
            ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, models);
            autoModel.setAdapter(modelAdapter);
            autoModel.setText("", false);
        }
    }

    private void resetPricing(String message) {
        tvPrice.setText(message);
        tvAvailability.setText("");
    }

    private void updatePricing() {
        String brand = autoBrand.getText().toString().trim();
        String model = autoModel.getText().toString().trim();
        String quality = autoQuality.getText().toString().trim();

        if (brand.isEmpty()) {
            resetPricing("Select brand to see price");
            return;
        }

        if (model.isEmpty()) {
            resetPricing("Select model to see price");
            return;
        }

        if (isPartsService && quality.isEmpty()) {
            resetPricing("Select quality to see price");
            return;
        }

        // Use base price as requested, no multipliers
        double calculatedPrice = service.getPrice();
        String status;
        int color;

        if (service.getName().toLowerCase().contains("diagnosis")) {
            status = "Standard Diagnosis Fee";
            color = Color.parseColor("#0867D9");
        } else {
            status = "Service Price Calculated";
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
