package com.example.techfix.features.auth.data;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.booking.ui.MyBookingsActivity;
import com.example.techfix.features.booking.ui.RepairHistoryActivity;
import com.example.techfix.features.branches.ui.BranchesActivity;
import com.example.techfix.features.services.ui.ServicesActivity;

public class CustomerHomeActivity extends AppCompatActivity {

    // Drawer
    private DrawerLayout customerDrawerLayout;

    // Header
    private ImageView imgCustomerMenu;
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
    private LinearLayout navHistory;
    private LinearLayout navProfile;

    // Drawer menu
    private TextView menuCustomerHome;
    private TextView menuCustomerProfile;
    private TextView menuCustomerRepairs;
    private TextView menuCustomerHistory;
    private TextView menuCustomerBranches;
    private TextView menuCustomerSupport;
    private TextView menuCustomerLogout;

    private TextView txtDrawerCustomer;

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
        // CONNECT DRAWER
        // =====================================

        customerDrawerLayout =
                findViewById(R.id.customerDrawerLayout);


        // =====================================
        // HEADER
        // =====================================

        imgCustomerMenu =
                findViewById(R.id.imgCustomerMenu);

        imgCustomerNotification =
                findViewById(R.id.imgCustomerNotification);


        // =====================================
        // CUSTOMER DETAILS
        // =====================================

        txtWelcome =
                findViewById(R.id.txtWelcome);

        txtDrawerCustomer =
                findViewById(R.id.txtDrawerCustomer);


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

        navHistory =
                findViewById(R.id.navHistory);

        navProfile =
                findViewById(R.id.navProfile);


        // =====================================
        // DRAWER MENU
        // =====================================

        menuCustomerHome =
                findViewById(R.id.menuCustomerHome);

        menuCustomerProfile =
                findViewById(R.id.menuCustomerProfile);

        menuCustomerRepairs =
                findViewById(R.id.menuCustomerRepairs);

        menuCustomerHistory =
                findViewById(R.id.menuCustomerHistory);

        menuCustomerBranches =
                findViewById(R.id.menuCustomerBranches);

        menuCustomerSupport =
                findViewById(R.id.menuCustomerSupport);

        menuCustomerLogout =
                findViewById(R.id.menuCustomerLogout);


        // =====================================
        // LOAD CUSTOMER NAME
        // =====================================

        loadCustomerName();


        // =====================================
        // OPEN DRAWER
        // =====================================

        imgCustomerMenu.setOnClickListener(v -> {

            customerDrawerLayout.openDrawer(
                    Gravity.START
            );
        });


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
        // BOTTOM - MY REPAIRS
        // =====================================

        navMyRepairs.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, MyBookingsActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // =====================================
        // BOTTOM - HISTORY
        // =====================================

        navHistory.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerHomeActivity.this, RepairHistoryActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // =====================================
        // BOTTOM - PROFILE
        // =====================================

        navProfile.setOnClickListener(v -> {

            openProfile();
        });


        // =====================================
        // DRAWER - HOME
        // =====================================

        menuCustomerHome.setOnClickListener(v -> {

            customerDrawerLayout.closeDrawer(
                    Gravity.START
            );
        });


        // =====================================
        // DRAWER - PROFILE
        // =====================================

        menuCustomerProfile.setOnClickListener(v -> {

            customerDrawerLayout.closeDrawer(
                    Gravity.START
            );

            openProfile();
        });


        // =====================================
        // DRAWER - MY REPAIRS
        // =====================================

        menuCustomerRepairs.setOnClickListener(v -> {
            customerDrawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(CustomerHomeActivity.this, MyBookingsActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // =====================================
        // DRAWER - HISTORY
        // =====================================

        menuCustomerHistory.setOnClickListener(v -> {
            customerDrawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(CustomerHomeActivity.this, RepairHistoryActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });


        // =====================================
        // DRAWER - BRANCHES
        // =====================================

        menuCustomerBranches.setOnClickListener(v -> {
            customerDrawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(CustomerHomeActivity.this, BranchesActivity.class);
            startActivity(intent);
        });


        // =====================================
        // DRAWER - CUSTOMER SUPPORT
        // =====================================

        menuCustomerSupport.setOnClickListener(v -> {

            customerDrawerLayout.closeDrawer(
                    Gravity.START
            );

            Toast.makeText(
                    CustomerHomeActivity.this,
                    "Customer Support",
                    Toast.LENGTH_SHORT
            ).show();
        });


        // =====================================
        // DRAWER - LOGOUT
        // =====================================

        menuCustomerLogout.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CustomerHomeActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
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

            txtDrawerCustomer.setText(
                    customerName
            );

        } else {

            txtWelcome.setText(
                    "Hello, Customer 👋"
            );

            txtDrawerCustomer.setText(
                    "Customer"
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


    // =========================================
    // BACK BUTTON
    // =========================================

    @Override
    public void onBackPressed() {

        if (customerDrawerLayout.isDrawerOpen(
                Gravity.START
        )) {

            customerDrawerLayout.closeDrawer(
                    Gravity.START
            );

        } else {

            super.onBackPressed();
        }
    }
}