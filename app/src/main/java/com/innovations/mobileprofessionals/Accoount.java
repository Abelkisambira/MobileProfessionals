package com.innovations.mobileprofessionals;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Accoount extends Fragment {

    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    private Button editBtn,logoutBtn;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;

    public Accoount() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accoount, container, false);

        nameEditText = view.findViewById(R.id.name);
        phoneEditText = view.findViewById(R.id.phoneholder);
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        logoutBtn=view.findViewById(R.id.logout);
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Professionals");
        logoutBtn.setOnClickListener(new View.OnClickListener() {
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
                getActivity().finish();  // Close the current activity
            }
        });

        // Retrieve and fill user data
        retrieveAndFillUserData();


        return view;
    }

    private void retrieveAndFillUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Retrieve additional user details from the database
            dbRef.orderByChild("email").equalTo(currentUser.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Professionals professionals = snapshot.getValue(Professionals.class);

                                    if (professionals != null) {
                                        // Fill the EditText fields with user data
                                        nameEditText.setText(professionals.getUsername());
                                        phoneEditText.setText(professionals.getPhone());
                                        emailEditText.setText(professionals.getEmail());
                                        passwordEditText.setText(professionals.getPassword());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors
                        }
                    });
        }
    }
}
