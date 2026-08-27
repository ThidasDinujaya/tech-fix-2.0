package com.example.management_new.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.management_new.R;
import com.example.management_new.model.SparePart;

import java.util.List;

public class SparePartAdapter extends RecyclerView.Adapter<SparePartAdapter.PartViewHolder> {

    private List<SparePart> list;

    public SparePartAdapter(List<SparePart> list) {
        this.list = list;
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
        holder.tvStock.setText("Stock: " + p.getStock());
        holder.tvPrice.setText(String.format("LKR %.2f", p.getPrice()));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class PartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStock, tvPrice;

        public PartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPartName);
            tvStock = itemView.findViewById(R.id.tvPartStock);
            tvPrice = itemView.findViewById(R.id.tvPartPrice);
        }
    }
}