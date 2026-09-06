package com.example.techfix.features.branches.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.techfix.R;
import com.example.techfix.features.branches.data.Branch;

public class BranchDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_details);

        Branch branch = (Branch) getIntent().getSerializableExtra("branch_data");
        if (branch == null) {
            finish();
            return;
        }

        setupToolbar();
        populateDetails(branch);
    }

    private void setupToolbar() {
        // No setSupportActionBar call to keep it consistent with Branches UI
        // and avoid default system title being displayed
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
