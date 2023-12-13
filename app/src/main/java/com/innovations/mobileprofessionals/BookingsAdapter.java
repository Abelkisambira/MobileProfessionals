package com.innovations.mobileprofessionals;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {

    private List<Booking> bookingsList;

    public BookingsAdapter(List<Booking> bookingsList) {
        this.bookingsList = bookingsList;
    }

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

    }

    @Override
    public int getItemCount() {
        return bookingsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSeekerName, textViewLocation, textViewDate,textViewTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewSeekerName = itemView.findViewById(R.id.textViewSeekerName);
            textViewLocation = itemView.findViewById(R.id.locationtext);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);

        }
    }
}

