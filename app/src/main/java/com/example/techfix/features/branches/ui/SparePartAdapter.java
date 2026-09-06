package com.example.techfix.features.branches.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix.R;
import com.example.techfix.features.branches.data.SparePart;

import java.util.List;

public class SparePartAdapter extends RecyclerView.Adapter<SparePartAdapter.PartViewHolder> {

    private List<SparePart> list;
    private OnPartActionListener listener;

    public interface OnPartActionListener {
        void onEdit(SparePart part);
        void onDelete(SparePart part);
    }

    public SparePartAdapter(List<SparePart> list, OnPartActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spare_part, parent, false);
        return new PartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PartViewHolder holder, int position) {
        SparePart p = list.get(position);
        holder.tvName.setText(p.getName());
        holder.tvStock.setText("Stock: " + p.getStock() + " | " + p.getQuality());
        holder.tvPrice.setText(String.format("LKR %,.2f", p.getPrice()));
        
        if (p.getBrand() != null && p.getModel() != null) {
            holder.tvName.setText(p.getName() + " (" + p.getBrand() + " " + p.getModel() + ")");
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(p));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(p));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class PartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStock, tvPrice;
        View btnEdit, btnDelete;

        public PartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPartName);
            tvStock = itemView.findViewById(R.id.tvPartStock);
            tvPrice = itemView.findViewById(R.id.tvPartPrice);
            btnEdit = itemView.findViewById(R.id.btnEditPart);
            btnDelete = itemView.findViewById(R.id.btnDeletePart);
        }
    }
}
