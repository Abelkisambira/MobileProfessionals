package com.innovations.mobileprofessionals;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Chats extends Fragment {

    private ListView employerListView;
    private EmployerListAdapter employerListAdapter;
    private List<ChatEmployer> chatEmployers = new ArrayList<>();

    public Chats() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        // Initialize views and adapters
        employerListView = view.findViewById(R.id.employerListView);
        employerListAdapter = new EmployerListAdapter(getContext(), R.layout.item, chatEmployers, new EmployerListAdapter.OnEmployerClickListener() {
            @Override
            public void onEmployerClick(ChatEmployer employer) {
                // Handle employer click
                openChattingActivity(employer);
            }
        });
        employerListView.setAdapter(employerListAdapter);

        // Set a click listener for the employer list items
        employerListView.setOnItemClickListener((parent, view1, position, id) -> {
            // Handle item click, open Chatting Activity
            ChatEmployer selectedEmployer = chatEmployers.get(position);
            openChattingActivity(selectedEmployer);
        });

        // Listen for incoming messages and update employer list accordingly
        listenForIncomingMessages();

        return view;
    }


    private void listenForIncomingMessages() {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                // Update employer list based on incoming messages
                updateEmployerList(snapshot);
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }
    private void updateEmployerList(DataSnapshot snapshot) {
        // Extract relevant data from snapshot
        String employerID = snapshot.child("employerID").getValue(String.class);
        String employerName = snapshot.child("employerName").getValue(String.class);
        String employerProfilePicUrl = snapshot.child("employerProfilePicUrl").getValue(String.class);
        String message = snapshot.child("message").getValue(String.class);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        // Adjust to use professional's ID
        String professionalId=currentUser.getUid();
        // Check if the sender is an employer (not a professional)
        if (employerID != null && employerName != null && !employerID.equals(professionalId)) {
            // Check if the sender is already in the list
            boolean employerExists = false;
            for (ChatEmployer existingEmployer : chatEmployers) {
                if (existingEmployer != null && existingEmployer.getEmployerID() != null && employerID.equals(existingEmployer.getEmployerID())) {
                    existingEmployer.setMessage(message);
                    employerExists = true;
                    break;
                }
            }

            // If the sender is not in the list, add them
            if (!employerExists) {
                ChatEmployer newSender = new ChatEmployer(employerID, employerName, employerProfilePicUrl, message, false);
                chatEmployers.add(newSender);
                employerListAdapter.notifyDataSetChanged();
            }
        }
    }






    private void openChattingActivity(ChatEmployer selectedEmployer) {
        String employerID  = selectedEmployer.getEmployerID();
        String employerName  = selectedEmployer.getEmployerName();


        // Navigate to the login activity
        Intent intent = new Intent(getActivity(), Chatting.class);
        intent.putExtra("employerID", employerID);
        intent.putExtra("employerName", employerName);
        startActivity(intent);
    }
}
