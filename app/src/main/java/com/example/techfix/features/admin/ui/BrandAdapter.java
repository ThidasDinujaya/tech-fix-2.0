package com.example.techfix.features.admin.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.features.admin.data.Brand;
import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private List<Brand> list;
    private OnBrandActionListener listener;

    public interface OnBrandActionListener {
        void onEdit(Brand brand);
        void onDelete(Brand brand);
    }

    public BrandAdapter(List<Brand> list, OnBrandActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand, parent, false);
        return new BrandViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brand b = list.get(position);
        holder.tvName.setText(b.getName());
        holder.tvCategory.setText("Category: " + b.getCategory());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(b));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(b));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class BrandViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory;
        ImageView btnEdit, btnDelete;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBrandName);
            tvCategory = itemView.findViewById(R.id.tvBrandCategory);
            btnEdit = itemView.findViewById(R.id.btnEditBrand);
            btnDelete = itemView.findViewById(R.id.btnDeleteBrand);
        }
    }
}
