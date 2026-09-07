package com.example.techfix.features.auth.data;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.common.util.SessionManager;
import com.example.techfix.features.admin.data.AdminLoginActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;

    private Button btnLogin;

    private TextView txtRegister;
    private TextView txtAdminLogin;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogin = findViewById(R.id.btnLogin);

        txtRegister = findViewById(R.id.txtRegister);
        txtAdminLogin = findViewById(R.id.txtAdminLogin);

        // Customer login
        btnLogin.setOnClickListener(v -> loginUser());

        // Register
        txtRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });

        // Admin Login
        txtAdminLogin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    AdminLoginActivity.class
            );

            startActivity(intent);
        });
    }

    private void loginUser() {

        String email =
                edtEmail.getText().toString().trim();

        String password =
                edtPassword.getText().toString().trim();

        if (email.isEmpty()) {

            edtEmail.setError("Please enter your email");
            edtEmail.requestFocus();

            return;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            edtEmail.setError("Please enter a valid email");
            edtEmail.requestFocus();

            return;
        }

        if (password.isEmpty()) {

            edtPassword.setError("Please enter your password");
            edtPassword.requestFocus();

            return;
        }

        boolean validUser =
                databaseHelper.checkUser(
                        email,
                        password
                );

        if (validUser) {

            // Save customer session
            sessionManager.createSession(email, SessionManager.ROLE_CUSTOMER);

            Toast.makeText(
                    LoginActivity.this,
                    "Login Successful!",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    LoginActivity.this,
                    CustomerHomeActivity.class
            );

            intent.putExtra(
                    "USER_EMAIL",
                    email
            );

            startActivity(intent);

            finish();

        } else {

            Toast.makeText(
                    LoginActivity.this,
                    "Incorrect email or password",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}