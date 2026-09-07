package com.example.techfix.features.booking.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.booking.data.Review;

public class ViewReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_review);

        int bookingId = getIntent().getIntExtra("booking_id", -1);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Review review = dbHelper.getReviewByBookingId(bookingId);

        if (review == null) {
            Toast.makeText(this, "Review not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvBookingId = findViewById(R.id.tvViewReviewBookingId);
        RatingBar ratingBar = findViewById(R.id.viewRatingBar);
        TextView tvDate = findViewById(R.id.tvViewReviewDate);
        TextView tvComment = findViewById(R.id.tvViewReviewComment);
        ImageView ivPhoto = findViewById(R.id.ivViewReviewPhoto);
        TextView tvPhotoLabel = findViewById(R.id.tvReviewPhotoLabel);

        tvBookingId.setText("Booking ID: TF" + (1000 + bookingId));
        ratingBar.setRating(review.getRating());
        tvDate.setText("Reviewed on: " + review.getReviewDate());
        tvComment.setText(review.getComment());

        if (review.getImageUri() != null && !review.getImageUri().isEmpty()) {
            try {
                ivPhoto.setImageURI(Uri.parse(review.getImageUri()));
                ivPhoto.setVisibility(View.VISIBLE);
                tvPhotoLabel.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                ivPhoto.setVisibility(View.GONE);
                tvPhotoLabel.setVisibility(View.GONE);
            }
        } else {
            ivPhoto.setVisibility(View.GONE);
            tvPhotoLabel.setVisibility(View.GONE);
        }
    }
}
