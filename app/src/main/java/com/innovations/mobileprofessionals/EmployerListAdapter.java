package com.innovations.mobileprofessionals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmployerListAdapter extends ArrayAdapter<ChatEmployer> {

    private OnEmployerClickListener onEmployerClickListener;

    public EmployerListAdapter(Context context, int resource, List<ChatEmployer> employers, OnEmployerClickListener listener) {
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
            TextView timeTextView = view.findViewById(R.id.last_message_time_text);

            nameTextView.setText(employer.getEmployerName());
            messageTextView.setText(employer.getMessage());
//
//            // Convert Timestamp to formatted string
//            String formattedTime = timestampToString(employer.getTimestampString());
//            timeTextView.setText(formattedTime);

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

    public static String timestampToString(String timestampString) {
        try {
            // Parse the timestamp string to Date
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(timestampString);

            // Format the date and return it as a string
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
