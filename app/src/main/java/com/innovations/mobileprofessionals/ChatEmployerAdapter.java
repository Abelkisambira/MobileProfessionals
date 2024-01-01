package com.innovations.mobileprofessionals;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatEmployerAdapter extends RecyclerView.Adapter<ChatEmployerAdapter.ViewHolder> {

    private List<ChatEmployer> employerList;
    private String professionalID;

    public ChatEmployerAdapter(List<ChatEmployer> employerList,String professionalID) {
        this.employerList = employerList;
        this.professionalID=professionalID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatEmployer chatEmployer = employerList.get(position);
        holder.employerNameTextView.setText(chatEmployer.getEmployerName());

        if (chatEmployer.isSentByProfessional()) {
            // Customize the appearance of the professional's message
            holder.employerMessageTextView.setVisibility(View.GONE);
            holder.professionalMessageTextView.setVisibility(View.VISIBLE);
            holder.employerNameTextView.setVisibility(View.GONE);

            holder.professionalMessageTextView.setText(chatEmployer.getMessage());

        } else {
            // Customize the appearance of the employer's message
            holder.professionalMessageTextView.setVisibility(View.GONE);
            holder.employerMessageTextView.setVisibility(View.VISIBLE);
            holder.employerNameTextView.setVisibility(View.VISIBLE);

            holder.employerMessageTextView.setText(chatEmployer.getMessage());

        }
    }


    @Override
    public int getItemCount() {
        return employerList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView employerNameTextView;
        TextView employerMessageTextView;
        TextView professionalMessageTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            employerNameTextView = itemView.findViewById(R.id.employer_username);
            professionalMessageTextView = itemView.findViewById(R.id.professionalMessageTextView);
            employerMessageTextView = itemView.findViewById(R.id.employerMessageTextView);
        }
    }
}
