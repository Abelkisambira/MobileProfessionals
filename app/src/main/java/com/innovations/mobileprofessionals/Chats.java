package com.innovations.mobileprofessionals;
import static androidx.fragment.app.FragmentManager.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chats extends Fragment  {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private EditText messageEditText;
    private ImageButton sendButton;
    private Toolbar backTool;
    private FirebaseUser currentUser;
    private static final String TAG = "Chats";



    public Chats() {
        // Required empty public constructor
    }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_chats, container, false);

            // Initialize views
            recyclerView = view.findViewById(R.id.recyclerView);
            messageEditText = view.findViewById(R.id.messageEditText);
            sendButton = view.findViewById(R.id.sendButton);

            // Initialize RecyclerView and ChatAdapter
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            recyclerView.setLayoutManager(layoutManager);
            chatAdapter = new ChatAdapter(chatMessages);
            recyclerView.setAdapter(chatAdapter);

            // Listen for incoming messages
            listenForIncomingMessages();

            // Set a click listener for the send button
            sendButton.setOnClickListener(v -> sendMessage());

            return view;
        }

        private void listenForIncomingMessages() {
            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");

            messagesRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                    handleMessage(snapshot);
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
    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            DatabaseReference professionalsRef = FirebaseDatabase.getInstance().getReference("professionals");
            String professionalId = getArguments().getString("professionalId");

            professionalsRef.child(professionalId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String professionalFCMToken = dataSnapshot.child("fcmToken").getValue(String.class);

                        if (professionalFCMToken != null && !professionalFCMToken.isEmpty()) {
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                            // Adjust to use professional's ID
                            String senderID = currentUser.getUid();
                            String senderName = currentUser.getDisplayName();

                            Map<String, String> data = new HashMap<>();
                            data.put("message", messageText);
                            data.put("senderName", senderName);
                            data.put("professionalID", senderID);

                            ChatMessage chatMessage = new ChatMessage(messageText, true);
                            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
                            messagesRef.push().setValue(chatMessage);

                            FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(professionalFCMToken)
                                    .setData(data)
                                    .build());

                            messageEditText.getText().clear();
                        } else {
                            Log.e(TAG, "Professional's FCM token is null or empty");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error fetching professional's FCM token", databaseError.toException());
                }
            });
        }
    }



    private void handleMessage(DataSnapshot snapshot) {
        String messageText = snapshot.child("message").getValue(String.class);
        String senderID = snapshot.child("professionalID").getValue(String.class);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Determine if the message is sent by the current user (professional)
        boolean sentByProfessional = senderID != null && senderID.equals(currentUser.getUid());

        addMessage(messageText, sentByProfessional);
        simulateIncomingMessage();
    }


//        private void handleMessage(DataSnapshot snapshot) {
//            // Extract the message details from the DataSnapshot
//            String messageText = snapshot.child("message").getValue(String.class);
//            String senderName = snapshot.child("senderName").getValue(String.class);
//
//            // Determine if the message is sent by the employer
//            boolean sentByEmployer = "employer".equals(senderName);
//
//            // Add the received message to your local chatMessages list
//            addMessage(messageText, sentByEmployer);
//
//            // Optionally, simulate a delay and send a response
//            simulateIncomingMessage();
//        }

        private void simulateIncomingMessage() {
            recyclerView.postDelayed(() -> addMessage("Sure, I can help with that.", false), 1000);
        }

        private void addMessage(String message, boolean isSentByEmployer) {
            ChatMessage chatMessage = new ChatMessage(message, isSentByEmployer);
            chatMessages.add(chatMessage);
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);

            recyclerView.smoothScrollToPosition(chatMessages.size() - 1);
        }
    }






