package com.example.techfix.features.admin.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.features.admin.data.TechnicianRole;
import java.util.List;

public class TechnicianRoleAdapter extends RecyclerView.Adapter<TechnicianRoleAdapter.RoleViewHolder> {

    private List<TechnicianRole> list;
    private OnRoleActionListener listener;

    public interface OnRoleActionListener {
        void onEdit(TechnicianRole role);
        void onDelete(TechnicianRole role);
    }

    public TechnicianRoleAdapter(List<TechnicianRole> list, OnRoleActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_technician_role, parent, false);
        return new RoleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder, int position) {
        TechnicianRole r = list.get(position);
        holder.tvName.setText(r.getName());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(r));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(r));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class RoleViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView btnEdit, btnDelete;

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRoleName);
            btnEdit = itemView.findViewById(R.id.btnEditRole);
            btnDelete = itemView.findViewById(R.id.btnDeleteRole);
        }
    }
}
