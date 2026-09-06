package com.example.techfix.features.admin.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.branches.data.SparePart;
import java.util.List;

public class AdminViewInventoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_inventory);

        findViewById(R.id.btnBackViewInv).setOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvUnifiedInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUnifiedData();
    }

    private void loadUnifiedData() {
        List<SparePart> parts = dbHelper.getAllSpareParts();
        UnifiedInventoryAdapter adapter = new UnifiedInventoryAdapter(parts);
        recyclerView.setAdapter(adapter);
    }
}
