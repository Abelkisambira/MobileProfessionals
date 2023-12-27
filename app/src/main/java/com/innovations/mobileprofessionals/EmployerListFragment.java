package com.innovations.mobileprofessionals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class EmployerListFragment extends Fragment {

    private RecyclerView employerRecyclerView;
    private EmployerListAdapter employerListAdapter;
    private List<String> employerIds = new ArrayList<>();

    private FirebaseUser currentUser;

    public EmployerListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employer_list, container, false);

        // Initialize views
        employerRecyclerView = view.findViewById(R.id.employerRecyclerView);

        // Initialize RecyclerView and EmployerListAdapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        employerRecyclerView.setLayoutManager(layoutManager);
        employerListAdapter = new EmployerListAdapter(employerIds, this::onEmployerClicked);
        employerRecyclerView.setAdapter(employerListAdapter);

        // Fetch current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Listen for incoming messages to update the employer list
        listenForIncomingMessages();

        return view;
    }

    private void listenForIncomingMessages() {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String employerId = snapshot.child("employerID").getValue(String.class);

                if (employerId != null && !employerIds.contains(employerId)) {
                    employerIds.add(employerId);
                    employerListAdapter.notifyDataSetChanged();
                }
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

            // Other ChildEventListener methods...

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle database errors (if needed)
            }
        });
    }

    private void onEmployerClicked(String employerId) {
        // Open the ProfessionalsChats fragment with the selected employer
        openProfessionalsChatsFragment(employerId);
    }

    private void openProfessionalsChatsFragment(String employerId) {
        // Create a new instance of the ProfessionalsChats fragment and pass the employerId
        ProfessionalsChats fragment = new ProfessionalsChats();
        Bundle args = new Bundle();
        args.putString("employerId", employerId);
        fragment.setArguments(args);

        // Replace the current fragment with the ProfessionalsChats fragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.bottom_nav_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
