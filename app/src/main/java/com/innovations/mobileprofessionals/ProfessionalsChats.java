//package com.innovations.mobileprofessionals;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.RemoteMessage;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ProfessionalsChats extends Fragment {
//
//    private RecyclerView recyclerView;
//    private ChatAdapter chatAdapter;
//    private List<ChatEmployer> chatEmployers = new ArrayList<>();
//    private EditText messageEditText;
//    private ImageButton sendButton;
//    private Toolbar backTool;
//    private FirebaseUser currentUser;
//    private static final String TAG = "Chats";
//
//
//    public ProfessionalsChats() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_professionals_chats, container, false);
//
//        // Initialize views
//        recyclerView = view.findViewById(R.id.recyclerView);
//        messageEditText = view.findViewById(R.id.messageEditText);
//        sendButton = view.findViewById(R.id.sendButton);
//
//        // Initialize RecyclerView and ChatAdapter
//        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
//        recyclerView.setLayoutManager(layoutManager);
//        chatAdapter = new ChatAdapter(chatEmployers);
//        recyclerView.setAdapter(chatAdapter);
//
//        // Listen for incoming messages
//        listenForIncomingMessages();
//
//        // Set a click listener for the send button
//        sendButton.setOnClickListener(v -> sendMessage());
//
//        return view;
//    }
//
//    private void listenForIncomingMessages() {
//        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
//
//        messagesRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
//                handleMessage(snapshot);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                // Handle changes to the existing messages (if needed)
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                // Handle removal of messages (if needed)
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                // Handle movement of messages (if needed)
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Handle database errors (if needed)
//            }
//        });
//    }
//    private void handleMessage(DataSnapshot snapshot) {
//        String messageText = snapshot.child("message").getValue(String.class);
//        String senderID = snapshot.child("employerID").getValue(String.class);
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        // Determine if the message is sent by the current user (professional)
//        boolean sentByProfessional = senderID != null && senderID.equals(currentUser.getUid());
//
//        // Create an instance of ChatEmployer using fields from the snapshot
//        ChatEmployer chatEmployer = new ChatEmployer();
//        chatEmployer.setLastMessage(messageText);
//        chatEmployer.setSentByProfessional(sentByProfessional);
//
//        // Add the ChatEmployer instance to the list
//        addMessage(chatEmployer);
//
//        // Simulate incoming message
//        simulateIncomingMessage();
//    }
//
//    private void sendMessage() {
//        String messageText = messageEditText.getText().toString().trim();
//        if (!messageText.isEmpty()) {
//            DatabaseReference professionalsRef = FirebaseDatabase.getInstance().getReference("Professionals");
//
//            String professionalId = getArguments().getString("uid");
//
//            professionalsRef.child(professionalId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        String professionalFCMToken = dataSnapshot.child("fcmToken").getValue(String.class);
//
//                        if (professionalFCMToken != null && !professionalFCMToken.isEmpty()) {
//                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//
//                            // Adjust to use professional's ID
//                            String senderID = currentUser.getUid();
//                            String senderName = currentUser.getDisplayName();
//
//                            Map<String, String> data = new HashMap<>();
//                            data.put("message", messageText);
//                            data.put("senderName", senderName);
//                            data.put("professionalID", senderID);
//
//                            ChatMessage chatMessage = new ChatMessage(messageText, true);
//                            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
//                            messagesRef.push().setValue(chatMessage);
//
//                            FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(professionalFCMToken)
//                                    .setData(data)
//                                    .build());
//
//                            messageEditText.getText().clear();
//                        } else {
//                            Log.e(TAG, "Professional's FCM token is null or empty");
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Log.e(TAG, "Error fetching professional's FCM token", databaseError.toException());
//                }
//            });
//        }
//    }
//
//    private void simulateIncomingMessage() {
//        recyclerView.postDelayed(() -> {
//            // Simulate an incoming message
//            ChatEmployer incomingMessage = new ChatEmployer();
//            incomingMessage.setLastMessage("Sure, I can help with that.");
//            incomingMessage.setSentByProfessional(false);
//
//            // Add the ChatEmployer instance to the list
//            addMessage(incomingMessage);
//        }, 1000);
//    }
//
//    private void addMessage(ChatEmployer chatEmployer) {
//        chatEmployers.add(chatEmployer);
//        chatAdapter.notifyItemInserted(chatEmployers.size() - 1);
//
//        recyclerView.smoothScrollToPosition(chatEmployers.size() - 1);
//    }
//}