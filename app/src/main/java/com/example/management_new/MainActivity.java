package com.example.management_new;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.management_new.Activity.BranchesActivity;
import com.example.management_new.Activity.ManageSparePartsActivity;
import com.example.management_new.Activity.ManageTechniciansActivity;
import com.example.management_new.Activity.MapNearestBranchActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up click listeners for Member 3 features
        findViewById(R.id.btnBranches).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, BranchesActivity.class))
        );

        findViewById(R.id.btnTechnicians).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ManageTechniciansActivity.class))
        );

        findViewById(R.id.btnSpareParts).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ManageSparePartsActivity.class))
        );

        findViewById(R.id.btnMap).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MapNearestBranchActivity.class))
        );
    }
}