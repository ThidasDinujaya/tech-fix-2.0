package com.example.techfix.features.admin.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.features.admin.data.PartQuality;
import java.util.List;

public class PartQualityAdapter extends RecyclerView.Adapter<PartQualityAdapter.QualityViewHolder> {

    private List<PartQuality> list;
    private OnQualityActionListener listener;

    public interface OnQualityActionListener {
        void onEdit(PartQuality quality);
        void onDelete(PartQuality quality);
    }

    public PartQualityAdapter(List<PartQuality> list, OnQualityActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QualityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_part_quality, parent, false);
        return new QualityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QualityViewHolder holder, int position) {
        PartQuality q = list.get(position);
        holder.tvName.setText(q.getName());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(q));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(q));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class QualityViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView btnEdit, btnDelete;

        public QualityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvQualityName);
            btnEdit = itemView.findViewById(R.id.btnEditQuality);
            btnDelete = itemView.findViewById(R.id.btnDeleteQuality);
        }
    }
}
