package com.example.techfix.features.booking.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.common.util.SessionManager;
import com.example.techfix.features.booking.viewmodel.BookRepairViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

// Handles the repair booking form, camera integration, and data submission
public class BookRepairActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private AutoCompleteTextView autoDeviceType, autoBrand;
    private TextInputEditText etModel, etProblemDesc, etAppointmentDate, etServiceName, etQuality;
    private TextInputLayout layoutQuality;
    private ImageView ivDevicePhoto;
    private BookRepairViewModel viewModel;
    private int selectedServiceId = -1;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_repair);

        // Get the selected service details from the previous screen
        selectedServiceId = getIntent().getIntExtra("selected_service_id", -1);
        String prefilledServiceName = getIntent().getStringExtra("selected_service_name");
        String prefilledDeviceType = getIntent().getStringExtra("selected_device_type");
        String prefilledBrand = getIntent().getStringExtra("selected_brand");
        String prefilledModel = getIntent().getStringExtra("selected_model");
        String prefilledQuality = getIntent().getStringExtra("selected_quality");

        // User Session management
        SessionManager sessionManager = new SessionManager(this);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userId = dbHelper.getUserIdByEmail(sessionManager.getEmail());

        initializeViews();
        setupDropdowns();
        setupDatePicker();
        setupCamera();
        setupViewModel();
        
        // Apply pre-filled data if coming from ServiceDetails
        if (prefilledServiceName != null) {
            etServiceName.setText(prefilledServiceName);
            autoDeviceType.setText(prefilledDeviceType);
            autoBrand.setText(prefilledBrand);
            etModel.setText(prefilledModel);

            // Disable editing for pre-filled fields to prevent errors
            etServiceName.setEnabled(false);
            autoDeviceType.setEnabled(false);
            autoBrand.setEnabled(false);
            etModel.setEnabled(false);

            if (prefilledQuality != null) {
                layoutQuality.setVisibility(View.VISIBLE);
                etQuality.setText(prefilledQuality);
                etQuality.setEnabled(false);
            }

            // Remove end icons for a cleaner read-only state
            removeEndIcon(autoDeviceType);
            removeEndIcon(autoBrand);
        }

        findViewById(R.id.btnSubmitBooking).setOnClickListener(v -> submitForm());
    }

    private void removeEndIcon(AutoCompleteTextView view) {
        if (view.getParent().getParent() instanceof TextInputLayout) {
            ((TextInputLayout) view.getParent().getParent()).setEndIconMode(TextInputLayout.END_ICON_NONE);
        }
    }

    private void initializeViews() {
        etServiceName = findViewById(R.id.etServiceName);
        autoDeviceType = findViewById(R.id.autoDeviceType);
        autoBrand = findViewById(R.id.autoBrand);
        etModel = findViewById(R.id.etModel);
        etQuality = findViewById(R.id.etQuality);
        layoutQuality = findViewById(R.id.layoutBookQuality);
        etProblemDesc = findViewById(R.id.etProblemDesc);
        etAppointmentDate = findViewById(R.id.etAppointmentDate);
        ivDevicePhoto = findViewById(R.id.ivDevicePhoto);
    }

    // Connects the UI to the ViewModel for form validation and submission
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(BookRepairViewModel.class);

        // Listen for successful booking
        viewModel.getBookingStatus().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                showSuccessDialog();
            }
        });

        // Listen for validation or database errors
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Shows a professional success dialog before closing the screen
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Booking Successful!")
            .setMessage("Your repair request has been submitted. You can track its status in 'Repairs'.")
            .setPositiveButton("OK", (dialog, which) -> finish())
            .setCancelable(false)
            .show();
    }

    // Collects all data from the form and sends it to the ViewModel
    private void submitForm() {
        String type = autoDeviceType.getText().toString();
        String brand = autoBrand.getText().toString();
        String model = etModel.getText().toString();
        String quality = etQuality.getText().toString();
        String desc = etProblemDesc.getText().toString();
        String date = etAppointmentDate.getText().toString();

        if (userId == -1) {
            Toast.makeText(this, "Error: User session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Include quality in the model name for storage if present
        String finalModel = quality.isEmpty() ? model : model + " (" + quality + ")";

        viewModel.submitBooking(selectedServiceId, type, brand, finalModel, desc, date, userId, "");
    }

    // Sets up the click listener to open the system camera
    private void setupCamera() {
        ivDevicePhoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Receives the photo from the camera app and displays it in the ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivDevicePhoto.setImageBitmap(imageBitmap);
            ivDevicePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    // Fills the dropdown menus with predefined device and brand options
    private void setupDropdowns() {
        String[] deviceTypes = {"Mobile Phone", "Laptop", "Desktop", "Tablet"};
        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceTypes);
        autoDeviceType.setAdapter(deviceAdapter);

        String[] brands = {"Samsung", "Apple", "Huawei", "HP", "Dell", "Asus"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands);
        autoBrand.setAdapter(brandAdapter);
    }

    // Opens a calendar dialog when the date field is clicked
    private void setupDatePicker() {
        etAppointmentDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                etAppointmentDate.setText(date);
            }, year, month, day);
            
            datePickerDialog.show();
        });
    }
}
