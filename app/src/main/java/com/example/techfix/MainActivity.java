package com.example.techfix;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.techfix.features.branches.ui.BranchesActivity;
import com.example.techfix.features.branches.ui.ManageSparePartsActivity;
import com.example.techfix.features.branches.ui.ManageTechniciansActivity;

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

        findViewById(R.id.btnBranches).setOnClickListener(v ->
                startActivity(new Intent(this, BranchesActivity.class)));
        findViewById(R.id.btnTechnicians).setOnClickListener(v ->
                startActivity(new Intent(this, ManageTechniciansActivity.class)));
        findViewById(R.id.btnSpareParts).setOnClickListener(v ->
                startActivity(new Intent(this, ManageSparePartsActivity.class)));
    }
}
