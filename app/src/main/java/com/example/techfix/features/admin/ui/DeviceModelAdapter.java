package com.example.techfix.features.admin.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix.R;
import com.example.techfix.features.admin.data.DeviceModel;
import java.util.List;

public class DeviceModelAdapter extends RecyclerView.Adapter<DeviceModelAdapter.ModelViewHolder> {

    private List<DeviceModel> list;
    private OnModelActionListener listener;

    public interface OnModelActionListener {
        void onEdit(DeviceModel model);
        void onDelete(DeviceModel model);
    }

    public DeviceModelAdapter(List<DeviceModel> list, OnModelActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_model, parent, false);
        return new ModelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelViewHolder holder, int position) {
        DeviceModel m = list.get(position);
        holder.tvName.setText(m.getName());
        holder.tvBrand.setText("Brand: " + m.getBrandName());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(m));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(m));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ModelViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBrand;
        ImageView btnEdit, btnDelete;

        public ModelViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvModelName);
            tvBrand = itemView.findViewById(R.id.tvModelBrand);
            btnEdit = itemView.findViewById(R.id.btnEditModel);
            btnDelete = itemView.findViewById(R.id.btnDeleteModel);
        }
    }
}
