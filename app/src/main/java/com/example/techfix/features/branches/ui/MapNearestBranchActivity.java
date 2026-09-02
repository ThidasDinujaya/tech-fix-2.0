package com.example.techfix.features.branches.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.branches.data.Branch;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapNearestBranchActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private CardView cardNearestBranch;
    private TextView tvNearestBranchName, tvNearestDistance;
    private EditText etSearchLocation;

    // Map to associate markers with Branch objects
    private Map<Marker, Branch> markerBranchMap = new HashMap<>();
    private List<Branch> branchList;
    private LatLng currentSearchLatLng = null;
    private final ExecutorService geocoderExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_nearest_branch);

        dbHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        cardNearestBranch = findViewById(R.id.cardNearestBranch);
        tvNearestBranchName = findViewById(R.id.tvNearestBranchName);
        tvNearestDistance = findViewById(R.id.tvNearestDistance);
        etSearchLocation = findViewById(R.id.etSearchLocation);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        findViewById(R.id.btnGetLocation).setOnClickListener(v -> checkPermissionAndFindNearest());
        findViewById(R.id.btnSearchLocation).setOnClickListener(v -> searchCustomLocation());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        loadBranchMarkers();

        // 1. CLICK BRANCH MARKER ON MAP
        mMap.setOnMarkerClickListener(marker -> {
            Branch selectedBranch = markerBranchMap.get(marker);
            if (selectedBranch != null) {
                displayBranchDetails(selectedBranch);
            }
            return false;
        });
    }

    private void loadBranchMarkers() {
        branchList = dbHelper.getAllBranches();
        markerBranchMap.clear();

        for (Branch b : branchList) {
            LatLng pos = new LatLng(b.getLatitude(), b.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title(b.getName()).snippet(b.getAddress()));
            if (marker != null) {
                markerBranchMap.put(marker, b);
            }
        }

        if (!branchList.isEmpty()) {
            LatLng firstBranch = new LatLng(branchList.get(0).getLatitude(), branchList.get(0).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstBranch, 9f));
        }
    }

    // Display info when branch marker is clicked
    private void displayBranchDetails(Branch branch) {
        tvNearestBranchName.setText(branch.getName());

        if (currentSearchLatLng != null) {
            float[] results = new float[1];
            Location.distanceBetween(
                    currentSearchLatLng.latitude, currentSearchLatLng.longitude,
                    branch.getLatitude(), branch.getLongitude(),
                    results
            );
            double distanceKm = results[0] / 1000.0;
            tvNearestDistance.setText(String.format(Locale.getDefault(), "%.1f km away", distanceKm));
        } else {
            tvNearestDistance.setText(branch.getAddress());
        }

        cardNearestBranch.setVisibility(View.VISIBLE);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(branch.getLatitude(), branch.getLongitude()), 13f));
    }

    // 2. SEARCH TYPED LOCATION & CALCULATE DISTANCE TO NEAREST BRANCH
    private void searchCustomLocation() {
        String locationName = etSearchLocation.getText().toString().trim();
        if (locationName.isEmpty()) {
            Toast.makeText(this, "Please enter a location to search", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mMap == null) {
            Toast.makeText(this, "Map is still loading. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        geocoderExecutor.execute(() -> {
            try {
                List<Address> addresses = new Geocoder(this, Locale.getDefault())
                        .getFromLocationName(locationName, 1);
                runOnUiThread(() -> {
                    if (addresses == null || addresses.isEmpty()) {
                        Toast.makeText(this, "Location not found. Try a different name.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Address address = addresses.get(0);
                    currentSearchLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentSearchLatLng)
                            .title("Searched Location: " + locationName));
                    findAndShowNearestBranchFromLocation(currentSearchLatLng);
                });
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Geocoder service error. Try again.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void findAndShowNearestBranchFromLocation(LatLng fromLatLng) {
        if (branchList == null || branchList.isEmpty()) return;

        Branch nearestBranch = null;
        float minDistanceMeters = Float.MAX_VALUE;

        for (Branch branch : branchList) {
            float[] results = new float[1];
            Location.distanceBetween(
                    fromLatLng.latitude, fromLatLng.longitude,
                    branch.getLatitude(), branch.getLongitude(),
                    results
            );

            if (results[0] < minDistanceMeters) {
                minDistanceMeters = results[0];
                nearestBranch = branch;
            }
        }

        if (nearestBranch != null) {
            double distanceKm = minDistanceMeters / 1000.0;
            tvNearestBranchName.setText("Nearest: " + nearestBranch.getName());
            tvNearestDistance.setText(String.format(Locale.getDefault(), "%.1f km away from searched location", distanceKm));
            cardNearestBranch.setVisibility(View.VISIBLE);

            LatLng branchLatLng = new LatLng(nearestBranch.getLatitude(), nearestBranch.getLongitude());
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(fromLatLng)
                    .include(branchLatLng)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
        }
    }

    private void checkPermissionAndFindNearest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                getUserGpsLocation();
            }
        }
    }

    private void getUserGpsLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, userLocation -> {
                if (userLocation != null) {
                    currentSearchLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                    findAndShowNearestBranchFromLocation(currentSearchLatLng);
                } else {
                    Toast.makeText(this, "Turn on GPS to fetch your location", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPermissionAndFindNearest();
        }
    }

    @Override
    protected void onDestroy() {
        geocoderExecutor.shutdownNow();
        super.onDestroy();
    }
}
