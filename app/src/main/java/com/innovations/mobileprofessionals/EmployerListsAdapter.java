package com.innovations.mobileprofessionals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EmployerListsAdapter extends ArrayAdapter<ChatEmployer> {

    private Context context;
    private int resource;
    private List<ChatEmployer> employers;

    public EmployerListsAdapter(Context context, int resource, List<ChatEmployer> employers) {
        super(context, resource, employers);
        this.context = context;
        this.resource = resource;
        this.employers = employers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        // Get the current employer
        ChatEmployer employer = employers.get(position);

        // Update UI components with employer information
        TextView nameTextView = convertView.findViewById(R.id.user_name_text);
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);

        nameTextView.setText(employer.getEmployerName());
        messageTextView.setText(employer.getMessage());

        return convertView;
    }
}
