package com.example.techfix.features.admin.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix.R;
import com.example.techfix.common.data.DatabaseHelper;
import com.example.techfix.features.admin.data.TimeSlot;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<TimeSlot> list;
    private OnTimeSlotActionListener listener;
    private DatabaseHelper dbHelper;
    private String selectedDate;
    private int maxCapacity;

    public interface OnTimeSlotActionListener {
        void onEdit(TimeSlot slot);
        void onDelete(TimeSlot slot);
    }

    public TimeSlotAdapter(List<TimeSlot> list, OnTimeSlotActionListener listener, DatabaseHelper dbHelper, String selectedDate, int maxCapacity) {
        this.list = list;
        this.listener = listener;
        this.dbHelper = dbHelper;
        this.selectedDate = selectedDate;
        this.maxCapacity = maxCapacity > 0 ? maxCapacity : 1;
    }

    public void updateData(List<TimeSlot> list, String selectedDate, int maxCapacity) {
        this.list = list;
        this.selectedDate = selectedDate;
        this.maxCapacity = maxCapacity > 0 ? maxCapacity : 1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot ts = list.get(position);
        holder.tvSlotName.setText(ts.getSlotName());
        String branch = ts.getBranchName() != null ? ts.getBranchName() : "Colombo Main";
        holder.tvSlotBranch.setText("Branch: " + branch);
        holder.tvSlotStatus.setText(ts.getStatus());

        if ("Available".equalsIgnoreCase(ts.getStatus())) {
            holder.tvSlotStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvSlotStatus.setTextColor(Color.parseColor("#D32F2F"));
        }

        int bookedCount = 0;
        if (dbHelper != null && selectedDate != null && !selectedDate.isEmpty()) {
            bookedCount = dbHelper.getBookingCountForSlot(branch, selectedDate, ts.getSlotName());
        }

        if (bookedCount >= maxCapacity) {
            holder.tvSlotCapacity.setText("Bookings: " + bookedCount + " / " + maxCapacity + " (Full)");
            holder.tvSlotCapacity.setTextColor(Color.parseColor("#C62828"));
        } else {
            holder.tvSlotCapacity.setText("Bookings: " + bookedCount + " / " + maxCapacity);
            holder.tvSlotCapacity.setTextColor(Color.parseColor("#1565C0"));
        }

        holder.btnEditSlot.setOnClickListener(v -> listener.onEdit(ts));
        holder.btnDeleteSlot.setOnClickListener(v -> listener.onDelete(ts));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvSlotName, tvSlotBranch, tvSlotStatus, tvSlotCapacity;
        View btnEditSlot, btnDeleteSlot;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSlotName = itemView.findViewById(R.id.tvSlotName);
            tvSlotBranch = itemView.findViewById(R.id.tvSlotBranch);
            tvSlotStatus = itemView.findViewById(R.id.tvSlotStatus);
            tvSlotCapacity = itemView.findViewById(R.id.tvSlotCapacity);
            btnEditSlot = itemView.findViewById(R.id.btnEditSlot);
            btnDeleteSlot = itemView.findViewById(R.id.btnDeleteSlot);
        }
    }
}
