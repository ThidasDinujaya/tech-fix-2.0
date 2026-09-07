package com.example.techfix.features.booking.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;

public class AddReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etComment;
    private ImageView ivPhoto;
    private int bookingId;
    private Uri selectedImageUri;
    private DatabaseHelper dbHelper;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivPhoto.setImageURI(uri);
                    ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    
                    // Persist permission for local storage URI if needed (not strictly required for local viewing in same session but good practice)
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {}
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        dbHelper = new DatabaseHelper(this);
        bookingId = getIntent().getIntExtra("booking_id", -1);

        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etReviewComment);
        ivPhoto = findViewById(R.id.ivReviewPhoto);
        TextView tvBookingId = findViewById(R.id.tvReviewBookingId);

        tvBookingId.setText("Booking ID: TF" + (1000 + bookingId));

        ivPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        findViewById(R.id.btnSubmitReview).setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();
        String imageUriStr = selectedImageUri != null ? selectedImageUri.toString() : "";

        if (rating == 0) {
            Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.addReview(bookingId, rating, comment, imageUriStr)) {
            Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show();
        }
    }
}
