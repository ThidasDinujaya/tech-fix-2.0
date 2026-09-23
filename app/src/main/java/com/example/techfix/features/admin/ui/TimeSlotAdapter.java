package com.example.techfix.features.admin.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix.R;
import com.example.techfix.features.admin.data.TimeSlot;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<TimeSlot> list;
    private OnTimeSlotActionListener listener;

    public interface OnTimeSlotActionListener {
        void onEdit(TimeSlot slot);
        void onDelete(TimeSlot slot);
    }

    public TimeSlotAdapter(List<TimeSlot> list, OnTimeSlotActionListener listener) {
        this.list = list;
        this.listener = listener;
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
        holder.tvSlotBranch.setText("Branch: " + (ts.getBranchName() != null ? ts.getBranchName() : "Colombo Main"));
        holder.tvSlotStatus.setText(ts.getStatus());

        if ("Available".equalsIgnoreCase(ts.getStatus())) {
            holder.tvSlotStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvSlotStatus.setTextColor(Color.parseColor("#D32F2F"));
        }

        holder.btnEditSlot.setOnClickListener(v -> listener.onEdit(ts));
        holder.btnDeleteSlot.setOnClickListener(v -> listener.onDelete(ts));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvSlotName, tvSlotBranch, tvSlotStatus;
        View btnEditSlot, btnDeleteSlot;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSlotName = itemView.findViewById(R.id.tvSlotName);
            tvSlotBranch = itemView.findViewById(R.id.tvSlotBranch);
            tvSlotStatus = itemView.findViewById(R.id.tvSlotStatus);
            btnEditSlot = itemView.findViewById(R.id.btnEditSlot);
            btnDeleteSlot = itemView.findViewById(R.id.btnDeleteSlot);
        }
    }
}
