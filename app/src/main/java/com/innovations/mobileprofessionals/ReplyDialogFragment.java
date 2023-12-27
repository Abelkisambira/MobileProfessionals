package com.innovations.mobileprofessionals;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ReplyDialogFragment extends DialogFragment {

    private static final String ARG_MESSAGE = "message";

    public static ReplyDialogFragment newInstance(String message) {
        ReplyDialogFragment fragment = new ReplyDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String message = getArguments().getString(ARG_MESSAGE);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Reply to Message")
                .setMessage("Original Message: " + message)
                .setPositiveButton("Send Reply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle sending the reply
                        // Implement your logic here...
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancelled, do nothing
                    }
                });

        return builder.create();
    }
}
