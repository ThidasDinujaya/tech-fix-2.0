package com.example.techfix.features.branches.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.branches.data.Branch;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BranchesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BranchAdapter adapter;
    private DatabaseHelper dbHelper;
    private boolean isAdmin = false;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSION_ID = 44;
    private EditText etSearchLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);

        isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        recyclerView = findViewById(R.id.recyclerViewBranches);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        etSearchLocation = findViewById(R.id.etSearchLocation);

        dbHelper = new DatabaseHelper(this);
        loadData();

        FloatingActionButton fab = findViewById(R.id.fabAddBranch);
        if (isAdmin) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> showAddEditDialog(null));
        } else {
            fab.setVisibility(View.GONE);
        }

        findViewById(R.id.btnFindNearest).setOnClickListener(v -> getLastLocation());
        findViewById(R.id.btnSearchNearest).setOnClickListener(v -> searchLocation());
    }

    private void loadData() {
        List<Branch> branchList = dbHelper.getAllBranches();
        adapter = new BranchAdapter(branchList, isAdmin, new BranchAdapter.OnBranchActionListener() {
            @Override
            public void onEdit(Branch branch) {
                if (isAdmin) showAddEditDialog(branch);
            }

            @Override
            public void onDelete(Branch branch) {
                if (isAdmin) {
                    new AlertDialog.Builder(BranchesActivity.this)
                            .setTitle("Delete Branch")
                            .setMessage("Are you sure you want to delete " + branch.getName() + "?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                if (dbHelper.deleteBranch(branch.getId())) {
                                    loadData();
                                    Toast.makeText(BranchesActivity.this, "Branch deleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void searchLocation() {
        String locationName = etSearchLocation.getText().toString().trim();
        if (locationName.isEmpty()) {
            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                findNearestBranch(address.getLatitude(), address.getLongitude(), "typed location");
            } else {
                Toast.makeText(this, "Location not found. Try a more specific name.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Geocoder service unavailable. Check your connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLastLocation() {
        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            
            // Try to get last location
            fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location == null) {
                    // Request new location if last location is null (common on first run)
                    requestNewLocationData();
                } else {
                    findNearestBranch(location.getLatitude(), location.getLongitude(), "your location");
                }
            });
        } else {
            requestPermissions();
        }
    }

    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMaxUpdates(1)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    findNearestBranch(location.getLatitude(), location.getLongitude(), "your location");
                } else {
                    Toast.makeText(BranchesActivity.this, "Unable to detect location. Please check GPS.", Toast.LENGTH_LONG).show();
                }
            }
        }, Looper.myLooper());
    }

    private void findNearestBranch(double lat, double lon, String source) {
        List<Branch> branchList = dbHelper.getAllBranches();
        if (branchList.isEmpty()) {
            Toast.makeText(this, "No branches found in database", Toast.LENGTH_SHORT).show();
            return;
        }

        Branch nearest = null;
        float minDistance = Float.MAX_VALUE;

        for (Branch b : branchList) {
            float[] results = new float[1];
            Location.distanceBetween(lat, lon, b.getLatitude(), b.getLongitude(), results);
            if (results[0] < minDistance) {
                minDistance = results[0];
                nearest = b;
            }
        }

        if (nearest != null) {
            final Branch finalNearest = nearest;
            String distanceStr = String.format(Locale.getDefault(), "%.1f km away", minDistance / 1000);
            new AlertDialog.Builder(this)
                    .setTitle("Nearest Branch Found")
                    .setMessage(nearest.getName() + " is " + distanceStr + " from " + source + ".")
                    .setPositiveButton("View Details", (dialog, which) -> {
                        Intent intent = new Intent(this, BranchDetailsActivity.class);
                        intent.putExtra("branch_data", finalNearest);
                        startActivity(intent);
                    })
                    .setNegativeButton("Close", null)
                    .show();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private void showAddEditDialog(Branch branch) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(branch == null ? "Add New Branch" : "Edit Branch");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_branch, null);
        EditText etName = view.findViewById(R.id.etBranchName);
        EditText etAddr = view.findViewById(R.id.etBranchAddress);
        EditText etPhone = view.findViewById(R.id.etBranchPhone);
        EditText etPhone2 = view.findViewById(R.id.etBranchPhone2);
        EditText etMonFri = view.findViewById(R.id.etHoursMonFri);
        EditText etSat = view.findViewById(R.id.etHoursSat);
        EditText etSun = view.findViewById(R.id.etHoursSun);
        EditText etMapLink = view.findViewById(R.id.etBranchMapLink);
        EditText etLat = view.findViewById(R.id.etBranchLat);
        EditText etLon = view.findViewById(R.id.etBranchLon);

        if (branch != null) {
            etName.setText(branch.getName());
            etAddr.setText(branch.getAddress());
            etPhone.setText(branch.getPhone());
            etPhone2.setText(branch.getPhone2());
            etMonFri.setText(branch.getHoursMonFri());
            etSat.setText(branch.getHoursSat());
            etSun.setText(branch.getHoursSun());
            etMapLink.setText(branch.getMapLink());
            etLat.setText(String.valueOf(branch.getLatitude()));
            etLon.setText(String.valueOf(branch.getLongitude()));
        }

        builder.setView(view);
        builder.setPositiveButton(branch == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String addr = etAddr.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String phone2 = etPhone2.getText().toString().trim();
            String monFri = etMonFri.getText().toString().trim();
            String sat = etSat.getText().toString().trim();
            String sun = etSun.getText().toString().trim();
            String mapLink = etMapLink.getText().toString().trim();
            String latStr = etLat.getText().toString().trim();
            String lonStr = etLon.getText().toString().trim();

            if (!name.isEmpty() && !addr.isEmpty() && !phone.isEmpty()) {
                try {
                    double lat = latStr.isEmpty() ? 0.0 : Double.parseDouble(latStr);
                    double lon = lonStr.isEmpty() ? 0.0 : Double.parseDouble(lonStr);
                    
                    boolean success;
                    if (branch == null) {
                        success = dbHelper.addBranch(name, addr, phone, phone2, monFri, sat, sun, mapLink, lat, lon);
                    } else {
                        success = dbHelper.updateBranch(branch.getId(), name, addr, phone, phone2, monFri, sat, sun, mapLink, lat, lon);
                    }

                    if (success) {
                        loadData();
                        Toast.makeText(this, branch == null ? "Branch added" : "Branch updated", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid coordinate format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Name, Address, and Phone are required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
