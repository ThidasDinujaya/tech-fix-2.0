package com.example.techfix.features.booking.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.features.booking.viewmodel.AdminManageBookingsViewModel;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

// Dashboard screen for Admins to view and manage customer repair bookings
public class AdminManageBookingsActivity extends AppCompatActivity {

    private AdminManageBookingsViewModel viewModel;
    private AdminBookingAdapter adapter;

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
        adapter = new AdminBookingAdapter(new ArrayList<>());
        rv.setAdapter(adapter);
    }

    // Connects the UI to the ViewModel to observe booking data
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AdminManageBookingsViewModel.class);
        
        viewModel.getBookings().observe(this, bookings -> {
            adapter = new AdminBookingAdapter(bookings);
            ((RecyclerView) findViewById(R.id.rvAdminBookings)).setAdapter(adapter);
        });

        // Initial fetch
        viewModel.fetchBookings("All");
    }

    // Listens for tab selection changes to filter the bookings list
    private void setupTabs() {
        TabLayout tabs = findViewById(R.id.tabLayoutBookings);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String filter = tab.getText().toString();
                viewModel.fetchBookings(filter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}
