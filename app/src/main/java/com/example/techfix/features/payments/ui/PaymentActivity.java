package com.example.techfix.features.payments.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.techfix.R;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.payments.viewmodel.PaymentViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvBookingId, tvServiceName, tvAmount;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCard;
    private LinearLayout layoutCardDetails;
    private TextInputEditText etCardNumber, etExpiryDate, etCvv;
    private Button btnPayNow;

    private PaymentViewModel viewModel;
    private Booking booking;
    private int bookingId;
    private double currentAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        booking = (Booking) getIntent().getSerializableExtra("booking_data");
        if (booking != null) {
            bookingId = booking.getId();
        } else {
            bookingId = getIntent().getIntExtra("booking_id", -1);
        }

        currentAmount = getIntent().getDoubleExtra("service_price", 0.0);
        String serviceName = getIntent().getStringExtra("service_name");

        initViews();
        setupViewModel();
        setupListeners();
        
        if (booking != null) {
            tvBookingId.setText("Booking ID: New Booking Request");
            if (serviceName != null) {
                tvServiceName.setText("Service: " + serviceName);
            }
            if (currentAmount > 0) {
                tvAmount.setText(String.format(Locale.US, "LKR %.2f", currentAmount));
            }
        } else if (bookingId != -1) {
            tvBookingId.setText("Booking ID: TF" + String.format(Locale.US, "%04d", bookingId));
            if (serviceName != null) {
                tvServiceName.setText("Service: " + serviceName);
            }
            if (currentAmount > 0) {
                tvAmount.setText(String.format(Locale.US, "LKR %.2f", currentAmount));
            }
            viewModel.loadBookingDetails(bookingId);
        }
    }

    private void initViews() {
        tvBookingId = findViewById(R.id.tvBookingId);
        tvServiceName = findViewById(R.id.tvServiceName);
        tvAmount = findViewById(R.id.tvAmount);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbCard = findViewById(R.id.rbCard);
        layoutCardDetails = findViewById(R.id.layoutCardDetails);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        etCvv = findViewById(R.id.etCvv);
        btnPayNow = findViewById(R.id.btnPayNow);
    }

    private void setupListeners() {
        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCard) {
                layoutCardDetails.setVisibility(View.VISIBLE);
            } else {
                layoutCardDetails.setVisibility(View.GONE);
            }
        });

        btnPayNow.setOnClickListener(v -> processPayment());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        viewModel.getAmount().observe(this, amount -> {
            this.currentAmount = amount;
            tvAmount.setText(String.format("LKR %.2f", amount));
        });

        viewModel.getServiceName().observe(this, name -> {
            tvServiceName.setText(name);
        });

        viewModel.getPaymentSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                showSuccessDialog();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processPayment() {
        String method = "Cash";
        int checkedId = rgPaymentMethod.getCheckedRadioButtonId();
        if (checkedId == R.id.rbCard) {
            method = "Card";
        } else if (checkedId == R.id.rbTransfer) {
            method = "Online Transfer";
        }

        String cardNumber = etCardNumber.getText().toString().trim();
        String expiry = etExpiryDate.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        if (booking != null) {
            viewModel.processBookingPayment(booking, currentAmount, method, cardNumber, expiry, cvv);
        } else {
            viewModel.processPayment(bookingId, currentAmount, method, cardNumber, expiry, cvv);
        }
    }

    private void showSuccessDialog() {
        int finalBookingId = (booking != null && booking.getId() > 0) ? booking.getId() : bookingId;
        new AlertDialog.Builder(this)
                .setTitle("Payment Successful!")
                .setMessage("Thank you for your payment. Your receipt has been generated.")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(this, PaymentReceiptActivity.class);
                    intent.putExtra("booking_id", finalBookingId);
                    intent.putExtra("service_name", tvServiceName.getText().toString().replace("Service: ", ""));
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
