package com.example.techfix.features.booking.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.features.booking.data.Booking;
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
        TextView tvBookingId, tvStatus, tvServiceName, tvDeviceModel, tvDateTime;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDeviceModel = itemView.findViewById(R.id.tvDeviceModel);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
        }

        public void bind(final Booking booking, final OnBookingClickListener listener) {
            tvBookingId.setText("TF" + String.format("%04d", booking.getId()));
            tvStatus.setText(booking.getStatus());
            tvServiceName.setText("Repair Service"); // In a real app, we'd fetch the service name by ID
            tvDeviceModel.setText(booking.getBrand() + " " + booking.getModel());
            tvDateTime.setText(booking.getAppointmentDate());

            itemView.setOnClickListener(v -> listener.onBookingClick(booking));
            
            // Set status color (simplified for now)
            if ("COMPLETED".equals(booking.getStatus())) {
                tvStatus.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_light));
            } else if ("ASSIGNED".equals(booking.getStatus())) {
                tvStatus.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_blue_light));
            } else {
                tvStatus.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_light));
            }
        }
    }
}
