package com.example.techfix.features.booking.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etComment;
    private ImageView ivPhoto;
    private int bookingId;
    private Uri capturedImageUri;
    private String selectedImagePath = "";
    private DatabaseHelper dbHelper;

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImagePath = uri.toString();
                    ivPhoto.setImageURI(uri);
                    ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {}
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && capturedImageUri != null) {
                    selectedImagePath = capturedImageUri.toString();
                    ivPhoto.setImageURI(capturedImageUri);
                    ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
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

        ivPhoto.setOnClickListener(v -> showImagePickerDialog());
        findViewById(R.id.btnSubmitReview).setOnClickListener(v -> submitReview());
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Add Photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        galleryLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            capturedImageUri = FileProvider.getUriForFile(this, "com.example.techfix.fileprovider", photoFile);
            cameraLauncher.launch(capturedImageUri);
        } catch (IOException e) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();
        String imageUriStr = selectedImagePath;

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
