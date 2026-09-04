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
import com.example.techfix.features.auth.data.LoginActivity;
import com.example.techfix.features.booking.ui.AdminManageBookingsActivity;
import com.example.techfix.features.branches.ui.BranchesActivity;
import com.example.techfix.features.branches.ui.ManageSparePartsActivity;
import com.example.techfix.features.branches.ui.ManageTechniciansActivity;
import com.example.techfix.features.payments.ui.PaymentActivity;
import com.example.techfix.features.services.ui.ServicesActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    // =========================
    // DRAWER
    // =========================
    private DrawerLayout drawerLayout;

    // =========================
    // TOP BAR
    // =========================
    private ImageView imgAdminMenu;
    private ImageView imgAdminNotification;

    // =========================
    // STATISTICS
    // =========================
    private TextView txtTotalBookings;
    private TextView txtPendingRepairs;
    private TextView txtCompleted;
    private TextView txtTotalRevenue;

    // =========================
    // QUICK ACCESS
    // =========================
    private LinearLayout cardBookings;
    private LinearLayout cardServices;
    private LinearLayout cardTechnicians;
    private LinearLayout cardParts;
    private LinearLayout cardBranches;
    private LinearLayout cardPayments;

    // =========================
    // DRAWER MENU ITEMS
    // =========================
    private TextView menuDashboard;
    private TextView menuAdminProfile;
    private TextView menuNotifications;
    private TextView menuSettings;
    private TextView menuCustomerLogin;
    private TextView menuLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_dashboard);

        // =========================
        // CONNECT DRAWER
        // =========================
        drawerLayout = findViewById(R.id.drawerLayout);

        // =========================
        // CONNECT TOP BAR
        // =========================
        imgAdminMenu = findViewById(R.id.imgAdminMenu);
        imgAdminNotification = findViewById(R.id.imgAdminNotification);

        // =========================
        // CONNECT STATISTICS
        // =========================
        txtTotalBookings = findViewById(R.id.txtTotalBookings);
        txtPendingRepairs = findViewById(R.id.txtPendingRepairs);
        txtCompleted = findViewById(R.id.txtCompleted);
        txtTotalRevenue = findViewById(R.id.txtTotalRevenue);

        // =========================
        // CONNECT QUICK ACCESS
        // =========================
        cardBookings = findViewById(R.id.cardBookings);
        cardServices = findViewById(R.id.cardServices);
        cardTechnicians = findViewById(R.id.cardTechnicians);
        cardParts = findViewById(R.id.cardParts);
        cardBranches = findViewById(R.id.cardBranches);
        cardPayments = findViewById(R.id.cardPayments);

        // =========================
        // CONNECT DRAWER MENU
        // =========================
        menuDashboard = findViewById(R.id.menuDashboard);
        menuAdminProfile = findViewById(R.id.menuAdminProfile);
        menuNotifications = findViewById(R.id.menuNotifications);
        menuSettings = findViewById(R.id.menuSettings);
        menuCustomerLogin = findViewById(R.id.menuCustomerLogin);
        menuLogout = findViewById(R.id.menuLogout);

        // =========================
        // TEMPORARY DASHBOARD DATA
        // =========================
        txtTotalBookings.setText("128");
        txtPendingRepairs.setText("32");
        txtCompleted.setText("96");
        txtTotalRevenue.setText("LKR 256,000");

        // =========================
        // OPEN DRAWER
        // =========================
        imgAdminMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(Gravity.START);
        });

        // =========================
        // TOP NOTIFICATION
        // =========================
        imgAdminNotification.setOnClickListener(v -> {
            Toast.makeText(
                    AdminDashboardActivity.this,
                    "No new notifications",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // =========================
        // QUICK ACCESS - BOOKINGS
        // =========================
        cardBookings.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminManageBookingsActivity.class);
            startActivity(intent);
        });

        // =========================
        // QUICK ACCESS - SERVICES
        // =========================
        cardServices.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ServicesActivity.class);
            startActivity(intent);
        });

        // =========================
        // QUICK ACCESS - TECHNICIANS
        // =========================
        cardTechnicians.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageTechniciansActivity.class);
            startActivity(intent);
        });

        // =========================
        // QUICK ACCESS - PARTS
        // =========================
        cardParts.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageSparePartsActivity.class);
            startActivity(intent);
        });

        // =========================
        // QUICK ACCESS - BRANCHES
        // =========================
        cardBranches.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, BranchesActivity.class);
            startActivity(intent);
        });

        // =========================
        // QUICK ACCESS - PAYMENTS
        // =========================
        cardPayments.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, PaymentActivity.class);
            startActivity(intent);
        });

        // =========================
        // DRAWER - DASHBOARD
        // =========================
        menuDashboard.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
        });

        // =========================
        // DRAWER - ADMIN PROFILE
        // =========================
        menuAdminProfile.setOnClickListener(v -> {

            drawerLayout.closeDrawer(Gravity.START);

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    AdminProfileActivity.class
            );

            startActivity(intent);
        });

        // =========================
        // DRAWER - NOTIFICATIONS
        // =========================
        menuNotifications.setOnClickListener(v -> {

            drawerLayout.closeDrawer(Gravity.START);

            Toast.makeText(
                    AdminDashboardActivity.this,
                    "No new notifications",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // =========================
        // DRAWER - SETTINGS
        // =========================
        menuSettings.setOnClickListener(v -> {

            drawerLayout.closeDrawer(Gravity.START);

            Toast.makeText(
                    AdminDashboardActivity.this,
                    "Settings",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // =========================
        // DRAWER - CUSTOMER LOGIN
        // =========================
        menuCustomerLogin.setOnClickListener(v -> {

            drawerLayout.closeDrawer(Gravity.START);

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
        });

        // =========================
        // DRAWER - LOGOUT
        // =========================
        menuLogout.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    AdminLoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        });
    }

    // =========================
    // BACK BUTTON
    // =========================
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.START)) {

            drawerLayout.closeDrawer(Gravity.START);

        } else {

            super.onBackPressed();
        }
    }
}