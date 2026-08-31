package com.example.techfix.features.booking.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.techfix.R;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

// Handles the repair booking form and camera integration for device photos
public class BookRepairActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private AutoCompleteTextView autoDeviceType, autoBrand;
    private TextInputEditText etAppointmentDate;
    private ImageView ivDevicePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_repair);

        initializeViews();
        setupDropdowns();
        setupDatePicker();
        setupCamera();
        
        findViewById(R.id.btnSubmitBooking).setOnClickListener(v -> {
            Toast.makeText(this, "Booking feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    // Connects the UI elements to the Java variables
    private void initializeViews() {
        autoDeviceType = findViewById(R.id.autoDeviceType);
        autoBrand = findViewById(R.id.autoBrand);
        etAppointmentDate = findViewById(R.id.etAppointmentDate);
        ivDevicePhoto = findViewById(R.id.ivDevicePhoto);
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
