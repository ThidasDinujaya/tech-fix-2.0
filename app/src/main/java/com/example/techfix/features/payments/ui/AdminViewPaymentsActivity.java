package com.example.techfix.features.payments.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.payments.data.Payment;
import java.util.List;

public class AdminViewPaymentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_payments);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Payment> payments = dbHelper.getAllPayments();

        RecyclerView rv = findViewById(R.id.rvAdminPayments);
        rv.setLayoutManager(new LinearLayoutManager(this));
        
        AdminPaymentAdapter adapter = new AdminPaymentAdapter(payments, payment -> {
            Intent intent = new Intent(this, PaymentReceiptActivity.class);
            intent.putExtra("booking_id", payment.getBookingId());
            startActivity(intent);
        });
        
        rv.setAdapter(adapter);
    }
}
