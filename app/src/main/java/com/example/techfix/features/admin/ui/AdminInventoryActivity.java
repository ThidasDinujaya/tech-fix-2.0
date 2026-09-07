package com.example.techfix.features.admin.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.techfix.R;
import com.example.techfix.features.branches.ui.ManageSparePartsActivity;

public class AdminInventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_inventory);

        findViewById(R.id.cardInvBrands).setOnClickListener(v -> 
            startActivity(new Intent(this, AdminManageBrandsActivity.class)));

        findViewById(R.id.cardInvModels).setOnClickListener(v -> 
            startActivity(new Intent(this, AdminManageModelsActivity.class)));

        findViewById(R.id.cardInvQualities).setOnClickListener(v -> 
            startActivity(new Intent(this, AdminManageQualitiesActivity.class)));

        findViewById(R.id.cardInvParts).setOnClickListener(v -> 
            startActivity(new Intent(this, ManageSparePartsActivity.class)));
    }
}
