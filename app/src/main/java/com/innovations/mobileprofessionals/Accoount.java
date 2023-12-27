package com.innovations.mobileprofessionals;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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

    private Button editBtn,logoutBtn,saveBtn;

    private ImageView imageView;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION=123;

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
        imageView=view.findViewById(R.id.imageView);
        editBtn=view.findViewById(R.id.edit);
        saveBtn=view.findViewById(R.id.save);
        saveBtn.setVisibility(View.GONE);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Professionals");

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION);
        }
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog to confirm logout
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout?");

                // Add positive button - user confirms logout
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

                // Add negative button - user cancels logout
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Optionally handle cancel action
                    }
                });

                // Show the AlertDialog
                builder.show();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable editing of EditText fields
                nameEditText.setEnabled(true);
                phoneEditText.setEnabled(true);
                emailEditText.setEnabled(true);
                passwordEditText.setEnabled(true);
                // Switch button visibility
                editBtn.setVisibility(View.GONE);
                saveBtn.setVisibility(View.VISIBLE);


            }

        });
        // Update the data when the Save button is clicked
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable editing
                nameEditText.setEnabled(false);
                phoneEditText.setEnabled(false);
                emailEditText.setEnabled(false);
                passwordEditText.setEnabled(false);
                saveBtn.setVisibility(View.GONE);
                editBtn.setVisibility(View.VISIBLE);

                // Update the data in the database
//                        updateUserData(nameEditText.getText().toString(), phoneEditText.getText().toString());
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

                                        // Load and display the image using Glide
                                        String imageUrl = professionals.getImageUrl();
                                        if (imageUrl != null && !imageUrl.isEmpty()) {
                                            Glide.with(requireContext()).load(imageUrl).into(imageView);
                                        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with image loading or any other functionality
            } else {
                // Permission denied, handle accordingly
                // You may want to show a message to the user or disable functionality that requires this permission
            }
        }
    }





//    private void retrieveAndFillUserData() {
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//        if (currentUser != null) {
//            // Retrieve additional user details from the database
//            dbRef.orderByChild("email").equalTo(currentUser.getEmail())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                    Professionals professionals = snapshot.getValue(Professionals.class);
//
//                                    if (professionals != null) {
//                                        // Fill the EditText fields with user data
//                                        nameEditText.setText(professionals.getUsername());
//                                        phoneEditText.setText(professionals.getPhone());
//                                        emailEditText.setText(professionals.getEmail());
//                                        passwordEditText.setText(professionals.getPassword());
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            // Handle errors
//                        }
//                    });
//        }
//    }
}
