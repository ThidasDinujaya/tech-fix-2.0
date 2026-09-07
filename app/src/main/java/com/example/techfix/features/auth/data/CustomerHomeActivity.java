package com.example.techfix.features.auth.data;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.booking.ui.MyBookingsActivity;
import com.example.techfix.features.branches.ui.BranchesActivity;
import com.example.techfix.features.services.ui.ServicesActivity;

public class CustomerHomeActivity extends AppCompatActivity {

    // ScrollView for refresh
    private ScrollView scrollViewHome;

    // Header
    private ImageView imgCustomerNotification;

    // Welcome
    private TextView txtWelcome;

    // Search / View All
    private EditText edtSearchService;
    private TextView txtViewAll;

    // Service cards
    private LinearLayout cardPhoneRepair;
    private LinearLayout cardLaptopRepair;
    private LinearLayout cardDesktopRepair;
    private LinearLayout cardTabletRepair;

    // Bottom navigation
    private LinearLayout navHome;
    private LinearLayout navMyRepairs;
    private LinearLayout navBranches;
    private LinearLayout navProfile;

    // Database
    private DatabaseHelper databaseHelper;

    // Logged-in user's email
    private String userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_home);

        databaseHelper = new DatabaseHelper(this);

        // Get logged-in user email
        userEmail =
                getIntent().getStringExtra("USER_EMAIL");


        scrollViewHome = findViewById(R.id.scrollHome);

        // Header
        imgCustomerNotification =
                findViewById(R.id.imgCustomerNotification);


        // Customer details
        txtWelcome =
                findViewById(R.id.txtWelcome);


        // Search / View all
        edtSearchService =
                findViewById(R.id.edtSearchService);

        txtViewAll =
                findViewById(R.id.txtViewAll);


        // Service cards
        cardPhoneRepair =
                findViewById(R.id.cardPhoneRepair);

        cardLaptopRepair =
                findViewById(R.id.cardLaptopRepair);

        cardDesktopRepair =
                findViewById(R.id.cardDesktopRepair);

        cardTabletRepair =
                findViewById(R.id.cardTabletRepair);


        // Bottom navigation
        navHome =
                findViewById(R.id.navHome);

        navMyRepairs =
                findViewById(R.id.navMyRepairs);

        navBranches =
                findViewById(R.id.navBranches);

        navProfile =
                findViewById(R.id.navProfile);


        // Load customer name
        loadCustomerName();


        // Notifications
        imgCustomerNotification.setOnClickListener(v -> {

            Toast.makeText(
                    CustomerHomeActivity.this,
                    "No new notifications",
                    Toast.LENGTH_SHORT
            ).show();
        });


        // View all services
        txtViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, ServicesActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // Phone repair
        cardPhoneRepair.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, ServicesActivity.class);
            intent.putExtra("CATEGORY", "Phone");
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // Laptop repair
        cardLaptopRepair.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, ServicesActivity.class);
            intent.putExtra("CATEGORY", "Laptop");
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // Desktop repair
        cardDesktopRepair.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, ServicesActivity.class);
            intent.putExtra("CATEGORY", "Desktop");
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // Tablet repair
        cardTabletRepair.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, ServicesActivity.class);
            intent.putExtra("CATEGORY", "Tablet");
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // Bottom - Home
        navHome.setOnClickListener(v -> {
            refreshDashboard();
        });


        // Bottom - Repairs
        navMyRepairs.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, MyBookingsActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // Bottom - Branches
        navBranches.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, BranchesActivity.class);
            intent.putExtra("IS_ADMIN", false);
            startActivity(intent);
        });


        // Bottom - Profile
        navProfile.setOnClickListener(v -> {

            openProfile();
        });
    }


    private void refreshDashboard() {
        if (edtSearchService != null) {
            edtSearchService.setText("");
            edtSearchService.clearFocus();
        }
        
        loadCustomerName();
        
        if (scrollViewHome != null) {
            scrollViewHome.smoothScrollTo(0, 0);
        }
        
        Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
    }


    // Open profile
    private void openProfile() {

        Intent intent = new Intent(
                CustomerHomeActivity.this,
                ProfileActivity.class
        );

        intent.putExtra(
                "USER_EMAIL",
                userEmail
        );

        startActivity(intent);
    }


    // Load customer name
    private void loadCustomerName() {

        if (userEmail != null &&
                !userEmail.isEmpty()) {

            String customerName =
                    databaseHelper.getUserName(
                            userEmail
                    );

            txtWelcome.setText(
                    "Hello, " +
                            customerName +
                            " 👋"
            );

        } else {

            txtWelcome.setText(
                    "Hello, Customer 👋"
            );
        }
    }


    // Refresh after profile update
    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {

            loadCustomerName();
        }
    }
}