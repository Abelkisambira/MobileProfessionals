package com.innovations.mobileprofessionals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MyBookings extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private List<Booking> bookingsList;
    private BookingsAdapter bookingsAdapter;

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
        recyclerView.setAdapter(bookingsAdapter);

        // Load bookings data
        loadBookingsDataForProfessional();

        return view;
    }

    private void loadBookingsDataForProfessional() {
        if (currentUser != null) {
            String professionalId = currentUser.getUid();

            DatabaseReference professionalBookingRef = databaseReference.child("professional_bookings").child(professionalId);

            professionalBookingRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    bookingsList.clear();

                    for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                        Booking booking = bookingSnapshot.getValue(Booking.class);
                        if (booking != null) {
                            bookingsList.add(booking);
                        }
                    }

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
}
