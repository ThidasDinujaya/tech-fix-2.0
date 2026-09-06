package com.example.techfix.features.admin.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.features.admin.data.ServiceCategory;
import java.util.List;

public class ServiceCategoryAdapter extends RecyclerView.Adapter<ServiceCategoryAdapter.CategoryViewHolder> {

    private List<ServiceCategory> list;
    private OnCategoryActionListener listener;

    public interface OnCategoryActionListener {
        void onEdit(ServiceCategory category);
        void onDelete(ServiceCategory category);
    }

    public ServiceCategoryAdapter(List<ServiceCategory> list, OnCategoryActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_category, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ServiceCategory c = list.get(position);
        holder.tvName.setText(c.getName());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(c));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(c));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        View btnEdit, btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            btnEdit = itemView.findViewById(R.id.btnEditCategory);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}
