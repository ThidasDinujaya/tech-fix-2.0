package com.example.techfix.features.services.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.features.services.ServiceAdapter;
import com.example.techfix.features.services.viewmodel.ServicesViewModel;

import java.util.ArrayList;

// Screen that displays the list of repair services filtered by category
public class ServicesActivity extends AppCompatActivity {

    private ServicesViewModel viewModel;
    private ServiceAdapter adapter;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        category = getIntent().getStringExtra("CATEGORY");
        updateTitle();
        setupRecyclerView();
        setupViewModel();
    }

    private void updateTitle() {
        TextView tvTitle = findViewById(R.id.toolbarServices).findViewById(R.id.tvToolbarTitle);
        if (tvTitle != null && category != null) {
            tvTitle.setText(category + " Services");
        }
    }

    // Configures the list layout and click behavior
    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rvServices);
        rv.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new ServiceAdapter(new ArrayList<>(), service -> {
            // Opens the detailed view for the selected service
            Intent intent = new Intent(this, ServiceDetailsActivity.class);
            intent.putExtra("service_data", service);
            startActivity(intent);
        });
        rv.setAdapter(adapter);
    }

    // Connects the UI to the ViewModel to observe data changes
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ServicesViewModel.class);
        
        // Observe the services list and update the adapter when it changes
        viewModel.getServices().observe(this, services -> {
            if (services != null) {
                adapter = new ServiceAdapter(services, service -> {
                    Intent intent = new Intent(this, ServiceDetailsActivity.class);
                    intent.putExtra("service_data", service);
                    startActivity(intent);
                });
                RecyclerView rv = findViewById(R.id.rvServices);
                if (rv != null) {
                    rv.setAdapter(adapter);
                }
            }
        });

        viewModel.loadServices(category);
    }
}
