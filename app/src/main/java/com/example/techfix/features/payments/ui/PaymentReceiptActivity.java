package com.example.techfix.features.payments.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.techfix.R;
import com.example.techfix.features.auth.data.CustomerHomeActivity;
import com.example.techfix.features.payments.data.Payment;
import com.example.techfix.features.payments.data.PaymentRepository;
import com.example.techfix.common.util.SessionManager;
import java.util.Locale;
import android.view.View;

public class PaymentReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_receipt);

        int bookingId = getIntent().getIntExtra("booking_id", -1);
        String serviceName = getIntent().getStringExtra("service_name");

        PaymentRepository repository = PaymentRepository.getInstance(this);
        Payment payment = repository.getPaymentByBookingId(bookingId);

        if (payment != null) {
            TextView tvId = findViewById(R.id.tvReceiptBookingId);
            TextView tvService = findViewById(R.id.tvReceiptService);
            TextView tvAmount = findViewById(R.id.tvReceiptAmount);
            TextView tvMethod = findViewById(R.id.tvReceiptMethod);
            TextView tvDate = findViewById(R.id.tvReceiptDate);

            tvId.setText("Booking ID: TF" + (1000 + bookingId));
            tvService.setText("Service: " + (serviceName != null ? serviceName : "Repair Service"));
            tvAmount.setText(String.format(Locale.US, "Amount: LKR %.2f", payment.getAmount()));
            tvMethod.setText("Method: " + payment.getMethod());
            tvDate.setText("Date: " + payment.getPaymentDate());
        }

        SessionManager sessionManager = new SessionManager(this);
        if (SessionManager.ROLE_ADMIN.equals(sessionManager.getRole())) {
            findViewById(R.id.btnBackToHome).setVisibility(View.GONE);
        }

        findViewById(R.id.btnBackToHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
