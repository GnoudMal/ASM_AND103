package com.vdsl.myapplication.Account.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vdsl.myapplication.R;
import com.vdsl.myapplication.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ActivityForgotPassword extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");

        binding.btnReset.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString().trim();

            boolean error = false;

            if (email.isEmpty()) {
                binding.edtEmail.setError("Please enter your Email!");
                error = true;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmail.setError("Wrong email format!");
                error = true;
            }

            if (!error) {
                usersRef.whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    Toast.makeText(this, "Email does not exist!", Toast.LENGTH_SHORT).show();
                                } else {
                                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                                            binding.btnReset.setText("Back to sign in");
                                            binding.btnReset.setBackgroundColor(Color.BLACK);
                                            binding.btnReset.setOnClickListener(v1 -> {
                                                startActivity(new Intent(this, ActivitySignIn.class));
                                            });
                                        } else {
                                            if (task1.getException() instanceof FirebaseAuthInvalidUserException) {
                                                Toast.makeText(this, "Email address not found", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(this, "Error checking email existence", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }
}