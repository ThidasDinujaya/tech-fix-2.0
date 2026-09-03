package com.example.techfix.features.auth.data;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix.R;

import com.example.techfix.common.util.SessionManager;
import com.example.techfix.features.admin.data.AdminDashboardActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2000; // Reduced to 2s for better UX

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        SessionManager sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                // Auto-redirect based on role if session exists
                if (SessionManager.ROLE_ADMIN.equals(sessionManager.getRole())) {
                    intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, CustomerHomeActivity.class);
                    intent.putExtra("USER_EMAIL", sessionManager.getEmail());
                }
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_TIME);
    }
}
