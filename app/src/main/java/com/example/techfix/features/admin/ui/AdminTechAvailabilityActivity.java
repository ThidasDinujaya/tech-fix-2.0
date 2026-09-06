package com.example.techfix.features.admin.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AdminTechAvailabilityActivity extends AppCompatActivity {

    private int techId;
    private String techName;
    private String selectedDate;
    private DatabaseHelper dbHelper;
    private TextView tvStatus, tvDateDisplay;
    private MaterialButton btnToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tech_availability);

        techId = getIntent().getIntExtra("TECH_ID", -1);
        techName = getIntent().getStringExtra("TECH_NAME");
        dbHelper = new DatabaseHelper(this);

        TextView tvTitle = findViewById(R.id.tvTechNameTitle);
        tvTitle.setText("Availability: " + techName);

        CalendarView calendarView = findViewById(R.id.calendarTechAvail);
        tvStatus = findViewById(R.id.tvCurrentStatus);
        tvDateDisplay = findViewById(R.id.tvSelectedDateDisplay);
        btnToggle = findViewById(R.id.btnToggleAvailability);

        // Initial date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(Calendar.getInstance().getTime());
        updateUI();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            selectedDate = sdf.format(cal.getTime());
            updateUI();
        });

        btnToggle.setOnClickListener(v -> {
            boolean isAvailable = dbHelper.isTechAvailable(techId, selectedDate);
            dbHelper.setTechAvailability(techId, selectedDate, !isAvailable);
            updateUI();
            Toast.makeText(this, "Status Updated", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI() {
        tvDateDisplay.setText("Date: " + selectedDate);
        boolean available = dbHelper.isTechAvailable(techId, selectedDate);
        
        if (available) {
            tvStatus.setText("Status: AVAILABLE");
            tvStatus.setTextColor(Color.parseColor("#28A745"));
            btnToggle.setText("Set as Unavailable");
            btnToggle.setBackgroundColor(Color.parseColor("#DC3545"));
        } else {
            tvStatus.setText("Status: UNAVAILABLE");
            tvStatus.setTextColor(Color.parseColor("#DC3545"));
            btnToggle.setText("Set as Available");
            btnToggle.setBackgroundColor(Color.parseColor("#28A745"));
        }
    }
}
