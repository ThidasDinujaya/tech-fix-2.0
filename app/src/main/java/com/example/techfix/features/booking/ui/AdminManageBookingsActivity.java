package com.example.techfix.features.booking.ui;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingStatus;
import com.example.techfix.features.booking.viewmodel.AdminManageBookingsViewModel;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

// Dashboard screen for Admins to view and manage customer repair bookings
public class AdminManageBookingsActivity extends AppCompatActivity {

    private AdminManageBookingsViewModel viewModel;
    private AdminBookingAdapter adapter;
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_bookings);

        setupRecyclerView();
        setupViewModel();
        setupTabs();
    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rvAdminBookings);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    // Connects the UI to the ViewModel to observe booking data
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AdminManageBookingsViewModel.class);
        
        viewModel.getBookings().observe(this, bookings -> {
            updateAdapter(bookings);
        });

        // Initial fetch
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
                        .setMessage("Are you sure you want to delete booking TF" + (1000 + booking.getId()) + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            viewModel.deleteBooking(booking.getId(), currentFilter);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        ((RecyclerView) findViewById(R.id.rvAdminBookings)).setAdapter(adapter);
    }

    private void showStatusUpdateDialog(Booking booking) {
        String[] statuses = {
                BookingStatus.PENDING,
                BookingStatus.ASSIGNED,
                BookingStatus.REPAIRING,
                BookingStatus.READY,
                BookingStatus.COMPLETED
        };

        new AlertDialog.Builder(this)
                .setTitle("Update Booking Status")
                .setItems(statuses, (dialog, which) -> {
                    viewModel.updateBookingStatus(booking.getId(), statuses[which], currentFilter);
                })
                .show();
    }

    // Listens for tab selection changes to filter the bookings list
    private void setupTabs() {
        TabLayout tabs = findViewById(R.id.tabLayoutBookings);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFilter = tab.getText().toString();
                viewModel.fetchBookings(currentFilter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}
