package com.example.techfix.features.auth.data;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.booking.ui.MyBookingsActivity;
import com.example.techfix.features.branches.ui.BranchesActivity;
import com.example.techfix.features.services.ui.ServicesActivity;

public class CustomerHomeActivity extends AppCompatActivity {

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

        // =====================================
        // GET LOGGED-IN USER EMAIL
        // =====================================

        userEmail =
                getIntent().getStringExtra("USER_EMAIL");


        // =====================================
        // HEADER
        // =====================================

        imgCustomerNotification =
                findViewById(R.id.imgCustomerNotification);


        // =====================================
        // CUSTOMER DETAILS
        // =====================================

        txtWelcome =
                findViewById(R.id.txtWelcome);


        // =====================================
        // SEARCH / VIEW ALL
        // =====================================

        edtSearchService =
                findViewById(R.id.edtSearchService);

        txtViewAll =
                findViewById(R.id.txtViewAll);


        // =====================================
        // SERVICE CARDS
        // =====================================

        cardPhoneRepair =
                findViewById(R.id.cardPhoneRepair);

        cardLaptopRepair =
                findViewById(R.id.cardLaptopRepair);

        cardDesktopRepair =
                findViewById(R.id.cardDesktopRepair);

        cardTabletRepair =
                findViewById(R.id.cardTabletRepair);


        // =====================================
        // BOTTOM NAVIGATION
        // =====================================

        navHome =
                findViewById(R.id.navHome);

        navMyRepairs =
                findViewById(R.id.navMyRepairs);

        navBranches =
                findViewById(R.id.navBranches);

        navProfile =
                findViewById(R.id.navProfile);


        // =====================================
        // LOAD CUSTOMER NAME
        // =====================================

        loadCustomerName();


        // =====================================
        // NOTIFICATIONS
        // =====================================

        imgCustomerNotification.setOnClickListener(v -> {

            Toast.makeText(
                    CustomerHomeActivity.this,
                    "No new notifications",
                    Toast.LENGTH_SHORT
            ).show();
        });


        // =====================================
        // VIEW ALL SERVICES
        // =====================================

        txtViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, ServicesActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // =====================================
        // PHONE REPAIR
        // =====================================

        cardPhoneRepair.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, ServicesActivity.class);
            intent.putExtra("CATEGORY", "Phone");
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // =====================================
        // LAPTOP REPAIR
        // =====================================

        cardLaptopRepair.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, ServicesActivity.class);
            intent.putExtra("CATEGORY", "Laptop");
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // =====================================
        // DESKTOP REPAIR
        // =====================================

        cardDesktopRepair.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, ServicesActivity.class);
            intent.putExtra("CATEGORY", "Desktop");
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // =====================================
        // TABLET REPAIR
        // =====================================

        cardTabletRepair.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, ServicesActivity.class);
            intent.putExtra("CATEGORY", "Tablet");
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // =====================================
        // BOTTOM - HOME
        // =====================================

        navHome.setOnClickListener(v -> {

            Toast.makeText(
                    CustomerHomeActivity.this,
                    "Home",
                    Toast.LENGTH_SHORT
            ).show();
        });


        // =====================================
        // BOTTOM - REPAIRS
        // =====================================

        navMyRepairs.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, MyBookingsActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // =====================================
        // BOTTOM - BRANCHES
        // =====================================

        navBranches.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, BranchesActivity.class);
            intent.putExtra("IS_ADMIN", false);
            startActivity(intent);
        });


        // =====================================
        // BOTTOM - PROFILE
        // =====================================

        navProfile.setOnClickListener(v -> {

            openProfile();
        });
    }


    // =========================================
    // OPEN PROFILE
    // =========================================

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


    // =========================================
    // LOAD CUSTOMER NAME
    // =========================================

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


    // =========================================
    // REFRESH AFTER PROFILE UPDATE
    // =========================================

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {

            loadCustomerName();
        }
    }
}