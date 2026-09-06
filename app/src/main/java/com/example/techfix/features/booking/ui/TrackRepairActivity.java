package com.example.techfix.features.booking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.techfix.R;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingStatus;
import com.example.techfix.features.booking.viewmodel.TrackRepairViewModel;
import com.example.techfix.features.payments.ui.PaymentActivity;

public class TrackRepairActivity extends AppCompatActivity {

    private TextView tvBookingId, tvServiceName, tvDate, tvAssignedTech;
    private View stepSubmitted, stepAssigned, stepReceived, stepRepairing, stepReady, stepCompleted;
    private Button btnPayNow;
    private TrackRepairViewModel viewModel;
    private int bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_repair);

        int bookingId = getIntent().getIntExtra("booking_id", -1);
        this.bookingId = bookingId;

        initViews();
        setupViewModel(bookingId);
    }

    private void initViews() {
        tvBookingId = findViewById(R.id.tvBookingId);
        tvServiceName = findViewById(R.id.tvServiceName);
        tvDate = findViewById(R.id.tvDate);
        tvAssignedTech = findViewById(R.id.tvAssignedTech); // New if available in layout
        btnPayNow = findViewById(R.id.btnPayNow);

        stepSubmitted = findViewById(R.id.stepSubmitted);
        stepAssigned = findViewById(R.id.stepAssigned);
        stepReceived = findViewById(R.id.stepReceived);
        stepRepairing = findViewById(R.id.stepRepairing);
        stepReady = findViewById(R.id.stepReady);
        stepCompleted = findViewById(R.id.stepCompleted);

        setupStep(stepSubmitted, "Booking Submitted");
        setupStep(stepAssigned, "Technician Assigned");
        setupStep(stepReceived, "Device Received");
        setupStep(stepRepairing, "In Repair");
        setupStep(stepReady, "Ready for Collection");
        setupStep(stepCompleted, "Completed");

        btnPayNow.setOnClickListener(v -> {
            Intent intent = new Intent(TrackRepairActivity.this, PaymentActivity.class);
            intent.putExtra("booking_id", bookingId);
            startActivity(intent);
        });
    }

    private void setupStep(View view, String title) {
        TextView tvTitle = view.findViewById(R.id.tvStepTitle);
        tvTitle.setText(title);
    }

    private void setupViewModel(int bookingId) {
        viewModel = new ViewModelProvider(this).get(TrackRepairViewModel.class);
        viewModel.loadBooking(bookingId);
        viewModel.getBookingDetails().observe(this, this::updateUI);
    }

    private void updateUI(Booking booking) {
        if (booking == null) return;

        tvBookingId.setText("Booking ID\nTF" + (1000 + booking.getId()));
        tvServiceName.setText("Repair Service");
        tvDate.setText(booking.getAppointmentDate());

        if (tvAssignedTech != null) {
            String tech = booking.getTechnicianName();
            tvAssignedTech.setText(tech != null && !tech.isEmpty() ? "Assigned Tech: " + tech : "");
            tvAssignedTech.setVisibility(tech != null && !tech.isEmpty() ? View.VISIBLE : View.GONE);
        }

        if (BookingStatus.READY.equals(booking.getStatus()) || BookingStatus.COMPLETED.equals(booking.getStatus())) {
            btnPayNow.setVisibility(View.VISIBLE);
        } else {
            btnPayNow.setVisibility(View.GONE);
        }

        updateTimeline(booking.getStatus());
    }

    private void updateTimeline(String status) {
        resetStep(stepSubmitted);
        resetStep(stepAssigned);
        resetStep(stepReceived);
        resetStep(stepRepairing);
        resetStep(stepReady);
        resetStep(stepCompleted);

        setStepActive(stepSubmitted); 
        
        if (BookingStatus.ASSIGNED.equals(status) || BookingStatus.COLLECTED.equals(status) || 
            BookingStatus.REPAIRING.equals(status) || BookingStatus.READY.equals(status) || 
            BookingStatus.COMPLETED.equals(status)) {
            setStepActive(stepAssigned);
        }
        
        if (BookingStatus.COLLECTED.equals(status) || BookingStatus.REPAIRING.equals(status) || 
            BookingStatus.READY.equals(status) || BookingStatus.COMPLETED.equals(status)) {
            setStepActive(stepReceived);
        }

        if (BookingStatus.REPAIRING.equals(status) || BookingStatus.READY.equals(status) || 
            BookingStatus.COMPLETED.equals(status)) {
            setStepActive(stepRepairing);
        }
        
        if (BookingStatus.READY.equals(status) || BookingStatus.COMPLETED.equals(status)) {
            setStepActive(stepReady);
        }
        
        if (BookingStatus.COMPLETED.equals(status)) {
            setStepActive(stepCompleted);
        }
    }

    private void setStepActive(View view) {
        if (view == null) return;
        ImageView iv = view.findViewById(R.id.ivIndicator);
        iv.setImageResource(android.R.drawable.checkbox_on_background);
        iv.setColorFilter(getResources().getColor(R.color.brand_blue));
        
        View line = view.findViewById(R.id.viewLine);
        if (line != null) line.setBackgroundColor(getResources().getColor(R.color.brand_blue));
    }

    private void resetStep(View view) {
        if (view == null) return;
        ImageView iv = view.findViewById(R.id.ivIndicator);
        iv.setImageResource(android.R.drawable.checkbox_off_background);
        iv.setColorFilter(getResources().getColor(R.color.gray_400));
        
        View line = view.findViewById(R.id.viewLine);
        if (line != null) line.setBackgroundColor(getResources().getColor(R.color.gray_400));
    }
}
