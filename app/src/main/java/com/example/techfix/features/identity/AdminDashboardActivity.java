package com.example.techfix.features.identity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.techfix.R;
import com.example.techfix.features.booking.ui.AdminManageBookingsActivity;

// Placeholder Dashboard for Admin (UI 16). 
// Connects to Member 2's "Manage Bookings" screen.
public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Connect the "Manage Bookings" card to Member 2's Activity
        findViewById(R.id.cardManageBookings).setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminManageBookingsActivity.class);
            startActivity(intent);
        });
    }
}
