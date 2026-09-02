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
import com.example.techfix.features.booking.viewmodel.MyBookingsViewModel;
import java.util.ArrayList;

public class RepairHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private BookingAdapter adapter;
    private MyBookingsViewModel viewModel;
    private TextView tvEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_history);

        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int userId = dbHelper.getUserIdByEmail(userEmail);

        initViews();
        setupRecyclerView();
        setupViewModel(userId);
    }

    private void initViews() {
        rvHistory = findViewById(R.id.rvHistory);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new BookingAdapter(new ArrayList<>(), booking -> {
            Intent intent = new Intent(RepairHistoryActivity.this, TrackRepairActivity.class);
            intent.putExtra("booking_id", booking.getId());
            startActivity(intent);
        });
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }

    private void setupViewModel(int userId) {
        viewModel = new ViewModelProvider(this).get(MyBookingsViewModel.class);
        viewModel.getCompletedBookings().observe(this, bookings -> {
            if (bookings == null || bookings.isEmpty()) {
                rvHistory.setVisibility(View.GONE);
                tvEmptyMessage.setVisibility(View.VISIBLE);
            } else {
                rvHistory.setVisibility(View.VISIBLE);
                tvEmptyMessage.setVisibility(View.GONE);
                adapter.setBookings(bookings);
            }
        });
        viewModel.loadBookings(userId);
    }
}
