package com.example.techfix.features.branches.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.techfix.R;
import com.example.techfix.features.branches.data.Technician;

import java.util.List;

public class TechnicianAdapter extends RecyclerView.Adapter<TechnicianAdapter.TechViewHolder> {

    private List<Technician> list;

    public TechnicianAdapter(List<Technician> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public TechViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_technician, parent, false);
        return new TechViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TechViewHolder holder, int position) {
        Technician t = list.get(position);
        holder.tvName.setText(t.getName());
        holder.tvRole.setText(t.getRole());
        holder.tvBranch.setText(t.getBranchName());
        holder.tvStatus.setText(t.getStatus());

        if ("Available".equalsIgnoreCase(t.getStatus())) {
            holder.tvStatus.setTextColor(Color.parseColor("#28A745"));
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#DC3545"));
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class TechViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole, tvBranch, tvStatus;

        public TechViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTechName);
            tvRole = itemView.findViewById(R.id.tvTechRole);
            tvBranch = itemView.findViewById(R.id.tvTechBranch);
            tvStatus = itemView.findViewById(R.id.tvTechStatus);
        }
    }
}
