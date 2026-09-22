package com.example.techfix.features.admin.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.features.branches.data.SparePart;
import java.util.List;
import java.util.Locale;

public class UnifiedInventoryAdapter extends RecyclerView.Adapter<UnifiedInventoryAdapter.UnifiedViewHolder> {

    private List<SparePart> parts;

    public UnifiedInventoryAdapter(List<SparePart> parts) {
        this.parts = parts;
    }

    @NonNull
    @Override
    public UnifiedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unified_inventory, parent, false);
        return new UnifiedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UnifiedViewHolder holder, int position) {
        SparePart p = parts.get(position);
        holder.tvPartName.setText(p.getName());
        holder.tvBrand.setText(p.getBrand());
        holder.tvModel.setText(p.getModel());
        holder.tvQuality.setText(p.getQuality());
        holder.tvBranch.setText((p.getBranchName() != null && !p.getBranchName().isEmpty()) ? p.getBranchName() : "Main Branch");
        holder.tvStock.setText(String.valueOf(p.getStock()));
        holder.tvPrice.setText(String.format(Locale.US, "%,.2f", p.getPrice()));
        holder.tvCategory.setText(p.getCategory() != null ? p.getCategory() : "-"); 
    }

    @Override
    public int getItemCount() { return parts.size(); }

    static class UnifiedViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartName, tvCategory, tvBrand, tvModel, tvQuality, tvBranch, tvStock, tvPrice;

        public UnifiedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartName = itemView.findViewById(R.id.tvUnifiedPartName);
            tvCategory = itemView.findViewById(R.id.tvUnifiedCategory);
            tvBrand = itemView.findViewById(R.id.tvUnifiedBrand);
            tvModel = itemView.findViewById(R.id.tvUnifiedModel);
            tvQuality = itemView.findViewById(R.id.tvUnifiedQuality);
            tvBranch = itemView.findViewById(R.id.tvUnifiedBranch);
            tvStock = itemView.findViewById(R.id.tvUnifiedStock);
            tvPrice = itemView.findViewById(R.id.tvUnifiedPrice);
        }
    }
}
