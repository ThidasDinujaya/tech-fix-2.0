package com.example.techfix.features.admin.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.techfix.R;

public class AdminDeviceManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_device_management);

        findViewById(R.id.cardManageBrands).setOnClickListener(v -> 
            startActivity(new Intent(this, AdminManageBrandsActivity.class)));

        findViewById(R.id.cardManageModels).setOnClickListener(v -> 
            startActivity(new Intent(this, AdminManageModelsActivity.class)));
            
        Toolbar toolbar = findViewById(R.id.toolbarDeviceMgmt);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}
