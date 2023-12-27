package com.innovations.mobileprofessionals;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EmployerListAdapter extends RecyclerView.Adapter<EmployerListAdapter.EmployerViewHolder> {

    private List<String> employerIds;
    private OnEmployerClickListener onEmployerClickListener;

    public EmployerListAdapter(List<String> employerIds, OnEmployerClickListener listener) {
        this.employerIds = employerIds;
        this.onEmployerClickListener = listener;
    }

    @NonNull
    @Override
    public EmployerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employer, parent, false);
        return new EmployerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployerViewHolder holder, int position) {
        String employerId = employerIds.get(position);
        holder.bind(employerId);
    }

    @Override
    public int getItemCount() {
        return employerIds.size();
    }

    public class EmployerViewHolder extends RecyclerView.ViewHolder {

        private TextView employerNameTextView;

        public EmployerViewHolder(@NonNull View itemView) {
            super(itemView);
            employerNameTextView = itemView.findViewById(R.id.employerNameTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String employerId = employerIds.get(position);
                    onEmployerClickListener.onEmployerClick(employerId);
                }
            });
        }

        public void bind(String employerId) {
            // You can customize this method to display employer details (e.g., name) based on the employerId
            employerNameTextView.setText("Employer ID: " + employerId);
        }
    }

    public interface OnEmployerClickListener {
        void onEmployerClick(String employerId);
    }
}
