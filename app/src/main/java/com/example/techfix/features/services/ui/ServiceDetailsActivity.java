package com.example.techfix.features.services.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.techfix.R;
import com.example.techfix.features.booking.ui.BookRepairActivity;
import com.example.techfix.features.services.data.Service;

// Detailed view for a service using web image loading
public class ServiceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        Service service = (Service) getIntent().getSerializableExtra("service_data");
        if (service != null) {
            populateDetails(service);
        }

        Button btnBook = findViewById(R.id.btnBookService);
        btnBook.setOnClickListener(v -> {
            // Opens the booking form and passes the selected service ID
            Intent intent = new Intent(this, BookRepairActivity.class);
            if (service != null) {
                intent.putExtra("selected_service_id", service.getId());
            }
            startActivity(intent);
        });
    }

    private void populateDetails(Service service) {
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvPrice = findViewById(R.id.tvDetailPrice);
        TextView tvDesc = findViewById(R.id.tvDetailDesc);
        ImageView ivHeader = findViewById(R.id.ivDetailHeader);

        tvName.setText(service.getName());
        tvPrice.setText(String.format("From LKR %,.2f", service.getPrice()));
        tvDesc.setText(service.getDescription());

        // Load high-resolution web header image with robust caching
        Glide.with(this)
            .load(service.getImageUrl())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .centerCrop()
            .into(ivHeader);
    }
}
