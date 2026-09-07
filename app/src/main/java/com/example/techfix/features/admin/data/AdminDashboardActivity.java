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
import com.example.techfix.common.sync.FirebaseSyncRepository;
import com.example.techfix.features.auth.data.LoginActivity;
import com.example.techfix.features.booking.ui.AdminManageBookingsActivity;
import com.example.techfix.features.branches.ui.BranchesActivity;
import com.example.techfix.features.branches.ui.ManageTechniciansActivity;
import com.example.techfix.features.payments.ui.PaymentActivity;
import com.example.techfix.features.services.ui.AdminManageServicesActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView imgAdminMenu;
    private ImageView imgAdminNotification;

    private TextView txtTotalBookings, txtPendingRepairs, txtCompleted, txtTotalRevenue;

    private LinearLayout cardBookings, cardServices, cardTechnicians, cardBranches, cardPayments, cardInventory;

    private TextView menuDashboard, menuAdminProfile, menuNotifications, menuSettings, menuCustomerLogin, menuLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        imgAdminMenu = findViewById(R.id.imgAdminMenu);
        imgAdminNotification = findViewById(R.id.imgAdminNotification);

        txtTotalBookings = findViewById(R.id.txtTotalBookings);
        txtPendingRepairs = findViewById(R.id.txtPendingRepairs);
        txtCompleted = findViewById(R.id.txtCompleted);
        txtTotalRevenue = findViewById(R.id.txtTotalRevenue);

        cardBookings = findViewById(R.id.cardBookings);
        cardServices = findViewById(R.id.cardServices);
        cardTechnicians = findViewById(R.id.cardTechnicians);
        cardBranches = findViewById(R.id.cardBranches);
        cardPayments = findViewById(R.id.cardPayments);
        cardInventory = findViewById(R.id.cardInventory);

        menuDashboard = findViewById(R.id.menuDashboard);
        menuAdminProfile = findViewById(R.id.menuAdminProfile);
        menuNotifications = findViewById(R.id.menuNotifications);
        menuSettings = findViewById(R.id.menuSettings);
        menuCustomerLogin = findViewById(R.id.menuCustomerLogin);
        menuLogout = findViewById(R.id.menuLogout);

        txtTotalBookings.setText("128");
        txtPendingRepairs.setText("32");
        txtCompleted.setText("96");
        txtTotalRevenue.setText("LKR 256,000");

        imgAdminMenu.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));
        imgAdminNotification.setOnClickListener(v -> Toast.makeText(this, "No new notifications", Toast.LENGTH_SHORT).show());

        cardBookings.setOnClickListener(v -> startActivity(new Intent(this, AdminManageBookingsActivity.class)));
        cardServices.setOnClickListener(v -> startActivity(new Intent(this, AdminManageServicesActivity.class)));
        cardTechnicians.setOnClickListener(v -> startActivity(new Intent(this, ManageTechniciansActivity.class)));
        cardInventory.setOnClickListener(v -> startActivity(new Intent(this, AdminInventoryActivity.class)));

        cardBranches.setOnClickListener(v -> {
            Intent intent = new Intent(this, BranchesActivity.class);
            intent.putExtra("IS_ADMIN", true);
            startActivity(intent);
        });
        cardPayments.setOnClickListener(v -> startActivity(new Intent(this, PaymentActivity.class)));

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
        menuNotifications.setOnClickListener(v -> Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show());
        menuSettings.setOnClickListener(v -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show());
        menuCustomerLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        // Automatic Background Sync
        new Thread(() -> {
            FirebaseSyncRepository syncRepo = new FirebaseSyncRepository(this);
            syncRepo.pushAllDataToFirebase();
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
