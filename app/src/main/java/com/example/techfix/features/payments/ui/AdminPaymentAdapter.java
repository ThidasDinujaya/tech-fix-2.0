package com.example.techfix.features.payments.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.features.payments.data.Payment;
import java.util.List;
import java.util.Locale;

public class AdminPaymentAdapter extends RecyclerView.Adapter<AdminPaymentAdapter.PaymentViewHolder> {

    private final List<Payment> payments;
    private final OnPaymentClickListener listener;

    public interface OnPaymentClickListener {
        void onPaymentClick(Payment payment);
    }

    public AdminPaymentAdapter(List<Payment> payments, OnPaymentClickListener listener) {
        this.payments = payments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        holder.bind(payments.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvDate, tvAmount;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvPaymentId);
            tvDate = itemView.findViewById(R.id.tvPaymentDate);
            tvAmount = itemView.findViewById(R.id.tvPaymentAmount);
        }

        public void bind(Payment payment, OnPaymentClickListener listener) {
            tvId.setText("TF" + (1000 + payment.getBookingId()));
            tvDate.setText(payment.getPaymentDate());
            tvAmount.setText(String.format(Locale.US, "LKR %.2f", payment.getAmount()));
            itemView.setOnClickListener(v -> listener.onPaymentClick(payment));
        }
    }
}
