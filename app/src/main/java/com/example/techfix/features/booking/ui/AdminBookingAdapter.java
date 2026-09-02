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

// Adapter for the Admin dashboard to display a list of all repair bookings
public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.BookingViewHolder> {

    private final List<Booking> bookings;

    public AdminBookingAdapter(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_admin, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(bookings.get(position));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvId, tvStatus, tvService, tvDevice, tvDate;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvBookingId);
            tvStatus = itemView.findViewById(R.id.tvAdminStatus);
            tvService = itemView.findViewById(R.id.tvAdminServiceName);
            tvDevice = itemView.findViewById(R.id.tvAdminDeviceModel);
            tvDate = itemView.findViewById(R.id.tvAdminDate);
        }

        public void bind(Booking booking) {
            tvId.setText("TF" + (1000 + booking.getId()));
            tvStatus.setText(booking.getStatus());
            tvService.setText("Repair Request"); // This can be linked to service name later
            tvDevice.setText(booking.getBrand() + " " + booking.getModel());
            tvDate.setText(booking.getAppointmentDate());
        }
    }
}
