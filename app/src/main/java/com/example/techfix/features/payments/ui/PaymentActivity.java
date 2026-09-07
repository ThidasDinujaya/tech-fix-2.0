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
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        booking = (Booking) getIntent().getSerializableExtra("booking_data");
        amount = getIntent().getDoubleExtra("service_price", 0.0);
        String serviceName = getIntent().getStringExtra("service_name");

        initViews();
        setupListeners();
        setupViewModel();
        
        if (booking != null) {
            tvBookingId.setText("New Repair Booking");
            tvServiceName.setText(serviceName != null ? serviceName : "Repair Service");
            tvAmount.setText(String.format(Locale.US, "LKR %.2f", amount));
        } else {
            // Handle regular payment (if any)
            int bookingId = getIntent().getIntExtra("booking_id", -1);
            tvBookingId.setText("Booking ID: TF" + String.format(Locale.US, "%04d", bookingId));
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
        if (rbCard.isChecked()) {
            method = "Card";
        } else if (findViewById(R.id.rbTransfer).getId() == rgPaymentMethod.getCheckedRadioButtonId()) {
            method = "Online Transfer";
        }

        String cardNumber = etCardNumber.getText().toString().trim();
        String expiry = etExpiryDate.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        if (booking != null) {
            viewModel.processBookingPayment(booking, amount, method, cardNumber, expiry, cvv);
        } else {
            int bookingId = getIntent().getIntExtra("booking_id", -1);
            viewModel.processPayment(bookingId, amount, method, cardNumber, expiry, cvv);
        }
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Payment Successful!")
                .setMessage("Thank you for your payment. Your receipt has been generated.")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(this, PaymentReceiptActivity.class);
                    intent.putExtra("booking_id", booking != null ? booking.getId() : getIntent().getIntExtra("booking_id", -1));
                    intent.putExtra("service_name", tvServiceName.getText().toString().replace("Service: ", ""));
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
