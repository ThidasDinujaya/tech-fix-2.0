package com.example.techfix.features.services;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.techfix.R;
import com.example.techfix.features.services.data.Service;
import java.util.List;

// Adapter to display repair services with web images using Glide
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private final List<Service> services;
    private final OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }

    public ServiceAdapter(List<Service> services, OnServiceClickListener listener) {
        this.services = services;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        holder.bind(services.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final TextView tvName;
        private final TextView tvDesc;
        private final TextView tvPrice;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivServiceIcon);
            tvName = itemView.findViewById(R.id.tvServiceName);
            tvDesc = itemView.findViewById(R.id.tvServiceDesc);
            tvPrice = itemView.findViewById(R.id.tvServicePrice);
        }

        public void bind(final Service service, final OnServiceClickListener listener) {
            tvName.setText(service.getName());
            tvDesc.setText(service.getDescription());
            tvPrice.setText(String.format("LKR %,.2f", service.getPrice()));
            
            // Load web image using Glide with robust caching and error handling
            Glide.with(itemView.getContext())
                .load(service.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert) // Show alert icon if link fails
                .centerCrop()
                .into(ivIcon);

            itemView.setOnClickListener(v -> listener.onServiceClick(service));
        }
    }
}
