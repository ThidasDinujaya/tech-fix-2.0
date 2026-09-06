package com.example.techfix.features.admin.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.admin.data.PartQuality;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class AdminManageQualitiesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PartQualityAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_qualities);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvQualities);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        FloatingActionButton fab = findViewById(R.id.fabAddQuality);
        fab.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadData() {
        List<PartQuality> qualityList = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllQualities()) {
            while (cursor.moveToNext()) {
                qualityList.add(new PartQuality(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME))
                ));
            }
        }
        adapter = new PartQualityAdapter(qualityList, new PartQualityAdapter.OnQualityActionListener() {
            @Override
            public void onEdit(PartQuality quality) {
                showAddEditDialog(quality);
            }

            @Override
            public void onDelete(PartQuality quality) {
                new AlertDialog.Builder(AdminManageQualitiesActivity.this)
                        .setTitle("Delete Quality")
                        .setMessage("Are you sure you want to delete " + quality.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (dbHelper.deleteQuality(quality.getId())) {
                                loadData();
                                Toast.makeText(AdminManageQualitiesActivity.this, "Quality deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddEditDialog(PartQuality quality) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(quality == null ? "Add Quality" : "Edit Quality");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_quality, null);
        EditText etName = view.findViewById(R.id.etQualityName);

        if (quality != null) {
            etName.setText(quality.getName());
        }

        builder.setView(view);
        builder.setPositiveButton(quality == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();

            if (!name.isEmpty()) {
                boolean success;
                if (quality == null) {
                    success = dbHelper.addQuality(name);
                } else {
                    success = dbHelper.updateQuality(quality.getId(), name);
                }

                if (success) {
                    loadData();
                    Toast.makeText(this, quality == null ? "Quality added" : "Quality updated", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
