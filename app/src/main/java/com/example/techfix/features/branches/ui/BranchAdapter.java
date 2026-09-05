package com.example.techfix.features.branches.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix.R;
import com.example.techfix.features.branches.data.Branch;

import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {

    private List<Branch> branchList;
    private OnBranchActionListener listener;

    public interface OnBranchActionListener {
        void onEdit(Branch branch);
        void onDelete(Branch branch);
    }

    public BranchAdapter(List<Branch> branchList, OnBranchActionListener listener) {
        this.branchList = branchList;
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
        holder.tvPhone.setText(branch.getPhone());
        holder.tvHours.setText(branch.getHours());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(branch));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(branch));
    }

    @Override
    public int getItemCount() {
        return branchList.size();
    }

    static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvPhone, tvHours;
        ImageView btnEdit, btnDelete;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBranchName);
            tvAddress = itemView.findViewById(R.id.tvBranchAddress);
            tvPhone = itemView.findViewById(R.id.tvBranchPhone);
            tvHours = itemView.findViewById(R.id.tvBranchHours);
            btnEdit = itemView.findViewById(R.id.btnEditBranch);
            btnDelete = itemView.findViewById(R.id.btnDeleteBranch);
        }
    }
}
