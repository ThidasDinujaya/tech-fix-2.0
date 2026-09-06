package com.example.techfix.features.branches.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.techfix.R;
import com.example.techfix.features.branches.data.Branch;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BranchDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Branch branch;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_details);

        branch = (Branch) getIntent().getSerializableExtra("branch_data");
        if (branch == null) {
            finish();
            return;
        }

        setupToolbar();
        populateDetails(branch);

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupToolbar() {
        // Toolbar styled in XML to match Branches UI
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        // Place marker at branch location
        LatLng branchLocation = new LatLng(branch.getLatitude(), branch.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(branchLocation)
                .title(branch.getName()));
        
        // Move camera to branch location with zoom level 15
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(branchLocation, 15f));
        
        // Enable basic UI controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false); // We have our own button
    }

    private void populateDetails(Branch branch) {
        TextView tvName = findViewById(R.id.tvDetailBranchName);
        TextView tvAddr = findViewById(R.id.tvDetailBranchAddress);
        TextView tvPhone1 = findViewById(R.id.tvDetailPhone1);
        TextView tvPhone2 = findViewById(R.id.tvDetailPhone2);
        TextView tvMonFri = findViewById(R.id.tvDetailMonFri);
        TextView tvSat = findViewById(R.id.tvDetailSat);
        TextView tvSun = findViewById(R.id.tvDetailSun);

        tvName.setText(branch.getName());
        tvAddr.setText(branch.getAddress());
        
        tvPhone1.setText(branch.getPhone());
        tvPhone1.setOnClickListener(v -> makeCall(branch.getPhone()));

        if (branch.getPhone2() != null && !branch.getPhone2().trim().isEmpty()) {
            tvPhone2.setVisibility(View.VISIBLE);
            tvPhone2.setText(branch.getPhone2());
            tvPhone2.setOnClickListener(v -> makeCall(branch.getPhone2()));
        } else {
            tvPhone2.setVisibility(View.GONE);
        }

        tvMonFri.setText("Monday - Friday: " + branch.getHoursMonFri());
        tvSat.setText("Saturday: " + branch.getHoursSat());
        tvSun.setText("Sunday: " + branch.getHoursSun());

        findViewById(R.id.btnViewOnMap).setOnClickListener(v -> {
            String link = branch.getMapLink();
            if (link != null && !link.isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Invalid map link", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Map link not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeCall(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }
}
