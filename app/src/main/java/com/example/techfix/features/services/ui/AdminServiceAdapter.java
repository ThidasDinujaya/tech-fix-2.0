package com.example.techfix.features.services.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.techfix.R;
import com.example.techfix.features.services.data.Service;
import java.util.List;

public class AdminServiceAdapter extends RecyclerView.Adapter<AdminServiceAdapter.ServiceViewHolder> {

    private List<Service> list;
    private OnServiceActionListener listener;

    public interface OnServiceActionListener {
        void onEdit(Service service);
        void onDelete(Service service);
    }

    public AdminServiceAdapter(List<Service> list, OnServiceActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_admin, parent, false);
        return new ServiceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service s = list.get(position);
        holder.tvName.setText(s.getName());
        holder.tvPrice.setText(String.format("LKR %,.2f", s.getPrice()));
        holder.tvCategory.setText("Category: " + s.getCategory());

        Glide.with(holder.itemView.getContext())
                .load(s.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivService);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(s));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(s));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvCategory;
        ImageView ivService, btnEdit, btnDelete;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminServiceName);
            tvPrice = itemView.findViewById(R.id.tvAdminServicePrice);
            tvCategory = itemView.findViewById(R.id.tvAdminServiceCategory);
            ivService = itemView.findViewById(R.id.ivAdminService);
            btnEdit = itemView.findViewById(R.id.btnEditService);
            btnDelete = itemView.findViewById(R.id.btnDeleteService);
        }
    }
}
