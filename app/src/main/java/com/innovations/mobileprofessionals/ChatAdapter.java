package com.innovations.mobileprofessionals;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMessage> employerList;
    private String professionalID;

    public ChatAdapter(List<ChatMessage> employerList,String professionalID) {
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
        ChatMessage chatMessage = employerList.get(position);

        if (!chatMessage.isSentByProfessional()) {
            // Customize the appearance of the employer's message
            holder.professionalMessageTextView.setVisibility(View.GONE);
            holder.employerMessageTextView.setVisibility(View.VISIBLE);
            holder.employerNameTextView.setVisibility(View.VISIBLE);

            holder.employerNameTextView.setText(chatMessage.getEmployerName());
            holder.employerMessageTextView.setText(chatMessage.getMessage());

        } else {

            // Customize the appearance of the professional's message
            holder.employerMessageTextView.setVisibility(View.GONE);
            holder.professionalMessageTextView.setVisibility(View.VISIBLE);
            holder.employerNameTextView.setVisibility(View.GONE);

            holder.professionalMessageTextView.setText(chatMessage.getMessage());



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
