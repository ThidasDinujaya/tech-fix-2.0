package com.example.techfix.features.booking.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.common.util.SessionManager;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingStatus;
import com.example.techfix.features.booking.viewmodel.BookRepairViewModel;
import com.example.techfix.features.branches.data.Branch;
import com.example.techfix.features.payments.ui.PaymentActivity;
import com.example.techfix.features.services.data.Service;
import com.example.techfix.features.services.data.ServiceRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.File;
import java.io.IOException;

public class BookRepairActivity extends AppCompatActivity {

    private AutoCompleteTextView autoDeviceType, autoBrand, autoModel, autoBranch;
    private TextInputEditText etProblemDesc, etAppointmentDate, etServiceName, etQuality;
    private TextInputLayout layoutQuality;
    private ImageView ivDevicePhoto;
    private BookRepairViewModel viewModel;
    private DatabaseHelper dbHelper;
    private ServiceRepository serviceRepo;
    private int selectedServiceId = -1;
    private int userId = -1;
    private Uri capturedImageUri;
    private String selectedImagePath = "";

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImagePath = uri.toString();
                    ivDevicePhoto.setImageURI(uri);
                    ivDevicePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {}
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && capturedImageUri != null) {
                    selectedImagePath = capturedImageUri.toString();
                    ivDevicePhoto.setImageURI(capturedImageUri);
                    ivDevicePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_repair);

        selectedServiceId = getIntent().getIntExtra("selected_service_id", -1);
        String prefilledServiceName = getIntent().getStringExtra("selected_service_name");
        String prefilledDeviceType = getIntent().getStringExtra("selected_device_type");
        String prefilledBrand = getIntent().getStringExtra("selected_brand");
        String prefilledModel = getIntent().getStringExtra("selected_model");
        String prefilledQuality = getIntent().getStringExtra("selected_quality");

        SessionManager sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);
        serviceRepo = ServiceRepository.getInstance(this);
        userId = dbHelper.getUserIdByEmail(sessionManager.getEmail());

        initializeViews();
        setupDropdowns();
        setupDatePicker();
        setupImageAttachment();
        setupViewModel();
        
        if (prefilledServiceName != null) {
            etServiceName.setText(prefilledServiceName);
            autoDeviceType.setText(prefilledDeviceType, false);
            autoBrand.setText(prefilledBrand, false);
            autoModel.setText(prefilledModel, false);

            setReadOnly(etServiceName);
            setReadOnly(autoDeviceType);
            setReadOnly(autoBrand);
            setReadOnly(autoModel);

            if (prefilledQuality != null) {
                layoutQuality.setVisibility(View.VISIBLE);
                etQuality.setText(prefilledQuality);
                setReadOnly(etQuality);
            }

            removeEndIcon(autoDeviceType);
            removeEndIcon(autoBrand);
        }

        findViewById(R.id.btnSubmitBooking).setOnClickListener(v -> submitForm());
    }

    private void setReadOnly(View view) {
        view.setFocusable(false);
        view.setClickable(false);
        view.setLongClickable(false);
        if (view instanceof TextInputEditText) {
            ((TextInputEditText) view).setCursorVisible(false);
        } else if (view instanceof AutoCompleteTextView) {
            ((AutoCompleteTextView) view).setCursorVisible(false);
        }
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
        autoModel = findViewById(R.id.autoBookModel);
        autoBranch = findViewById(R.id.autoBookBranch);
        etQuality = findViewById(R.id.etQuality);
        layoutQuality = findViewById(R.id.layoutBookQuality);
        etProblemDesc = findViewById(R.id.etProblemDesc);
        etAppointmentDate = findViewById(R.id.etAppointmentDate);
        ivDevicePhoto = findViewById(R.id.ivDevicePhoto);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(BookRepairViewModel.class);
        viewModel.getBookingStatus().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                showSuccessDialog();
            }
        });
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Booking Successful!")
            .setMessage("Your repair request has been submitted.")
            .setPositiveButton("OK", (dialog, which) -> finish())
            .setCancelable(false)
            .show();
    }

    private void submitForm() {
        String branch = autoBranch.getText().toString().trim();
        String type = autoDeviceType.getText().toString();
        String brand = autoBrand.getText().toString();
        String model = autoModel.getText().toString();
        String quality = etQuality.getText().toString();
        String desc = etProblemDesc.getText().toString();
        String date = etAppointmentDate.getText().toString();

        if (branch.isEmpty() || type.isEmpty() || brand.isEmpty() || model.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == -1) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            return;
        }

        // Final capacity check
        if (!checkCapacity(branch, date)) {
            return;
        }

        String finalModel = quality.isEmpty() ? model : model + " (" + quality + ")";
        
        Booking newBooking = new Booking(0, selectedServiceId, type, brand, finalModel, desc, date, selectedImagePath, BookingStatus.PENDING, userId, branch, "");
        
        Service service = serviceRepo.getServiceById(selectedServiceId);
        double price = (service != null) ? service.getPrice() : 0.0;

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("booking_data", newBooking);
        intent.putExtra("service_price", price);
        intent.putExtra("service_name", service != null ? service.getName() : "Repair Service");
        startActivity(intent);
    }

    private boolean checkCapacity(String branch, String date) {
        int techCount = dbHelper.getAvailableTechCount(branch, date);
        if (techCount == 0) {
            Toast.makeText(this, "No technicians available at this branch on " + date, Toast.LENGTH_LONG).show();
            return false;
        }

        int capacity = techCount * 15;
        int currentBookings = dbHelper.getBookingCount(branch, date);

        if (currentBookings >= capacity) {
            new AlertDialog.Builder(this)
                .setTitle("Branch Fully Booked")
                .setMessage("Sorry, " + branch + " has reached its repair capacity for " + date + ". Please select another date or branch.")
                .setPositiveButton("OK", null)
                .show();
            return false;
        }
        return true;
    }

    private void setupImageAttachment() {
        ivDevicePhoto.setOnClickListener(v -> {
            String[] options = {"Take Photo", "Choose from Gallery"};
            new AlertDialog.Builder(this)
                    .setTitle("Add Photo")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            openCamera();
                        } else {
                            galleryLauncher.launch("image/*");
                        }
                    })
                    .show();
        });
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            capturedImageUri = FileProvider.getUriForFile(this, "com.example.techfix.fileprovider", photoFile);
            cameraLauncher.launch(capturedImageUri);
        } catch (IOException e) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void setupDropdowns() {
        String[] deviceTypes = {"Phone", "Laptop", "Desktop", "Tablet"};
        autoDeviceType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceTypes));

        autoDeviceType.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = (String) parent.getItemAtPosition(position);
            updateBrands(selectedCategory);
            autoModel.setText("");
        });

        autoBrand.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBrand = (String) parent.getItemAtPosition(position);
            updateModels(selectedBrand);
        });

        List<Branch> branches = dbHelper.getAllBranches();
        List<String> branchNames = new ArrayList<>();
        for (Branch b : branches) branchNames.add(b.getName());
        autoBranch.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, branchNames));
    }

    private void updateBrands(String category) {
        List<String> brandNames = new ArrayList<>();
        try (Cursor cursor = dbHelper.getBrandsByCategory(category)) {
            while (cursor.moveToNext()) {
                brandNames.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
            }
        }
        autoBrand.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brandNames));
        autoBrand.setText("");
    }

    private void updateModels(String brandName) {
        int brandId = -1;
        try (Cursor cursor = dbHelper.getAllBrands()) {
            while (cursor.moveToNext()) {
                if (brandName.equals(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)))) {
                    brandId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                    break;
                }
            }
        }

        if (brandId != -1) {
            List<String> modelNames = new ArrayList<>();
            try (Cursor cursor = dbHelper.getModelsByBrand(brandId)) {
                while (cursor.moveToNext()) {
                    modelNames.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
                }
            }
            autoModel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, modelNames));
        }
        autoModel.setText("");
    }

    private void setupDatePicker() {
        etAppointmentDate.setOnClickListener(v -> {
            String branch = autoBranch.getText().toString().trim();
            if (branch.isEmpty()) {
                Toast.makeText(this, "Please select a branch first", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(selectedYear, selectedMonth, selectedDay);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateStr = sdf.format(selectedCal.getTime());
                
                if (checkCapacity(branch, dateStr)) {
                    etAppointmentDate.setText(dateStr);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }
}
