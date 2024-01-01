package com.innovations.mobileprofessionals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Chatting extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private DatabaseReference messagesRef;
    private String username;
    private TextView name;

    private List<ChatMessage> chatMessages = new ArrayList<>();
    private EditText messageEditText;
    private ImageButton sendButton;
    private ImageButton backTool;
    private FirebaseUser currentUser;
    private String employerID, employerName, professionalID;

    private static final String TAG = "Chatting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        name= findViewById(R.id.other_username);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        messagesRef = database.getReference("messages");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        backTool = findViewById(R.id.back_btn);

        backTool.setOnClickListener((v) -> {
            onBackPressed();
        });

        // Fetch employer ID dynamically
        if (currentUser != null) {
            professionalID = currentUser.getUid();
        }
        // Initialize RecyclerView and ChatAdapter

        // Receive professional details from the intent
        Intent intent = getIntent();
        if (intent != null) {
            employerID = intent.getStringExtra("employerID");
            employerName = intent.getStringExtra("employerName");

            name.setText(employerName);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(chatMessages, professionalID);
        recyclerView.setAdapter(chatAdapter);
        // Listen for incoming messages
        listenForIncomingMessages();

        // Set a click listener for the send button
        sendButton.setOnClickListener(v -> sendMessage());
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
                // Handle changes to the existing messages (if needed)
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle removal of messages (if needed)
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle movement of messages (if needed)
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle database errors (if needed)
            }
        });
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            DatabaseReference professionalsRef = FirebaseDatabase.getInstance().getReference("Professionals");
//            String professionalId = getIntent().getStringExtra("professionalId");
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//            String professionalName = currentUser.getDisplayName();

            Timestamp timestamp = Timestamp.now();
            Date timestampDate = new Date(timestamp.getSeconds() * 1000);


            // Adjust to use professional's ID
            String professionalId = currentUser.getUid();
            professionalsRef.child(professionalId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String professionalFCMToken = dataSnapshot.child("fcmToken").getValue(String.class);
                        String professionalName = dataSnapshot.child("username").getValue(String.class);

                        if (professionalFCMToken != null && !professionalFCMToken.isEmpty() && professionalName !=null && !professionalName.isEmpty()) {
                            String messageKey = professionalsRef.push().getKey();
                            Map<String, String> data = new HashMap<>();
                            data.put("professionalName", professionalName);
                            data.put("username", employerName);
                            data.put("message", messageText);
                            data.put("professionalID", professionalId);
                            data.put("employerID", employerID);
                            data.put("time", String.valueOf(timestampDate));


                            ChatMessage chatMessage = new ChatMessage(messageText, professionalName, employerName, professionalId, employerID, timestampDate, true);
                            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
                            messagesRef.push().setValue(chatMessage);
                            // Send notification to the professional
                            sendNotificationToClients();
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

    private void sendNotificationToClients() {
        DatabaseReference clientsRef = FirebaseDatabase.getInstance().getReference("Clients");
        clientsRef.child(employerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String clientsFCMToken = dataSnapshot.child("fcmToken").getValue(String.class);

                    if (clientsFCMToken != null && !clientsFCMToken.isEmpty()) {
                        // Create a data payload for the notification
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "New Message");
                        data.put("body", "You have a new message from " + currentUser.getDisplayName());

                        // Create the notification payload
                        Map<String, Object> notification = new HashMap<>();
                        notification.put("title", "New Message");
                        notification.put("body", "You have a new message from " + currentUser.getDisplayName());

                        // Create the message payload
                        Map<String, Object> messagePayload = new HashMap<>();
                        messagePayload.put("data", data);
                        messagePayload.put("notification", notification);
                        messagePayload.put("to", clientsFCMToken);

                        // Convert Map to JSONObject
                        JSONObject jsonObject = new JSONObject(messagePayload);

                        // Call the API to send FCM message
                        callAPI(jsonObject);

                    } else {
                        Log.e(TAG, "Professional's FCM token is empty");
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching professional's FCM token", databaseError.toException());
            }
        });
    }
    void callAPI(JSONObject jsonObject){
        okhttp3.MediaType JSON = MediaType.get("application/json");

        OkHttpClient client = new OkHttpClient();
        String url="https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        okhttp3.Request Request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAhyqc8eo:APA91bFRoB_UFl6MGRlF-PXBOytYHYBx3-S-1_tUDXlfPkY-hRBSxgbFeYa9XHFtsu6EH7-rb0Af2ZdsiFcDh2QmeBDrHTd3x2evIuA4DUYXN-PPXa6Y-Y9c4KVI1BsXhbXS9Mj8Hu9s")
                .build();
        client.newCall(Request);
    }
//    private void handleMessage(DataSnapshot snapshot) {
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        // Adjust to use professional's ID
//        String professionalId = currentUser.getUid();
//
//        String senderId = snapshot.child("professionalID").getValue(String.class);
//        String receiverId = snapshot.child("employerID").getValue(String.class);
//        String senderName = snapshot.child("professionalName").getValue(String.class);
//        String messageText = snapshot.child("message").getValue(String.class);
//
//        // Check if the message is relevant to the current chat
//        if (senderId != null && receiverId != null && messageText != null) {
//            boolean isSentByCurrentUser = checkIfSentByCurrentUser(senderId);
//
//            // Create a ChatEmployer object
//            ChatEmployer chatEmployer = new ChatEmployer(senderId, receiverId, senderName, messageText, isSentByCurrentUser);
//
//            // Add the message to your UI
//            addMessage(chatEmployer);
//        }
//    }


    private void handleMessage(DataSnapshot snapshot) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        // Adjust to use professional's ID
//        String professionalId=currentUser.getUid();
//        String professionalName=snapshot.child("professionalName").getValue(String.class);
        String professionalID = snapshot.child("professionalID").getValue(String.class);
        String employerID = snapshot.child("employerID").getValue(String.class);
        employerName = snapshot.child("employerName").getValue(String.class);
        String messageText = snapshot.child("message").getValue(String.class);
        Date timestamp = snapshot.child("time").getValue(Date.class);


        // Check if the message is relevant to the current chat
//        if (employerID != null && professionalID != null && messageText != null) {
            boolean isSentByCurrentUser = checkIfSentByCurrentUser(professionalID);
            String professionalName = getProfessionalName();

            // Create a ChatEmployer object
            ChatMessage chatMessage = new ChatMessage(messageText, professionalName, employerName, professionalID, employerID, timestamp, isSentByCurrentUser);

            // Add the message to your UI
            addMessage(chatMessage);
//        }
//        simulateIncomingMessage();
    }

    private String getProfessionalName() {
        // Initialize the database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        messagesRef = database.getReference("Professionals");

        // Get the current user's ID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            messagesRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String user = dataSnapshot.child("username").getValue(String.class);
                        username = user;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(Chatting.this, "error", Toast.LENGTH_SHORT).show();
                }


            });
        }

        return username;
    }

    private boolean checkIfSentByCurrentUser(String professionalID) {
        // Implement logic to check if the message is sent by the current user
        // Compare with the ID of the current user in your app
        // For example, if you have the FirebaseUser object:
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null && currentUser.getUid().equals(professionalID);
    }


    private void simulateIncomingMessage() {
//        recyclerView.postDelayed(() -> addMessage(new ChatEmployer("1", "John Doe", "url", "Sure, I can help with that.", false)), 1000);
    }

    private void addMessage(ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        if (!chatMessages.isEmpty()) {
            recyclerView.smoothScrollToPosition(chatMessages.size() - 1);
        }
    }
}
