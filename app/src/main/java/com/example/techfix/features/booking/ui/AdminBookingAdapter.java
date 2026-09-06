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

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.BookingViewHolder> {

    private final List<Booking> bookings;
    private final OnBookingActionListener listener;

    public interface OnBookingActionListener {
        void onUpdateStatus(Booking booking);
        void onDelete(Booking booking);
    }

    public AdminBookingAdapter(List<Booking> bookings, OnBookingActionListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_admin, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(bookings.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvId, tvStatus, tvService, tvDevice, tvDate, tvBranch, tvTech;
        private final View btnUpdate, btnDelete;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvBookingId);
            tvStatus = itemView.findViewById(R.id.tvAdminStatus);
            tvService = itemView.findViewById(R.id.tvAdminServiceName);
            tvDevice = itemView.findViewById(R.id.tvAdminDeviceModel);
            tvDate = itemView.findViewById(R.id.tvAdminDate);
            tvBranch = itemView.findViewById(R.id.tvAdminBranch);
            tvTech = itemView.findViewById(R.id.tvAdminTech);
            btnUpdate = itemView.findViewById(R.id.btnUpdateStatus);
            btnDelete = itemView.findViewById(R.id.btnDeleteBooking);
        }

        public void bind(Booking booking, OnBookingActionListener listener) {
            tvId.setText("TF" + (1000 + booking.getId()));
            tvStatus.setText(booking.getStatus());
            tvService.setText("Repair Request");
            tvDevice.setText(booking.getBrand() + " " + booking.getModel());
            tvDate.setText(booking.getAppointmentDate());
            tvBranch.setText("Branch: " + booking.getBranchName());
            
            String tech = booking.getTechnicianName();
            tvTech.setText("Tech: " + (tech != null && !tech.isEmpty() ? tech : "Not Assigned"));

            btnUpdate.setOnClickListener(v -> listener.onUpdateStatus(booking));
            btnDelete.setOnClickListener(v -> listener.onDelete(booking));
        }
    }
}
