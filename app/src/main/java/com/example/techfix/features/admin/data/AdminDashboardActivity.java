package com.example.techfix.features.admin.data;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.techfix.R;
import com.example.techfix.features.admin.ui.AdminInventoryActivity;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.common.sync.FirebaseSyncRepository;
import com.example.techfix.features.auth.data.LoginActivity;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingRepository;
import com.example.techfix.features.booking.data.BookingStatus;
import com.example.techfix.features.booking.ui.AdminManageBookingsActivity;
import com.example.techfix.features.branches.ui.BranchesActivity;
import com.example.techfix.features.branches.ui.ManageTechniciansActivity;
import com.example.techfix.features.payments.data.Payment;
import com.example.techfix.features.payments.ui.AdminViewPaymentsActivity;
import com.example.techfix.features.services.ui.AdminManageServicesActivity;
import java.util.List;
import java.util.Locale;

public class AdminDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView imgAdminMenu;

    private TextView txtTotalBookings, txtPendingRepairs, txtCompleted, txtTotalRevenue;

    private LinearLayout cardBookings, cardServices, cardTechnicians, cardBranches, cardInventory;

    private TextView menuDashboard, menuAdminProfile, menuCustomerLogin, menuLogout;

    private DatabaseHelper dbHelper;
    private BookingRepository bookingRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        imgAdminMenu = findViewById(R.id.imgAdminMenu);

        txtTotalBookings = findViewById(R.id.txtTotalBookings);
        txtPendingRepairs = findViewById(R.id.txtPendingRepairs);
        txtCompleted = findViewById(R.id.txtCompleted);
        txtTotalRevenue = findViewById(R.id.txtTotalRevenue);

        cardBookings = findViewById(R.id.cardBookings);
        cardServices = findViewById(R.id.cardServices);
        cardTechnicians = findViewById(R.id.cardTechnicians);
        cardBranches = findViewById(R.id.cardBranches);
        cardInventory = findViewById(R.id.cardInventory);

        menuDashboard = findViewById(R.id.menuDashboard);
        menuAdminProfile = findViewById(R.id.menuAdminProfile);
        menuCustomerLogin = findViewById(R.id.menuCustomerLogin);
        menuLogout = findViewById(R.id.menuLogout);

        dbHelper = new DatabaseHelper(this);
        bookingRepo = BookingRepository.getInstance(this);

        loadStatistics();

        imgAdminMenu.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));

        cardBookings.setOnClickListener(v -> startActivity(new Intent(this, AdminManageBookingsActivity.class)));
        cardServices.setOnClickListener(v -> startActivity(new Intent(this, AdminManageServicesActivity.class)));
        cardTechnicians.setOnClickListener(v -> startActivity(new Intent(this, ManageTechniciansActivity.class)));
        cardInventory.setOnClickListener(v -> startActivity(new Intent(this, AdminInventoryActivity.class)));

        cardBranches.setOnClickListener(v -> {
            Intent intent = new Intent(this, BranchesActivity.class);
            intent.putExtra("IS_ADMIN", true);
            startActivity(intent);
        });

        menuDashboard.setOnClickListener(v -> drawerLayout.closeDrawer(Gravity.START));
        menuAdminProfile.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            startActivity(new Intent(this, AdminProfileActivity.class));
        });
        menuLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.menuSyncFirebase).setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            FirebaseSyncRepository syncRepo = new FirebaseSyncRepository(this);
            syncRepo.pushAllDataToFirebase();
            Toast.makeText(this, "Syncing data to Cloud...", Toast.LENGTH_SHORT).show();
        });

        // Other menu items placeholders
        menuCustomerLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        // Automatic Background Sync
        new Thread(() -> {
            FirebaseSyncRepository syncRepo = new FirebaseSyncRepository(this);
            syncRepo.pushAllDataToFirebase();
        }).start();
    }

    private void loadStatistics() {
        List<Booking> bookings = bookingRepo.getAllBookings();
        List<Payment> payments = dbHelper.getAllPayments();

        int totalBookings = bookings.size();
        int pendingRepairs = 0;
        int completedRepairs = 0;
        double totalRevenue = 0;

        for (Booking b : bookings) {
            if (BookingStatus.PENDING.equals(b.getStatus()) || BookingStatus.ASSIGNED.equals(b.getStatus())) {
                pendingRepairs++;
            } else if (BookingStatus.COMPLETED.equals(b.getStatus())) {
                completedRepairs++;
            }
        }

        for (Payment p : payments) {
            totalRevenue += p.getAmount();
        }

        txtTotalBookings.setText(String.valueOf(totalBookings));
        txtPendingRepairs.setText(String.valueOf(pendingRepairs));
        txtCompleted.setText(String.valueOf(completedRepairs));
        txtTotalRevenue.setText(String.format(Locale.US, "LKR %.2f", totalRevenue));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics();
        // Automatic Background Sync on return to dashboard
        Toast.makeText(this, "Syncing data to Cloud...", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            FirebaseSyncRepository syncRepo = new FirebaseSyncRepository(this);
            syncRepo.pushAllDataToFirebase();
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }
}
