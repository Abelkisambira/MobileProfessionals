package com.innovations.mobileprofessionals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    Toolbar backTool;
    Button loginBtn;

    TextView Forgot,Reg;
    EditText email,password;

    FirebaseAuth mAuth;

    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        mAuth = FirebaseAuth.getInstance();
        loginBtn=findViewById(R.id.login);
        Forgot=findViewById(R.id.forgot);
        email = findViewById(R.id.email);
        password=findViewById(R.id.pass);
        Reg=findViewById(R.id.reg);
//        progressBar=findViewById(R.id.progressbar);
//        progressBar.setVisibility(View.GONE);

        Forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent1);
                finish();
            }
        });
        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Login.this, Register.class);
                startActivity(intent1);
                finish();
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email=email.getText().toString();
                String Password=password.getText().toString();

                if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password) ){
                    Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;

                }
                mAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    showLoginSuccessDialog();
                                    // Sign in success, update UI with the signed-in user's information
//                                    Toast.makeText(Login.this, "Login Successfull.",
//                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, Nav.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    showLoginFailureDialog();
//                                    // If sign in fails, display a message to the user.
//                                    Toast.makeText(Login.this, "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });




        backTool=findViewById(R.id.back);
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
    private void showLoginSuccessDialog() {
        if (!isFinishing()) {
            new AlertDialog.Builder(this)
                    .setTitle("Login Successful")
                    .setMessage("Welcome back!")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        // Handle OK button click if needed
                    })
                    .show();
        }
    }

    private void showLoginFailureDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Login Failed")
                    .setMessage("Authentication failed. Please check your email and password.")
                    .setPositiveButton("OK", null) // You can add a listener here if needed
                    .show();
        }
    }

//    void setInprogress(boolean inprogress){
//        if (inprogress){
//            progressBar.setVisibility(View.VISIBLE);
//
//        }
//        else{
//            progressBar.setVisibility(View.GONE);
//            loginBtn.setVisibility(View.VISIBLE);
//        }
//    }

}