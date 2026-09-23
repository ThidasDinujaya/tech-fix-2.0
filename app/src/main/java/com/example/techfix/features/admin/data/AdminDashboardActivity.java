package com.example.techfix.features.admin.data;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.techfix.R;
import com.example.techfix.common.sync.FirebaseSyncRepository;
import com.example.techfix.features.admin.ui.AdminInventoryActivity;
import com.example.techfix.features.admin.ui.AdminManageTimeSlotsActivity;
import com.example.techfix.features.auth.data.LoginActivity;
import com.example.techfix.features.booking.ui.AdminManageBookingsActivity;
import com.example.techfix.features.branches.ui.BranchesActivity;
import com.example.techfix.features.branches.ui.ManageTechniciansActivity;
import com.example.techfix.features.services.ui.AdminManageServicesActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView imgAdminMenu;

    private LinearLayout cardBookings, cardServices, cardTechnicians, cardBranches, cardInventory;

    private TextView menuDashboard, menuAdminProfile, menuCustomerLogin, menuLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        imgAdminMenu = findViewById(R.id.imgAdminMenu);

        cardBookings = findViewById(R.id.cardBookings);
        cardServices = findViewById(R.id.cardServices);
        cardTechnicians = findViewById(R.id.cardTechnicians);
        cardBranches = findViewById(R.id.cardBranches);
        cardInventory = findViewById(R.id.cardInventory);

        menuDashboard = findViewById(R.id.menuDashboard);
        menuAdminProfile = findViewById(R.id.menuAdminProfile);
        menuCustomerLogin = findViewById(R.id.menuCustomerLogin);
        menuLogout = findViewById(R.id.menuLogout);

        imgAdminMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        cardBookings.setOnClickListener(v -> startActivity(new Intent(this, AdminManageBookingsActivity.class)));
        cardServices.setOnClickListener(v -> startActivity(new Intent(this, AdminManageServicesActivity.class)));
        cardTechnicians.setOnClickListener(v -> startActivity(new Intent(this, ManageTechniciansActivity.class)));
        cardInventory.setOnClickListener(v -> startActivity(new Intent(this, AdminInventoryActivity.class)));

        cardBranches.setOnClickListener(v -> {
            Intent intent = new Intent(this, BranchesActivity.class);
            intent.putExtra("IS_ADMIN", true);
            startActivity(intent);
        });

        menuDashboard.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));
        menuAdminProfile.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, AdminProfileActivity.class));
        });
        findViewById(R.id.menuTimeSlots).setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, AdminManageTimeSlotsActivity.class));
        });
        menuLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.menuSyncFirebase).setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
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

    @Override
    protected void onResume() {
        super.onResume();
        
        // Automatic Background Sync (Push & Pull)
        Toast.makeText(this, "Syncing data with Cloud...", Toast.LENGTH_SHORT).show();
        
        FirebaseSyncRepository syncRepo = new FirebaseSyncRepository(this);
        // 1. Pull from Cloud
        syncRepo.pullAllDataFromFirebase(success -> {
            if (success) {
                // 2. Push local changes back (to handle any local updates that were pending)
                new Thread(syncRepo::pushAllDataToFirebase).start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
