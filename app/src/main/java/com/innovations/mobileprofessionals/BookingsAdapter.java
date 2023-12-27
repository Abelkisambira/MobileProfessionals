package com.innovations.mobileprofessionals;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {

    private List<Booking> bookingsList;
    private OnBookingActionListener actionListener;

    public BookingsAdapter(List<Booking> bookingsList) {
        this.bookingsList = bookingsList;
    }

    public void setOnBookingActionListener(OnBookingActionListener listener) {
        this.actionListener = listener;
    }
//    public BookingsAdapter(List<Booking> bookingsList, OnBookingActionListener actionListener) {
//        this.bookingsList = bookingsList;
//        this.actionListener = actionListener;
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingsList.get(position);

        holder.textViewSeekerName.setText("Seeker Name: " + booking.getSeekerName());
        holder.textViewLocation.setText("Location: " + booking.getSelectedLocation());
        holder.textViewDate.setText("Date: " + booking.getSelectedDate());
        holder.textViewTime.setText("Time: " + booking.getSelectedTime());
        holder.textViewStatus.setText("Status: " + booking.getBookingStatus());


        // Display employer rating
        float employerRating = booking.getEmployerRating();
        holder.textViewEmployerRating.setText("Employer Rating: " + employerRating);
//
//        holder.buttonApprove.setOnClickListener(view -> {
//            if (actionListener != null) {
//                actionListener.onApproveClick(position);
//            }
//        });
//
//        holder.buttonDecline.setOnClickListener(view -> {
//            if (actionListener != null) {
//                actionListener.onDeclineClick(position);
//            }
//        });

        if ("approved".equals(booking.getBookingStatus()) || "cancelled".equals(booking.getBookingStatus())) {
            holder.buttonApprove.setVisibility(View.GONE);
            holder.buttonDecline.setVisibility(View.GONE);
        } else {
            holder.buttonApprove.setVisibility(View.VISIBLE);
            holder.buttonDecline.setVisibility(View.VISIBLE);

            holder.buttonApprove.setOnClickListener(view -> {
                if (actionListener != null) {
                    actionListener.onApproveClick(position);
                }
            });

            holder.buttonDecline.setOnClickListener(view -> {
                if (actionListener != null) {
                    actionListener.onDeclineClick(position);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return bookingsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSeekerName, textViewLocation, textViewDate,textViewTime,textViewStatus,textViewEmployerRating;
        Button buttonApprove, buttonDecline;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewSeekerName = itemView.findViewById(R.id.textViewSeekerName);
            textViewLocation = itemView.findViewById(R.id.locationtext);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
            buttonDecline = itemView.findViewById(R.id.buttonDecline);
            textViewStatus=itemView.findViewById(R.id.bookingStatus);
            textViewEmployerRating=itemView.findViewById(R.id.textViewEmployerRating);

        }
    }
    public interface OnBookingActionListener {
        void onApproveClick(int position);
        void onDeclineClick(int position);
    }

}

