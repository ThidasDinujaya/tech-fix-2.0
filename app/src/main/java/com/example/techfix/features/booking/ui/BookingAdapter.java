package com.example.techfix.features.booking.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingStatus;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookings;
    private final OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }

    public BookingAdapter(List<Booking> bookings, OnBookingClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
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
        holder.bind(booking, listener);
    }

    @Override
    public int getItemCount() {
        return bookings != null ? bookings.size() : 0;
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingId, tvStatus, tvServiceName, tvDeviceModel, tvDateTime, tvTech;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDeviceModel = itemView.findViewById(R.id.tvDeviceModel);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            // We'll reuse/add a tech field if needed, or put it in status
        }

        public void bind(final Booking booking, final OnBookingClickListener listener) {
            tvBookingId.setText("TF" + (1000 + booking.getId()));
            tvStatus.setText(booking.getStatus());
            tvServiceName.setText("Repair Request");
            tvDeviceModel.setText(booking.getBrand() + " " + booking.getModel());
            tvDateTime.setText(booking.getAppointmentDate());

            if (booking.getTechnicianName() != null && !booking.getTechnicianName().isEmpty()) {
                tvDeviceModel.setText(booking.getBrand() + " " + booking.getModel() + "\nAssigned: " + booking.getTechnicianName());
            }

            itemView.setOnClickListener(v -> listener.onBookingClick(booking));
            
            // Set status color based on current status
            String status = booking.getStatus();
            if (status.equals(BookingStatus.PENDING)) {
                tvStatus.setBackgroundColor(Color.parseColor("#FFE082"));
                tvStatus.setTextColor(Color.parseColor("#FF8F00"));
            } else if (status.equals(BookingStatus.COMPLETED)) {
                tvStatus.setBackgroundColor(Color.parseColor("#C8E6C9"));
                tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            } else {
                tvStatus.setBackgroundColor(Color.parseColor("#BBDEFB"));
                tvStatus.setTextColor(Color.parseColor("#1565C0"));
            }
        }
    }
}
