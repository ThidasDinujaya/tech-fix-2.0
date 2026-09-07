package com.example.techfix.features.booking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingStatus;
import com.example.techfix.features.booking.viewmodel.AdminManageBookingsViewModel;
import com.example.techfix.features.branches.data.Technician;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;

public class AdminManageBookingsActivity extends AppCompatActivity {

    private AdminManageBookingsViewModel viewModel;
    private AdminBookingAdapter adapter;
    private String currentFilter = "All";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_bookings);

        dbHelper = new DatabaseHelper(this);
        setupRecyclerView();
        setupViewModel();
        setupTabs();
    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rvAdminBookings);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AdminManageBookingsViewModel.class);
        viewModel.getBookings().observe(this, this::updateAdapter);
        viewModel.fetchBookings(currentFilter);
    }

    private void updateAdapter(List<Booking> bookings) {
        adapter = new AdminBookingAdapter(bookings, new AdminBookingAdapter.OnBookingActionListener() {
            @Override
            public void onUpdateStatus(Booking booking) {
                showStatusUpdateDialog(booking);
            }

            @Override
            public void onDelete(Booking booking) {
                new AlertDialog.Builder(AdminManageBookingsActivity.this)
                        .setTitle("Delete Booking")
                        .setMessage("Are you sure you want to delete TF" + (1000 + booking.getId()) + "?")
                        .setPositiveButton("Delete", (dialog, which) -> viewModel.deleteBooking(booking.getId(), currentFilter))
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onViewReview(Booking booking) {
                Intent intent = new Intent(AdminManageBookingsActivity.this, ViewReviewActivity.class);
                intent.putExtra("booking_id", booking.getId());
                startActivity(intent);
            }
        }, dbHelper);
        ((RecyclerView) findViewById(R.id.rvAdminBookings)).setAdapter(adapter);
    }

    private void showStatusUpdateDialog(Booking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_booking_status, null);
        
        TextView tvTitle = view.findViewById(R.id.tvBookingTitle);
        AutoCompleteTextView autoStatus = view.findViewById(R.id.autoUpdateStatus);
        AutoCompleteTextView autoTech = view.findViewById(R.id.autoAssignTech);
        TextInputLayout layoutTech = view.findViewById(R.id.layoutAssignTech);

        tvTitle.setText("Update Booking TF" + (1000 + booking.getId()));

        String[] statuses = {
                BookingStatus.PENDING,
                BookingStatus.ASSIGNED,
                BookingStatus.COLLECTED,
                BookingStatus.REPAIRING,
                BookingStatus.COMPLETED
        };
        autoStatus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, statuses));
        autoStatus.setText(booking.getStatus(), false);

        // Fetch techs for this specific branch and date
        List<Technician> branchTechs = dbHelper.getAvailableTechsForBranch(booking.getBranchName(), booking.getAppointmentDate());
        List<String> techNames = new ArrayList<>();
        for (Technician t : branchTechs) techNames.add(t.getName());
        
        autoTech.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, techNames));
        
        if (booking.getTechnicianName() != null) {
            autoTech.setText(booking.getTechnicianName(), false);
        }

        // Only show tech assignment for specific statuses
        autoStatus.setOnItemClickListener((parent, v, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            layoutTech.setVisibility(selected.equals(BookingStatus.PENDING) ? View.GONE : View.VISIBLE);
        });
        layoutTech.setVisibility(booking.getStatus().equals(BookingStatus.PENDING) ? View.GONE : View.VISIBLE);

        builder.setView(view);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String newStatus = autoStatus.getText().toString();
            String assignedTech = autoTech.getText().toString();
            
            if (!newStatus.equals(BookingStatus.PENDING) && assignedTech.isEmpty()) {
                Toast.makeText(this, "Please assign a technician", Toast.LENGTH_SHORT).show();
                return;
            }
            
            viewModel.assignTechnician(booking.getId(), newStatus, assignedTech, currentFilter);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void setupTabs() {
        TabLayout tabs = findViewById(R.id.tabLayoutBookings);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFilter = tab.getText().toString();
                viewModel.fetchBookings(currentFilter);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}
