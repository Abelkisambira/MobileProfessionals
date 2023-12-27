package com.innovations.mobileprofessionals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyBookings extends Fragment implements BookingsAdapter.OnBookingActionListener {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private List<Booking> bookingsList;
    private BookingsAdapter bookingsAdapter;

    private Button cancelledButton,allAppointmentsButton,approvedButton;

    TextView noPendingTextView,noApprovedTextView,noCancelledTextView,mybookings;
    public MyBookings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bookings, container, false);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Initialize UI components
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookingsList = new ArrayList<>();
        bookingsAdapter = new BookingsAdapter(bookingsList);
        bookingsAdapter.setOnBookingActionListener(this);
        recyclerView.setAdapter(bookingsAdapter);
         cancelledButton = view.findViewById(R.id.cancelledBtn);
         approvedButton = view.findViewById(R.id.approvedBtn);
         allAppointmentsButton = view.findViewById(R.id.pendingBtn);
         noApprovedTextView=view.findViewById(R.id.noApprovedTextView);
         noCancelledTextView=view.findViewById(R.id.noCancelledTextView);
         noPendingTextView=view.findViewById(R.id.noPendingTextView);
        mybookings=view.findViewById(R.id.mybookings);
        loadBookingsDataForProfessional("pending");

        // For the "Cancelled" button
        cancelledButton.setOnClickListener(v -> {
            loadBookingsDataForProfessional("cancelled");
        });

// For the "Approved" button
        approvedButton.setOnClickListener(v -> {
            loadBookingsDataForProfessional("approved");
        });

// For a button to show all bookings
        allAppointmentsButton.setOnClickListener(v -> {
            loadBookingsDataForProfessional("pending");
        });


        return view;
    }

    private void loadBookingsDataForProfessional(String status) {
        if (currentUser != null) {
            String professionalId = currentUser.getUid();

            DatabaseReference professionalBookingRef = databaseReference.child("professional_bookings");
            professionalBookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    bookingsList.clear();

                    for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                        Booking booking = bookingSnapshot.getValue(Booking.class);
                        if (booking != null && booking.getBookingStatus().equals(status) && professionalId.equals(booking.getProfessionalId())) {
                            // Set bookingId using the unique identifier from Firebase
                            booking.setBookingId(bookingSnapshot.getKey());
                            bookingsList.add(booking);
                        }

                    }
                    updateTextViewVisibility(status,bookingsList.size());
                    bookingsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                    Toast.makeText(requireContext(), "Database Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



//    private void loadBookingsDataForProfessional() {
//        if (currentUser != null) {
//            String professionalId = currentUser.getUid();
//
//
//            DatabaseReference professionalBookingRef = databaseReference.child("professional_bookings").child(professionalId);
//
//            professionalBookingRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    bookingsList.clear();
//
//                    for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
//                        Booking booking = bookingSnapshot.getValue(Booking.class);
//                        if (booking != null) {
//                            bookingsList.add(booking);
//                        }
//                    }
//
//                    bookingsAdapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    // Handle error
//                    Toast.makeText(requireContext(), "Database Error", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }

    @Override
    public void onApproveClick(int position) {
        if (currentUser != null) {
            Booking booking = bookingsList.get(position);
            String bookingId = booking.getBookingId();


            DatabaseReference bookingRef = databaseReference
                    .child("professional_bookings")
                    .child(bookingId);

            bookingRef.child("bookingStatus").setValue("approved");
        }
    }

    @Override
    public void onDeclineClick(int position) {
        if (currentUser != null) {
            Booking booking = bookingsList.get(position);
            String bookingId = booking.getBookingId();


            DatabaseReference bookingRef = databaseReference
                    .child("professional_bookings")
                    .child(bookingId);

            bookingRef.child("bookingStatus").setValue("cancelled");
        }

    }
    private void updateTextViewVisibility(String status, int bookingsCount) {
        switch (status) {
            case "pending":
                noPendingTextView.setVisibility(bookingsCount == 0 ? View.VISIBLE : View.GONE);
                break;
            case "approved":
                noApprovedTextView.setVisibility(bookingsCount == 0 ? View.VISIBLE : View.GONE);
                break;
            case "cancelled":
                mybookings.setVisibility(bookingsCount == 0 ? View.GONE : View.VISIBLE);
                break;
        }
    }

}

