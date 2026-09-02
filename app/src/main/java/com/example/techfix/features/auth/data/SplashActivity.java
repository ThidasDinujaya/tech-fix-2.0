package com.example.techfix.features.auth.data;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            Intent intent =
                    new Intent(SplashActivity.this, LoginActivity.class);

            startActivity(intent);

            finish();

        }, SPLASH_TIME);
    }
}