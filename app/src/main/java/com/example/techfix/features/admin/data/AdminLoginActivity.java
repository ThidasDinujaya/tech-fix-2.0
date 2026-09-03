package com.example.techfix.features.admin.data;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix.R;
import com.example.techfix.common.util.SessionManager;
import com.example.techfix.features.auth.data.LoginActivity;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText edtAdminEmail;
    private EditText edtAdminPassword;

    private Button btnAdminLogin;
    private TextView txtBackCustomerLogin;

    private static final String ADMIN_EMAIL = "admin@techfix.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private static final String PREF_NAME = "TechFixAdminProfile";
    private static final String KEY_ADMIN_PASSWORD = "ADMIN_PASSWORD";

    private SharedPreferences sharedPreferences;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_login);

        sharedPreferences = getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        );
        sessionManager = new SessionManager(this);

        edtAdminEmail = findViewById(R.id.edtAdminEmail);
        edtAdminPassword = findViewById(R.id.edtAdminPassword);

        btnAdminLogin = findViewById(R.id.btnAdminLogin);
        txtBackCustomerLogin =
                findViewById(R.id.txtBackCustomerLogin);

        btnAdminLogin.setOnClickListener(v ->
                loginAdmin()
        );

        txtBackCustomerLogin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminLoginActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);

            finish();
        });
    }

    private void loginAdmin() {

        String email =
                edtAdminEmail.getText()
                        .toString()
                        .trim();

        String password =
                edtAdminPassword.getText()
                        .toString()
                        .trim();

        if (email.isEmpty()) {

            edtAdminEmail.setError(
                    "Please enter admin email"
            );

            edtAdminEmail.requestFocus();

            return;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            edtAdminEmail.setError(
                    "Please enter a valid email"
            );

            edtAdminEmail.requestFocus();

            return;
        }

        if (password.isEmpty()) {

            edtAdminPassword.setError(
                    "Please enter password"
            );

            edtAdminPassword.requestFocus();

            return;
        }

        String savedPassword =
                sharedPreferences.getString(
                        KEY_ADMIN_PASSWORD,
                        DEFAULT_ADMIN_PASSWORD
                );

        if (email.equals(ADMIN_EMAIL)
                && password.equals(savedPassword)) {

            // Save admin session
            sessionManager.createSession(email, SessionManager.ROLE_ADMIN);

            Toast.makeText(
                    AdminLoginActivity.this,
                    "Admin Login Successful!",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    AdminLoginActivity.this,
                    AdminDashboardActivity.class
            );

            startActivity(intent);

            finish();

        } else {

            Toast.makeText(
                    AdminLoginActivity.this,
                    "Incorrect admin email or password",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}