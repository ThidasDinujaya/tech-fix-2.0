package com.example.techfix.features.auth.data;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;

    private TextView txtChangePhoto;

    private EditText edtProfileName;
    private EditText edtProfileEmail;
    private EditText edtProfilePhone;

    private Button btnUpdateProfile;
    private Button btnProfileLogout;

    private DatabaseHelper databaseHelper;

    private String userEmail;

    private SharedPreferences sharedPreferences;

    private ActivityResultLauncher<String[]> imagePickerLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);


        // Database
        databaseHelper =
                new DatabaseHelper(this);


        // Shared preferences for profile image URI
        sharedPreferences =
                getSharedPreferences(
                        "TechFixProfile",
                        MODE_PRIVATE
                );


        // Connect XML
        imgProfile =
                findViewById(R.id.imgProfile);

        txtChangePhoto =
                findViewById(R.id.txtChangePhoto);

        edtProfileName =
                findViewById(R.id.edtProfileName);

        edtProfileEmail =
                findViewById(R.id.edtProfileEmail);

        edtProfilePhone =
                findViewById(R.id.edtProfilePhone);

        btnUpdateProfile =
                findViewById(R.id.btnUpdateProfile);

        btnProfileLogout =
                findViewById(R.id.btnProfileLogout);


        // Get logged-in email
        userEmail =
                getIntent().getStringExtra(
                        "USER_EMAIL"
                );


        // Image picker
        imagePickerLauncher =
                registerForActivityResult(

                        new ActivityResultContracts.OpenDocument(),

                        uri -> {

                            if (uri != null) {

                                try {

                                    getContentResolver()
                                            .takePersistableUriPermission(
                                                    uri,
                                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                                            );

                                } catch (SecurityException ignored) {

                                }


                                // Show selected image
                                imgProfile.setPadding(
                                        0,
                                        0,
                                        0,
                                        0
                                );

                                imgProfile.setImageURI(uri);


                                // Save image URI
                                saveProfileImage(uri);


                                Toast.makeText(
                                        ProfileActivity.this,
                                        "Profile photo updated",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );


        // Load profile
        loadUserDetails();

        loadProfileImage();


        // Change photo
        txtChangePhoto.setOnClickListener(v -> {

            imagePickerLauncher.launch(
                    new String[]{"image/*"}
            );
        });


        imgProfile.setOnClickListener(v -> {

            imagePickerLauncher.launch(
                    new String[]{"image/*"}
            );
        });


        // Update profile
        btnUpdateProfile.setOnClickListener(v -> {

            updateProfile();
        });


        // Logout
        btnProfileLogout.setOnClickListener(v -> {

            logoutUser();
        });
    }


    // Load user details from SQLite
    private void loadUserDetails() {

        if (userEmail == null ||
                userEmail.isEmpty()) {

            Toast.makeText(
                    ProfileActivity.this,
                    "Unable to load profile",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        Cursor cursor =
                databaseHelper.getUserByEmail(
                        userEmail
                );


        if (cursor != null &&
                cursor.moveToFirst()) {


            int nameIndex =
                    cursor.getColumnIndex(
                            DatabaseHelper.COL_NAME
                    );


            int emailIndex =
                    cursor.getColumnIndex(
                            DatabaseHelper.COL_EMAIL
                    );


            int phoneIndex =
                    cursor.getColumnIndex(
                            DatabaseHelper.COL_PHONE
                    );


            if (nameIndex >= 0) {

                String name =
                        cursor.getString(nameIndex);

                edtProfileName.setText(name);
            }


            if (emailIndex >= 0) {

                String email =
                        cursor.getString(emailIndex);

                edtProfileEmail.setText(email);
            }


            if (phoneIndex >= 0) {

                String phone =
                        cursor.getString(phoneIndex);

                edtProfilePhone.setText(phone);
            }


        } else {

            Toast.makeText(
                    ProfileActivity.this,
                    "User details not found",
                    Toast.LENGTH_SHORT
            ).show();
        }


        if (cursor != null) {

            cursor.close();
        }
    }


    // Update user profile
    private void updateProfile() {

        String name =
                edtProfileName
                        .getText()
                        .toString()
                        .trim();


        String phone =
                edtProfilePhone
                        .getText()
                        .toString()
                        .trim();


        // Name validation
        if (name.isEmpty()) {

            edtProfileName.setError(
                    "Please enter your name"
            );

            edtProfileName.requestFocus();

            return;
        }


        // Phone validation
        if (phone.isEmpty()) {

            edtProfilePhone.setError(
                    "Please enter your phone number"
            );

            edtProfilePhone.requestFocus();

            return;
        }


        if (phone.length() != 10) {

            edtProfilePhone.setError(
                    "Phone number must contain 10 digits"
            );

            edtProfilePhone.requestFocus();

            return;
        }


        boolean updated =
                databaseHelper.updateUserProfile(
                        userEmail,
                        name,
                        phone
                );


        if (updated) {

            Toast.makeText(
                    ProfileActivity.this,
                    "Profile Updated Successfully",
                    Toast.LENGTH_SHORT
            ).show();


            loadUserDetails();

        } else {

            Toast.makeText(
                    ProfileActivity.this,
                    "Profile Update Failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // Save profile image URI
    private void saveProfileImage(Uri uri) {

        if (userEmail == null) {
            return;
        }


        SharedPreferences.Editor editor =
                sharedPreferences.edit();


        editor.putString(
                "PROFILE_IMAGE_" + userEmail,
                uri.toString()
        );


        editor.apply();
    }


    // Load profile image
    private void loadProfileImage() {

        if (userEmail == null) {
            return;
        }


        String imageUri =
                sharedPreferences.getString(
                        "PROFILE_IMAGE_" + userEmail,
                        null
                );


        if (imageUri != null) {

            try {

                Uri uri =
                        Uri.parse(imageUri);


                imgProfile.setPadding(
                        0,
                        0,
                        0,
                        0
                );


                imgProfile.setImageURI(uri);


            } catch (Exception e) {

                imgProfile.setImageResource(
                        R.drawable.ic_profile
                );
            }
        }
    }


    // Logout user
    private void logoutUser() {

        Toast.makeText(
                ProfileActivity.this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();


        Intent intent =
                new Intent(
                        ProfileActivity.this,
                        LoginActivity.class
                );


        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );


        startActivity(intent);

        finish();
    }
}