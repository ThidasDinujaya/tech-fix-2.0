package com.example.techfix.features.booking.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import java.util.ArrayList;

// Dashboard screen for Admins to view and manage customer repair bookings
public class AdminManageBookingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_bookings);

        setupRecyclerView();
    }

    // Configures the list view to display bookings (Logic will be moved to ViewModel in Phase 6)
    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rvAdminBookings);
        rv.setLayoutManager(new LinearLayoutManager(this));
        
        // Initial empty adapter (Data will be loaded from SQLite in Phase 6)
        AdminBookingAdapter adapter = new AdminBookingAdapter(new ArrayList<>());
        rv.setAdapter(adapter);
    }
}
