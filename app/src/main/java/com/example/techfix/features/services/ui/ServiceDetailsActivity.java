package com.example.techfix.features.services.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.booking.ui.BookRepairActivity;
import com.example.techfix.features.services.data.Service;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Detailed view for a service with device selection via dropdowns and dynamic pricing
public class ServiceDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ServiceDetailsActivity";

    private AutoCompleteTextView autoBrand, autoModel, autoQuality;
    private TextInputLayout layoutBrand, layoutModel, layoutQuality;
    private TextView tvPrice, tvAvailability;
    private Service service;
    private DatabaseHelper dbHelper;

    private final Map<String, Integer> brandIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        dbHelper = new DatabaseHelper(this);
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

        // Load qualities from DB or list
        List<String> qualities = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllQualities()) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    qualities.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading qualities", e);
        }
        if (qualities.isEmpty()) {
            qualities.add("Original");
            qualities.add("Grade A");
            qualities.add("Grade B");
        }
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
        brandIdMap.clear();
        List<String> brandNames = new ArrayList<>();

        String category = service.getCategory();
        if (category != null && !category.isEmpty()) {
            try (Cursor cursor = dbHelper.getBrandsByCategory(category)) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                        brandNames.add(name);
                        brandIdMap.put(name, id);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading brands by category", e);
            }
        }

        if (brandNames.isEmpty()) {
            try (Cursor cursor = dbHelper.getAllBrands()) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                        brandNames.add(name);
                        brandIdMap.put(name, id);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading all brands", e);
            }
        }

        if (brandNames.isEmpty()) {
            if ("Phone".equalsIgnoreCase(category)) {
                brandNames.add("Apple");
                brandNames.add("Samsung");
                brandNames.add("Google");
            } else if ("Laptop".equalsIgnoreCase(category)) {
                brandNames.add("Apple");
                brandNames.add("HP");
                brandNames.add("Dell");
                brandNames.add("Asus");
                brandNames.add("Lenovo");
            } else {
                brandNames.add("Generic");
            }
        }

        if (!brandNames.contains("Other")) {
            brandNames.add("Other");
        }

        autoBrand.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brandNames));
    }

    private void updateModels(String brandName) {
        List<String> modelNames = new ArrayList<>();
        Integer brandId = brandIdMap.get(brandName);

        if (brandId != null) {
            try (Cursor cursor = dbHelper.getModelsByBrand(brandId)) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                        modelNames.add(name);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading models by brand", e);
            }
        }

        if (modelNames.isEmpty()) {
            if ("Apple".equalsIgnoreCase(brandName)) {
                modelNames.add("iPhone 15 Pro");
                modelNames.add("iPhone 14");
                modelNames.add("iPhone 13");
            } else if ("Samsung".equalsIgnoreCase(brandName)) {
                modelNames.add("Galaxy S23");
                modelNames.add("Galaxy S22");
                modelNames.add("Galaxy A54");
            } else {
                modelNames.add("Standard Model");
            }
        }

        if (!modelNames.contains("Other")) {
            modelNames.add("Other");
        }

        autoModel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, modelNames));
        autoModel.setText("");
    }

    private void updatePricing() {
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
    }
}
