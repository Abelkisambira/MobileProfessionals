package com.innovations.mobileprofessionals;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class Register extends AppCompatActivity {
    Toolbar backTool;
    Button registerBtn;

    DatabaseReference dbRef;

    EditText name, email, phone, password;
    TextView backToLog;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        backToLog = findViewById(R.id.backTolog);

        registerBtn = findViewById(R.id.register);
        name = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phoneholder);
        password = findViewById(R.id.password);

        dbRef = FirebaseDatabase.getInstance().getReference("Professionals");

        backToLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Register.this, Login.class);
                startActivity(intent1);
                finish();
            }
        });

//        registerBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String username = name.getText().toString();
//                String Email = email.getText().toString();
//                String Phone = phone.getText().toString();
//                String Password = password.getText().toString();
//
//                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(Email) || TextUtils.isEmpty(Phone) || TextUtils.isEmpty(Password)) {
//                    Toast.makeText(Register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//                    return;
//
//                } else {
//                    mAuth.createUserWithEmailAndPassword(Email, Password)
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    if (task.isSuccessful()) {
//                                        getFCMToken();
//                                        FirebaseUser user = mAuth.getCurrentUser();
//                                        if (user != null) {
//                                            String uid = user.getUid();
//                                            Toast.makeText(Register.this, "Account Created.",
//                                                    Toast.LENGTH_SHORT).show();
//                                            Professionals professional = new Professionals(uid,username, Email, Phone, Password);
//
//                                            // Set the UID for the professional
//                                            professional.setUid(uid);
//
//                                            // Store professional data in the Realtime Database under UID
//                                            dbRef.child(uid).setValue(professional);
//
//                                            Intent intent = new Intent(Register.this, Service.class);
//                                            startActivity(intent);
//                                            finish();
//                                            return;
//                                        }
//                                    } else {
//                                        // If sign in fails, display a message to the user.
//                                        Toast.makeText(Register.this, "Authentication failed.",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                }
//            }
//        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = name.getText().toString();
                String Email = email.getText().toString();
                String Phone = phone.getText().toString();
                String Password = password.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(Email) || TextUtils.isEmpty(Phone) || TextUtils.isEmpty(Password)) {
                    Toast.makeText(Register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;

                }

                // Validate email format
                if (!isValidEmail(Email)) {
                    Toast.makeText(Register.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate phone number format
                if (!isValidPhone(Phone)) {
                    Toast.makeText(Register.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate password strength (you can customize this based on your requirements)
                if (Password.length() < 6) {
                    Toast.makeText(Register.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                // All validations passed, proceed with user creation
                User user = new User(username, Email, Phone, Password);

                mAuth.createUserWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        getFCMToken();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            String uid = user.getUid();
                                            Toast.makeText(Register.this, "Account Created.",
                                                    Toast.LENGTH_SHORT).show();
                                            Professionals professional = new Professionals(uid,username, Email, Phone, Password);

                                            // Set the UID for the professional
                                            professional.setUid(uid);

                                            // Store professional data in the Realtime Database under UID
                                            dbRef.child(uid).setValue(professional);

                                            Intent intent = new Intent(Register.this, Service.class);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        }
                                } else {
                                    // If sign up fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication failed. " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



        backTool = findViewById(R.id.back);
        setSupportActionBar(backTool);

        // Enable the back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        backTool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    // Helper method to validate email format
    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Helper method to validate phone number format
    private boolean isValidPhone(String phone) {
        // You can use a more advanced phone number validation logic here
        return Patterns.PHONE.matcher(phone).matches();
    }
    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get the FCM token
                        String fcmToken = task.getResult();



                        Log.d(TAG, "FCM token: " + fcmToken);
                        // Store the FCM token in the database under the professional's UID
                        if (mAuth.getCurrentUser() != null) {
                            String uid = mAuth.getCurrentUser().getUid();
                            dbRef.child(uid).child("fcmToken").setValue(fcmToken);
                        }
                    }
                });
    }

}
