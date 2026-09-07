package com.example.techfix.features.branches.ui;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix.R;
import com.example.techfix.features.branches.data.Branch;

import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {

    private List<Branch> branchList;
    private boolean isAdmin;
    private OnBranchActionListener listener;

    public interface OnBranchActionListener {
        void onEdit(Branch branch);
        void onDelete(Branch branch);
    }

    public BranchAdapter(List<Branch> branchList, boolean isAdmin, OnBranchActionListener listener) {
        this.branchList = branchList;
        this.isAdmin = isAdmin;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_branch, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        Branch branch = branchList.get(position);
        holder.tvName.setText(branch.getName());
        holder.tvAddress.setText(branch.getAddress());
        
        String phoneDisplay = branch.getPhone();
        if (branch.getPhone2() != null && !branch.getPhone2().isEmpty()) {
            phoneDisplay += " / " + branch.getPhone2();
        }
        holder.tvPhone.setText(phoneDisplay);
        
        holder.tvHoursMonFri.setText("Mon-Fri: " + branch.getHoursMonFri());
        holder.tvHoursSat.setText("Sat: " + branch.getHoursSat());
        holder.tvHoursSun.setText("Sun: " + branch.getHoursSun());

        if (branch.getHoursSun() != null && "Closed".equalsIgnoreCase(branch.getHoursSun().trim())) {
            holder.tvHoursSun.setTextColor(Color.parseColor("#C62828"));
        } else {
            holder.tvHoursSun.setTextColor(Color.parseColor("#2E7D32"));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BranchDetailsActivity.class);
            intent.putExtra("branch_data", branch);
            v.getContext().startActivity(intent);
        });

        if (isAdmin) {
            holder.adminActions.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> listener.onEdit(branch));
            holder.btnDelete.setOnClickListener(v -> listener.onDelete(branch));
        } else {
            holder.adminActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return branchList.size();
    }

    static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvPhone, tvHoursMonFri, tvHoursSat, tvHoursSun;
        View btnEdit, btnDelete;
        View adminActions;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBranchName);
            tvAddress = itemView.findViewById(R.id.tvBranchAddress);
            tvPhone = itemView.findViewById(R.id.tvBranchPhone);
            tvHoursMonFri = itemView.findViewById(R.id.tvBranchHoursMonFri);
            tvHoursSat = itemView.findViewById(R.id.tvBranchHoursSat);
            tvHoursSun = itemView.findViewById(R.id.tvBranchHoursSun);
            btnEdit = itemView.findViewById(R.id.btnEditBranch);
            btnDelete = itemView.findViewById(R.id.btnDeleteBranch);
            adminActions = itemView.findViewById(R.id.layoutAdminActions);
        }
    }
}
