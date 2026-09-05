package com.example.techfix.features.branches.ui;

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
import com.example.techfix.features.branches.data.SparePart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ManageSparePartsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SparePartAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_spare_parts);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvSpareParts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();

        FloatingActionButton fab = findViewById(R.id.fabAddSparePart);
        fab.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadData() {
        List<SparePart> partList = dbHelper.getAllSpareParts();
        adapter = new SparePartAdapter(partList, new SparePartAdapter.OnPartActionListener() {
            @Override
            public void onEdit(SparePart part) {
                showAddEditDialog(part);
            }

            @Override
            public void onDelete(SparePart part) {
                new AlertDialog.Builder(ManageSparePartsActivity.this)
                        .setTitle("Delete Part")
                        .setMessage("Are you sure you want to delete " + part.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (dbHelper.deleteSparePart(part.getId())) {
                                loadData();
                                Toast.makeText(ManageSparePartsActivity.this, "Part deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddEditDialog(SparePart part) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(part == null ? "Add New Part" : "Edit Part");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_spare_part, null);
        EditText etName = view.findViewById(R.id.etPartName);
        EditText etStock = view.findViewById(R.id.etPartStock);
        EditText etPrice = view.findViewById(R.id.etPartPrice);

        if (part != null) {
            etName.setText(part.getName());
            etStock.setText(String.valueOf(part.getStock()));
            etPrice.setText(String.valueOf(part.getPrice()));
        }

        builder.setView(view);
        builder.setPositiveButton(part == null ? "Add" : "Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String stockStr = etStock.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (!name.isEmpty() && !stockStr.isEmpty() && !priceStr.isEmpty()) {
                try {
                    int stock = Integer.parseInt(stockStr);
                    double price = Double.parseDouble(priceStr);
                    boolean success;
                    
                    if (part == null) {
                        success = dbHelper.addSparePart(name, stock, price);
                    } else {
                        success = dbHelper.updateSparePart(part.getId(), name, stock, price);
                    }

                    if (success) {
                        loadData();
                        Toast.makeText(this, part == null ? "Part added" : "Part updated", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
