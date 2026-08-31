package com.example.techfix.features.booking.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.techfix.R;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

// Handles the repair booking form where users enter device details
public class BookRepairActivity extends AppCompatActivity {

    private AutoCompleteTextView autoDeviceType, autoBrand;
    private TextInputEditText etAppointmentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_repair);

        initializeViews();
        setupDropdowns();
        setupDatePicker();
        
        findViewById(R.id.btnSubmitBooking).setOnClickListener(v -> {
            // Submission logic will be handled by the ViewModel in Phase 4
            Toast.makeText(this, "Booking feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    // Connects the UI elements to the Java variables
    private void initializeViews() {
        autoDeviceType = findViewById(R.id.autoDeviceType);
        autoBrand = findViewById(R.id.autoBrand);
        etAppointmentDate = findViewById(R.id.etAppointmentDate);
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
