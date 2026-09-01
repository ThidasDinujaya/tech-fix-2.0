package com.example.techfix.features.auth.data;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName;
    private EditText edtEmail;
    private EditText edtPhone;
    private EditText edtPassword;
    private EditText edtConfirmPassword;

    private Button btnRegister;
    private TextView txtLogin;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);


        // Register Button
        btnRegister.setOnClickListener(v -> registerUser());


        // Go to Login
        txtLogin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);

            finish();
        });
    }


    private void registerUser() {

        String fullName =
                edtFullName.getText().toString().trim();

        String email =
                edtEmail.getText().toString().trim();

        String phone =
                edtPhone.getText().toString().trim();

        String password =
                edtPassword.getText().toString().trim();

        String confirmPassword =
                edtConfirmPassword.getText().toString().trim();


        // Name
        if (fullName.isEmpty()) {

            edtFullName.setError("Please enter your full name");
            edtFullName.requestFocus();

            return;
        }


        // Email
        if (email.isEmpty()) {

            edtEmail.setError("Please enter your email");
            edtEmail.requestFocus();

            return;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            edtEmail.setError("Please enter a valid email");
            edtEmail.requestFocus();

            return;
        }


        // Phone
        if (phone.isEmpty()) {

            edtPhone.setError("Please enter your phone number");
            edtPhone.requestFocus();

            return;
        }


        if (phone.length() != 10) {

            edtPhone.setError(
                    "Phone number must contain 10 digits"
            );

            edtPhone.requestFocus();

            return;
        }


        // Password
        if (password.isEmpty()) {

            edtPassword.setError("Please enter a password");
            edtPassword.requestFocus();

            return;
        }


        if (password.length() < 6) {

            edtPassword.setError(
                    "Password must contain at least 6 characters"
            );

            edtPassword.requestFocus();

            return;
        }


        // Confirm Password
        if (confirmPassword.isEmpty()) {

            edtConfirmPassword.setError(
                    "Please confirm your password"
            );

            edtConfirmPassword.requestFocus();

            return;
        }


        if (!password.equals(confirmPassword)) {

            edtConfirmPassword.setError(
                    "Passwords do not match"
            );

            edtConfirmPassword.requestFocus();

            return;
        }


        // Duplicate email
        if (databaseHelper.checkEmail(email)) {

            edtEmail.setError(
                    "This email is already registered"
            );

            edtEmail.requestFocus();

            return;
        }


        // Save user
        boolean inserted =
                databaseHelper.insertUser(
                        fullName,
                        email,
                        phone,
                        password
                );


        if (inserted) {

            Toast.makeText(
                    RegisterActivity.this,
                    "Registration Successful!",
                    Toast.LENGTH_SHORT
            ).show();


            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);

            finish();

        } else {

            Toast.makeText(
                    RegisterActivity.this,
                    "Registration Failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}