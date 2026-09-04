package com.example.techfix.features.admin.data;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix.R;

public class AdminProfileActivity extends AppCompatActivity {

    private ImageView btnBackAdminProfile;
    private EditText edtAdminName;
    private EditText edtAdminProfileEmail;
    private EditText edtAdminPhone;
    private EditText edtAdminRole;

    private Button btnUpdateAdminProfile;
    private Button btnChangeAdminPassword;
    private Button btnAdminProfileLogout;

    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME =
            "TechFixAdminProfile";

    private static final String ADMIN_EMAIL =
            "admin@techfix.com";

    private static final String ADMIN_ROLE =
            "Administrator";

    private static final String DEFAULT_ADMIN_PASSWORD =
            "admin123";

    private static final String KEY_ADMIN_PASSWORD =
            "ADMIN_PASSWORD";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_profile);

        sharedPreferences =
                getSharedPreferences(
                        PREF_NAME,
                        MODE_PRIVATE
                );

        btnBackAdminProfile =
                findViewById(R.id.btnBackAdminProfile);

        edtAdminName =
                findViewById(R.id.edtAdminName);

        edtAdminProfileEmail =
                findViewById(R.id.edtAdminProfileEmail);

        edtAdminPhone =
                findViewById(R.id.edtAdminPhone);

        edtAdminRole =
                findViewById(R.id.edtAdminRole);

        btnUpdateAdminProfile =
                findViewById(R.id.btnUpdateAdminProfile);

        btnChangeAdminPassword =
                findViewById(R.id.btnChangeAdminPassword);

        btnAdminProfileLogout =
                findViewById(R.id.btnAdminProfileLogout);

        loadAdminProfile();

        btnBackAdminProfile.setOnClickListener(v -> finish());

        btnUpdateAdminProfile.setOnClickListener(v ->
                updateAdminProfile()
        );

        btnChangeAdminPassword.setOnClickListener(v ->
                showChangePasswordDialog()
        );

        btnAdminProfileLogout.setOnClickListener(v ->
                logoutAdmin()
        );
    }


    private void loadAdminProfile() {

        String adminName =
                sharedPreferences.getString(
                        "ADMIN_NAME",
                        "TechFix Administrator"
                );

        String adminPhone =
                sharedPreferences.getString(
                        "ADMIN_PHONE",
                        "0771234567"
                );

        edtAdminName.setText(adminName);
        edtAdminProfileEmail.setText(ADMIN_EMAIL);
        edtAdminPhone.setText(adminPhone);
        edtAdminRole.setText(ADMIN_ROLE);
    }


    private void updateAdminProfile() {

        String name =
                edtAdminName.getText()
                        .toString()
                        .trim();

        String phone =
                edtAdminPhone.getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {

            edtAdminName.setError(
                    "Please enter admin name"
            );

            edtAdminName.requestFocus();

            return;
        }

        if (phone.isEmpty()) {

            edtAdminPhone.setError(
                    "Please enter phone number"
            );

            edtAdminPhone.requestFocus();

            return;
        }

        if (phone.length() != 10) {

            edtAdminPhone.setError(
                    "Phone number must contain 10 digits"
            );

            edtAdminPhone.requestFocus();

            return;
        }

        SharedPreferences.Editor editor =
                sharedPreferences.edit();

        editor.putString(
                "ADMIN_NAME",
                name
        );

        editor.putString(
                "ADMIN_PHONE",
                phone
        );

        editor.apply();

        Toast.makeText(
                AdminProfileActivity.this,
                "Admin Profile Updated Successfully",
                Toast.LENGTH_SHORT
        ).show();
    }


    private void showChangePasswordDialog() {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        int padding = 40;

        layout.setPadding(
                padding,
                padding,
                padding,
                padding
        );


        EditText edtCurrentPassword =
                new EditText(this);

        edtCurrentPassword.setHint(
                "Current Password"
        );

        edtCurrentPassword.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );


        EditText edtNewPassword =
                new EditText(this);

        edtNewPassword.setHint(
                "New Password"
        );

        edtNewPassword.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );


        EditText edtConfirmPassword =
                new EditText(this);

        edtConfirmPassword.setHint(
                "Confirm New Password"
        );

        edtConfirmPassword.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );


        layout.addView(
                edtCurrentPassword
        );

        layout.addView(
                edtNewPassword
        );

        layout.addView(
                edtConfirmPassword
        );


        AlertDialog dialog =
                new AlertDialog.Builder(this)

                        .setTitle(
                                "Change Password"
                        )

                        .setView(layout)

                        .setNegativeButton(
                                "Cancel",
                                null
                        )

                        .setPositiveButton(
                                "Change Password",
                                null
                        )

                        .create();


        dialog.setOnShowListener(dialogInterface -> {

            Button btnChange =
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    );


            btnChange.setOnClickListener(v -> {

                String currentPassword =
                        edtCurrentPassword
                                .getText()
                                .toString()
                                .trim();


                String newPassword =
                        edtNewPassword
                                .getText()
                                .toString()
                                .trim();


                String confirmPassword =
                        edtConfirmPassword
                                .getText()
                                .toString()
                                .trim();


                String savedPassword =
                        sharedPreferences.getString(
                                KEY_ADMIN_PASSWORD,
                                DEFAULT_ADMIN_PASSWORD
                        );


                if (currentPassword.isEmpty()) {

                    edtCurrentPassword.setError(
                            "Enter current password"
                    );

                    return;
                }


                if (!currentPassword.equals(
                        savedPassword
                )) {

                    edtCurrentPassword.setError(
                            "Current password is incorrect"
                    );

                    return;
                }


                if (newPassword.isEmpty()) {

                    edtNewPassword.setError(
                            "Enter new password"
                    );

                    return;
                }


                if (newPassword.length() < 6) {

                    edtNewPassword.setError(
                            "Password must contain at least 6 characters"
                    );

                    return;
                }


                if (!newPassword.equals(
                        confirmPassword
                )) {

                    edtConfirmPassword.setError(
                            "Passwords do not match"
                    );

                    return;
                }


                SharedPreferences.Editor editor =
                        sharedPreferences.edit();


                editor.putString(
                        KEY_ADMIN_PASSWORD,
                        newPassword
                );


                editor.apply();


                Toast.makeText(
                        AdminProfileActivity.this,
                        "Password Changed Successfully",
                        Toast.LENGTH_SHORT
                ).show();


                dialog.dismiss();
            });
        });


        dialog.show();
    }


    private void logoutAdmin() {

        Intent intent = new Intent(
                AdminProfileActivity.this,
                AdminLoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}