//    private RecyclerView recyclerView;
//    private ChatAdapter chatAdapter;
//    private List<ChatMessage> chatMessages = new ArrayList<>();
//    private EditText messageEditText;
//    private ImageButton sendButton;
//
//    public Chats() {
//        // Required empty public constructor
//    }
//    // Callback interface for message received
//    public interface OnMessageReceivedListener {
//        void onMessageReceived(String message);
//    }
//    private OnMessageReceivedListener messageReceivedListener;
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if (context instanceof OnMessageReceivedListener) {
//            messageReceivedListener = (OnMessageReceivedListener) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement OnMessageReceivedListener");
//        }
//    }
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_chats, container, false);
//
//        // Initialize views
//        recyclerView = view.findViewById(R.id.recyclerView);
//        messageEditText = view.findViewById(R.id.messageEditText);
//        sendButton = view.findViewById(R.id.sendButton);
//
//        // Initialize RecyclerView and ChatAdapter
//        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
//        recyclerView.setLayoutManager(layoutManager);
//        chatAdapter = new ChatAdapter(chatMessages);
//        recyclerView.setAdapter(chatAdapter);
//
//        // Listen for incoming messages
//        listenForIncomingMessages();
//
//        // Set a click listener for the send button
//        sendButton.setOnClickListener(v -> sendMessage());
//
//        // You can handle incoming messages from the employer here and add them using addMessage method
//        // For example, addMessage("Hello! How can I assist you?", false);
//
//        return view;
//    }
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
//        super.onMessageReceived(remoteMessage);
//
//        if (remoteMessage.getData().size()>0){
//            String messageText= remoteMessage.getData().get("message");
//            String sender = remoteMessage.getData().get("sender");
//
//            if ("employer".equals(sender)){
//                handleReceivedMessage(messageText);
//            }
//        }
//    }
//
//    private void listenForIncomingMessages() {
//        // Get the reference to the "messages" node in your Firebase Realtime Database
//        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
//
//        // Set up a ChildEventListener to listen for changes in the "messages" node
//        messagesRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
//                // Handle the incoming message
//                handleMessage(snapshot);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
//                // Handle changes to the existing messages (if needed)
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot snapshot) {
//                // Handle removal of messages (if needed)
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
//                // Handle movement of messages (if needed)
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Handle database errors (if needed)
//            }
//        });
//    }
//
//    private void handleMessage(DataSnapshot snapshot) {
//        // Extract the message details from the DataSnapshot
//        String messageText = snapshot.child("message").getValue(String.class);
//        String sender = snapshot.child("sender").getValue(String.class);
//
//        // Determine if the message is sent by the employer or the professional
//        boolean sentByEmployer = "employer".equals(sender);
//
//        // Create a ChatMessage object
//        ChatMessage chatMessage = new ChatMessage(messageText, sentByEmployer);
//
//        // Add the message to your local chatMessages list
//        chatMessages.add(chatMessage);
//
//        // Notify the adapter that the data set has changed
//        chatAdapter.notifyDataSetChanged();
//
//        // Scroll the RecyclerView to the bottom to show the latest message
//        recyclerView.scrollToPosition(chatMessages.size() - 1);
//    }
//
//    private void sendMessage() {
//        String messageText = messageEditText.getText().toString().trim();
//        if (!messageText.isEmpty()) {
//            // Add the sent message to the chat
//            addMessage(messageText, true);
//
//            // For demo purposes, simulate an incoming message after a short delay
//            simulateIncomingMessage();
//        }
//
//        // Clear the message input field
//        messageEditText.setText("");
//    }
//
//    private void simulateIncomingMessage() {
//        recyclerView.postDelayed(() -> addMessage("Sure, I can help with that.", false), 1000);
//    }
//
//    private void addMessage(String message, boolean isSentByEmployer) {
//        ChatMessage chatMessage = new ChatMessage(message, isSentByEmployer);
//        chatMessages.add(chatMessage);
//        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
//
//        // Scroll to the bottom of the RecyclerView to show the latest message
//        recyclerView.smoothScrollToPosition(chatMessages.size() - 1);
//    }
//}
