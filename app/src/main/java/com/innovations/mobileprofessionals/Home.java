package com.innovations.mobileprofessionals;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends Fragment {
    private EditText nameEditText;
    private DatabaseReference usersRef;
    private CardView pending,cancel,edit,logout;
    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        nameEditText = rootView.findViewById(R.id.name);
        pending=rootView.findViewById(R.id.pendingCard);
        cancel=rootView.findViewById(R.id.cancelCard);
        edit=rootView.findViewById(R.id.editCard);
        logout=rootView.findViewById(R.id.logoutCard);
        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with MyBookings fragment
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.bottom_nav_fragment_container, new MyBookings())
                        .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                        .commit();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with MyBookings fragment
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.bottom_nav_fragment_container, new MyBookings())
                        .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                        .commit();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with Accoount fragment
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.bottom_nav_fragment_container, new Accoount())
                        .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                        .commit();
            }
        });

//
//
//        pending.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent= new Intent(getContext(),MyBookings.class);
//                        startActivity(intent);
//                    }
//                });
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent= new Intent(getContext(), MyBookings.class);
//                startActivity(intent);
//            }
//        });
//        edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent= new Intent(getActivity(), Accoount.class);
//                startActivity(intent);
//            }
//        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out the current user
                FirebaseAuth.getInstance().signOut();

                // Clear the user session in shared preferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Mobile Professionals", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Navigate to the login activity
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });



        // Initialize the database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Professionals");

        // Get the current user's ID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String professionalId = currentUser.getUid();

            usersRef.child(professionalId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);


                        nameEditText.setText(username);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            nameEditText.setText("User not authenticated");
        }
return rootView;
    }

}