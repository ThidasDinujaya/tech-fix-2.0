package com.example.techfix.features.booking.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingStatus;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookings;
    private final OnBookingClickListener listener;
    private final DatabaseHelper dbHelper;

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
        void onAddReviewClick(Booking booking);
        void onViewReviewClick(Booking booking);
        void onViewReceiptClick(Booking booking);
    }

    public BookingAdapter(List<Booking> bookings, OnBookingClickListener listener, DatabaseHelper dbHelper) {
        this.bookings = bookings;
        this.listener = listener;
        this.dbHelper = dbHelper;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        boolean isReviewed = dbHelper.hasReview(booking.getId());
        boolean hasPayment = dbHelper.hasPayment(booking.getId());
        holder.bind(booking, listener, isReviewed, hasPayment);
    }

    @Override
    public int getItemCount() {
        return bookings != null ? bookings.size() : 0;
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingId, tvStatus, tvServiceName, tvDeviceModel, tvDateTime, tvTech;
        Button btnReview, btnReceipt;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDeviceModel = itemView.findViewById(R.id.tvDeviceModel);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            btnReview = itemView.findViewById(R.id.btnReview);
            btnReceipt = itemView.findViewById(R.id.btnViewReceipt);
        }

        public void bind(final Booking booking, final OnBookingClickListener listener, boolean isReviewed, boolean hasPayment) {
            tvBookingId.setText("TF" + (1000 + booking.getId()));
            tvStatus.setText(booking.getStatus());
            tvServiceName.setText("Repair Request");
            tvDeviceModel.setText(booking.getBrand() + " " + booking.getModel());
            
            String dateTimeStr = booking.getAppointmentDate();
            if (booking.getAppointmentTime() != null && !booking.getAppointmentTime().isEmpty()) {
                dateTimeStr += " • " + booking.getAppointmentTime();
            }
            tvDateTime.setText(dateTimeStr);

            if (hasPayment) {
                btnReceipt.setVisibility(View.VISIBLE);
                btnReceipt.setOnClickListener(v -> listener.onViewReceiptClick(booking));
            } else {
                btnReceipt.setVisibility(View.GONE);
            }

            if (booking.getTechnicianName() != null && !booking.getTechnicianName().isEmpty()) {
                tvDeviceModel.setText(booking.getBrand() + " " + booking.getModel() + "\nAssigned: " + booking.getTechnicianName());
            }

            itemView.setOnClickListener(v -> listener.onBookingClick(booking));
            
            // Set status color based on current status
            String status = booking.getStatus();
            if (status.equals(BookingStatus.PENDING)) {
                tvStatus.setBackgroundColor(Color.parseColor("#FFE082"));
                tvStatus.setTextColor(Color.parseColor("#FF8F00"));
                btnReview.setVisibility(View.GONE);
            } else if (status.equals(BookingStatus.COMPLETED)) {
                tvStatus.setBackgroundColor(Color.parseColor("#C8E6C9"));
                tvStatus.setTextColor(Color.parseColor("#2E7D32"));
                btnReview.setVisibility(View.VISIBLE);
                
                if (isReviewed) {
                    btnReview.setText("View Review");
                    btnReview.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.success_green)));
                    btnReview.setTextColor(Color.WHITE);
                    btnReview.setOnClickListener(v -> listener.onViewReviewClick(booking));
                } else {
                    btnReview.setText("Add Review");
                    btnReview.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.yellow_primary)));
                    btnReview.setTextColor(Color.BLACK);
                    btnReview.setOnClickListener(v -> listener.onAddReviewClick(booking));
                }
            } else {
                tvStatus.setBackgroundColor(Color.parseColor("#BBDEFB"));
                tvStatus.setTextColor(Color.parseColor("#1565C0"));
                btnReview.setVisibility(View.GONE);
            }
        }
    }
}
