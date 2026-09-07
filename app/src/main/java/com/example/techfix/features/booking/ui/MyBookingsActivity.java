package com.example.techfix.features.booking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.viewmodel.MyBookingsViewModel;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private RecyclerView rvBookings;
    private BookingAdapter adapter;
    private MyBookingsViewModel viewModel;
    private TabLayout tabLayout;
    private TextView tvEmptyMessage;
    private int userId;
    private DatabaseHelper dbHelper;

    private List<Booking> upcomingList = new ArrayList<>();
    private List<Booking> completedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        dbHelper = new DatabaseHelper(this);
        userId = dbHelper.getUserIdByEmail(userEmail);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupTabLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadBookings(userId);
    }

    private void initViews() {
        rvBookings = findViewById(R.id.rvBookings);
        tabLayout = findViewById(R.id.tabLayout);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
    }

    private void setupRecyclerView() {
        adapter = new BookingAdapter(new ArrayList<>(), new BookingAdapter.OnBookingClickListener() {
            @Override
            public void onBookingClick(Booking booking) {
                Intent intent = new Intent(MyBookingsActivity.this, TrackRepairActivity.class);
                intent.putExtra("booking_id", booking.getId());
                startActivity(intent);
            }

            @Override
            public void onAddReviewClick(Booking booking) {
                Intent intent = new Intent(MyBookingsActivity.this, AddReviewActivity.class);
                intent.putExtra("booking_id", booking.getId());
                startActivity(intent);
            }

            @Override
            public void onViewReviewClick(Booking booking) {
                Intent intent = new Intent(MyBookingsActivity.this, ViewReviewActivity.class);
                intent.putExtra("booking_id", booking.getId());
                startActivity(intent);
            }
        }, dbHelper);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MyBookingsViewModel.class);
        
        viewModel.getUpcomingBookings().observe(this, bookings -> {
            upcomingList = bookings;
            if (tabLayout.getSelectedTabPosition() == 0) {
                updateUI(upcomingList);
            }
        });

        viewModel.getCompletedBookings().observe(this, bookings -> {
            completedList = bookings;
            if (tabLayout.getSelectedTabPosition() == 1) {
                updateUI(completedList);
            }
        });
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    updateUI(upcomingList);
                } else {
                    updateUI(completedList);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void updateUI(List<Booking> list) {
        if (list == null || list.isEmpty()) {
            rvBookings.setVisibility(View.GONE);
            tvEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            rvBookings.setVisibility(View.VISIBLE);
            tvEmptyMessage.setVisibility(View.GONE);
            adapter.setBookings(list);
        }
    }
}
