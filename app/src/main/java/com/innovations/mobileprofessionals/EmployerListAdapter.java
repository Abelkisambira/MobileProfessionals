package com.innovations.mobileprofessionals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class EmployerListAdapter extends ArrayAdapter<ChatEmployer> {

    private OnEmployerClickListener onEmployerClickListener;

    public EmployerListAdapter(Context context, int resource, List<ChatEmployer> employers, OnEmployerClickListener listener) {
        // Constructor implementation
            super(context, resource, employers);
        this.onEmployerClickListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.item, parent, false);
        }

        ChatEmployer employer = getItem(position);
        if (employer != null) {
            TextView nameTextView = view.findViewById(R.id.user_name_text);
            TextView messageTextView = view.findViewById(R.id.last_message_text);
//            ImageView profilePic = view.findViewById(R.id.profile_pic_image_view);

            nameTextView.setText(employer.getEmployerName());
            messageTextView.setText(employer.getMessage());


            view.setOnClickListener(v -> {
                if (onEmployerClickListener != null) {
                    onEmployerClickListener.onEmployerClick(employer);
                }
            });
        }

        return view;
    }

    public interface OnEmployerClickListener {
        void onEmployerClick(ChatEmployer employer);
    }
}
