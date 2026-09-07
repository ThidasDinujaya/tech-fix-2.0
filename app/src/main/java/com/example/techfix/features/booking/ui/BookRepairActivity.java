package com.example.techfix.features.booking.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.util.List;
import java.util.Locale;

public class BookRepairActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private AutoCompleteTextView autoDeviceType, autoBrand, autoBranch;
    private TextInputEditText etModel, etProblemDesc, etAppointmentDate, etServiceName, etQuality;
    private TextInputLayout layoutQuality;
    private ImageView ivDevicePhoto;
    private BookRepairViewModel viewModel;
    private DatabaseHelper dbHelper;
    private ServiceRepository serviceRepo;
    private int selectedServiceId = -1;
    private int userId = -1;

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
        setupCamera();
        setupViewModel();
        
        if (prefilledServiceName != null) {
            etServiceName.setText(prefilledServiceName);
            autoDeviceType.setText(prefilledDeviceType);
            autoBrand.setText(prefilledBrand);
            etModel.setText(prefilledModel);

            setReadOnly(etServiceName);
            setReadOnly(autoDeviceType);
            setReadOnly(autoBrand);
            setReadOnly(etModel);

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
        autoBranch = findViewById(R.id.autoBookBranch);
        etModel = findViewById(R.id.etModel);
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
        String model = etModel.getText().toString();
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
        
        Booking newBooking = new Booking(0, selectedServiceId, type, brand, finalModel, desc, date, "", BookingStatus.PENDING, userId, branch, "");
        
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

    private void setupDropdowns() {
        String[] deviceTypes = {"Mobile Phone", "Laptop", "Desktop", "Tablet"};
        autoDeviceType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceTypes));

        String[] brands = {"Samsung", "Apple", "Huawei", "HP", "Dell", "Asus"};
        autoBrand.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brands));

        List<Branch> branches = dbHelper.getAllBranches();
        List<String> branchNames = new ArrayList<>();
        for (Branch b : branches) branchNames.add(b.getName());
        autoBranch.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, branchNames));
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
