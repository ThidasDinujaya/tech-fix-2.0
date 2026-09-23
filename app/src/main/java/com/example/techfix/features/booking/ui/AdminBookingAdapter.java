package com.example.techfix.features.booking.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.booking.data.Booking;
import com.example.techfix.features.booking.data.BookingStatus;
import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.BookingViewHolder> {

    private final List<Booking> bookings;
    private final OnBookingActionListener listener;
    private final DatabaseHelper dbHelper;

    public interface OnBookingActionListener {
        void onUpdateStatus(Booking booking);
        void onDelete(Booking booking);
        void onViewReview(Booking booking);
        void onViewReceipt(Booking booking);
    }

    public AdminBookingAdapter(List<Booking> bookings, OnBookingActionListener listener, DatabaseHelper dbHelper) {
        this.bookings = bookings;
        this.listener = listener;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_admin, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        boolean hasReview = dbHelper.hasReview(booking.getId());
        boolean hasPayment = dbHelper.hasPayment(booking.getId());
        holder.bind(booking, listener, hasReview, hasPayment);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvId, tvStatus, tvService, tvDevice, tvDate, tvBranch, tvTech;
        private final View btnUpdate, btnDelete, btnViewReview, btnViewReceipt;

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
            btnViewReview = itemView.findViewById(R.id.btnViewReview);
            btnViewReceipt = itemView.findViewById(R.id.btnAdminViewReceipt);
        }

        public void bind(Booking booking, OnBookingActionListener listener, boolean hasReview, boolean hasPayment) {
            tvId.setText("TF" + (1000 + booking.getId()));
            tvStatus.setText(booking.getStatus());
            tvService.setText("Repair Request");
            tvDevice.setText(booking.getBrand() + " " + booking.getModel());
            
            String dateTimeStr = booking.getAppointmentDate();
            if (booking.getAppointmentTime() != null && !booking.getAppointmentTime().isEmpty()) {
                dateTimeStr += " • " + booking.getAppointmentTime();
            }
            tvDate.setText(dateTimeStr);
            tvBranch.setText("Branch: " + booking.getBranchName());
            
            String tech = booking.getTechnicianName();
            tvTech.setText("Tech: " + (tech != null && !tech.isEmpty() ? tech : "Not Assigned"));

            btnUpdate.setOnClickListener(v -> listener.onUpdateStatus(booking));
            btnDelete.setOnClickListener(v -> listener.onDelete(booking));
            
            if (hasPayment) {
                btnViewReceipt.setVisibility(View.VISIBLE);
                btnViewReceipt.setOnClickListener(v -> listener.onViewReceipt(booking));
            } else {
                btnViewReceipt.setVisibility(View.GONE);
            }

            if (BookingStatus.COMPLETED.equals(booking.getStatus()) && hasReview) {
                btnViewReview.setVisibility(View.VISIBLE);
                btnViewReview.setOnClickListener(v -> listener.onViewReview(booking));
            } else {
                btnViewReview.setVisibility(View.GONE);
            }
        }
    }
}
