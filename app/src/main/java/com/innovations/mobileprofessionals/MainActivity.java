package com.innovations.mobileprofessionals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 100;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(ProgressBar.VISIBLE);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        checkUserSession();
    }

    private void checkUserSession() {
        authStateListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                // User is logged in, navigate to home
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            } else {
                // User is not logged in, navigate to login
                redirectToLogin();
            }
        };

        // Add AuthStateListener
        mAuth.addAuthStateListener(authStateListener);

        // Delay for splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Remove AuthStateListener after the splash screen
                mAuth.removeAuthStateListener(authStateListener);

                // Finish splash screen and move to the next screen
                finish();
            }
        }, SPLASH_SCREEN);
    }

    private void redirectToLogin() {
        // User is not logged in, navigate to login
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }
}
