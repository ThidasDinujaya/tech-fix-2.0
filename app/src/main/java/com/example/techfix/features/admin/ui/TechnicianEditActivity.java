package com.example.techfix.features.admin.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.branches.data.Branch;
import com.example.techfix.features.branches.data.Technician;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TechnicianEditActivity extends AppCompatActivity {

    private Technician tech;
    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private String selectedDateStr;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    private TextInputEditText etName;
    private AutoCompleteTextView autoBranch;
    private TextView tvMonthYear, tvDateLabel;
    private GridLayout calendarGrid;
    private MaterialButton btnToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_edit);

        tech = (Technician) getIntent().getSerializableExtra("TECH_DATA");
        if (tech == null) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();
        
        initViews();
        setupData();
    }

    private void initViews() {
        etName = findViewById(R.id.etEditTechName);
        autoBranch = findViewById(R.id.autoEditTechBranch);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        tvDateLabel = findViewById(R.id.tvSelectedDateLabel);
        calendarGrid = findViewById(R.id.calendarGrid);
        btnToggle = findViewById(R.id.btnToggleDateAvail);

        etName.setText(tech.getName());

        findViewById(R.id.btnPrevMonth).setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        findViewById(R.id.btnNextMonth).setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        btnToggle.setOnClickListener(v -> {
            if (selectedDateStr == null) return;
            boolean isAvailable = dbHelper.isTechAvailable(tech.getId(), selectedDateStr);
            dbHelper.setTechAvailability(tech.getId(), selectedDateStr, !isAvailable);
            updateCalendar();
            updateToggleUI(!isAvailable);
        });

        findViewById(R.id.btnSaveTechnician).setOnClickListener(v -> saveChanges());
        
        updateCalendar();
    }

    private void updateCalendar() {
        tvMonthYear.setText(monthYearFormat.format(calendar.getTime()));
        calendarGrid.removeAllViews();

        Calendar tempCal = (Calendar) calendar.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1; // 0 for Sunday
        int daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add Day Labels
        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        for (String day : days) {
            TextView tv = createDayTextView(day, false);
            tv.setTextColor(Color.GRAY);
            calendarGrid.addView(tv);
        }

        // Add Empty Spaces
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarGrid.addView(createDayTextView("", false));
        }

        // Add Month Days
        for (int day = 1; day <= daysInMonth; day++) {
            final int dayNum = day;
            tempCal.set(Calendar.DAY_OF_MONTH, day);
            String dateKey = sdf.format(tempCal.getTime());
            boolean available = dbHelper.isTechAvailable(tech.getId(), dateKey);

            TextView tvDay = createDayTextView(String.valueOf(day), true);
            
            // Set Color: Green for Available, Red for Unavailable
            int bgColor = available ? Color.parseColor("#28A745") : Color.parseColor("#DC3545");
            tvDay.setBackground(createCircleDrawable(bgColor));
            tvDay.setTextColor(Color.WHITE);

            tvDay.setOnClickListener(v -> {
                selectedDateStr = dateKey;
                tvDateLabel.setText("Date: " + dateKey);
                btnToggle.setVisibility(View.VISIBLE);
                updateToggleUI(available);
                
                // Highlight selection logic if needed
            });

            calendarGrid.addView(tvDay);
        }
    }

    private TextView createDayTextView(String text, boolean clickable) {
        TextView tv = new TextView(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 120;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        tv.setLayoutParams(params);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(14);
        tv.setClickable(clickable);
        return tv;
    }

    private GradientDrawable createCircleDrawable(int color) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(color);
        return shape;
    }

    private void updateToggleUI(boolean currentlyAvailable) {
        if (currentlyAvailable) {
            btnToggle.setText("Set Unavailable");
            btnToggle.setBackgroundColor(Color.parseColor("#DC3545"));
        } else {
            btnToggle.setText("Set Available");
            btnToggle.setBackgroundColor(Color.parseColor("#28A745"));
        }
    }

    private void setupData() {
        List<Branch> branches = dbHelper.getAllBranches();
        List<String> names = new ArrayList<>();
        for (Branch b : branches) names.add(b.getName());
        autoBranch.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
        autoBranch.setText(tech.getBranchName(), false);
    }

    private void saveChanges() {
        String name = etName.getText().toString().trim();
        String branch = autoBranch.getText().toString().trim();
        if (!name.isEmpty() && !branch.isEmpty()) {
            if (dbHelper.updateTechnician(tech.getId(), name, branch, tech.getStatus())) {
                Toast.makeText(this, "Technician Updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
